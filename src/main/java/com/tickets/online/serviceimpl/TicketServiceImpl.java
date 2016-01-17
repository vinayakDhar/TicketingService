package com.tickets.online.serviceimpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tickets.online.dao.TicketsDao;
import com.tickets.online.domain.Booking;
import com.tickets.online.domain.Seat;
import com.tickets.online.domain.SeatHold;
import com.tickets.online.domain.Ticket;
import com.tickets.online.exceptions.BookingExistsException;
import com.tickets.online.exceptions.ClientParameterError;
import com.tickets.online.exceptions.DataProcessingException;
import com.tickets.online.exceptions.LevelNotFoundException;
import com.tickets.online.exceptions.NotEnoughSeatsException;
import com.tickets.online.exceptions.SeatHoldRequestException;
import com.tickets.online.exceptions.ServerException;
import com.tickets.online.service.TicketService;

@Component
@Transactional
@Configuration
@PropertySource("classpath:ticket.properties")
@ConfigurationProperties(prefix = "ticket")
public class TicketServiceImpl implements TicketService {

  private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

  // Request Parameters Keys
  private static final String EMAIL     = "email";
  private static final String NUM_SEATS = "numSeats";
  private static final String MIN_LEVEL = "minLevel";
  private static final String MAX_LEVEL = "maxLevel";

  private String CONFIRMATION_PREFIX;
  private int    RANDOM_CODE_LENGTH;
  private long   BOOKING_HOLD_TIME;

  @Autowired
  private TicketsDao ticketDao;

  @Override
  public int numSeatsAvailable(Optional<Integer> venueLevel) {
    int numOfSeatsAvailable = 0;
    try {
      if (venueLevel.isPresent()) {
        numOfSeatsAvailable = getAvailableSeatsByLevel(venueLevel.get());
      } else {
        numOfSeatsAvailable = getAvailableSeats();
      }
    } catch (DataAccessException e) {
      log.error("Database access exception", e);
      throw new LevelNotFoundException();
    } catch (Exception e) {
      log.error("unhandled error", e);
      throw new ServerException();
    }
    return numOfSeatsAvailable;
  }

  @Override
  @Transactional
  public SeatHold findAndHoldSeats(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel,
      String customerEmail) {

    SeatHold seatHold = null;
    
    validateParametersForHold(numSeats, customerEmail);

    // Obtain Booking lock
    ticketDao.lockForBooking();

    // Create a booking for the customer
    Timestamp bookingTime = new Timestamp(System.currentTimeMillis());
    Booking newBooking = new Booking(customerEmail, bookingTime);

    int seatHoldId = ticketDao.createNewBooking(newBooking);

    // Find best ticket type and count for the customer
    List<Ticket> ticketsTypeList = getBestTicketsPossible(seatHoldId, numSeats, minLevel, maxLevel);

    // Add tickets for the customer
    ticketDao.updateTicketsForBooking(ticketsTypeList);

    // Populate SeatHold object to return
    seatHold = new SeatHold(seatHoldId, customerEmail, ticketsTypeList);

    // Release lock ticketDao.unLockBooking();

    return seatHold;
  }

  private void validateParametersForHold(int numSeats, String email) {
    if (numSeats < 1) {
      throw new ClientParameterError();
    }

    if (false == EmailValidator.getInstance().isValid(email)) {
      throw new ClientParameterError();
    }

  }
  

  @Override
  @Transactional
  public String reserveSeats(int seatHoldId, String customerEmail) {
    
    if (false == EmailValidator.getInstance().isValid(customerEmail)) {
      throw new ClientParameterError();
    }
    // Build a confirmation string for performing this reservation
    StringBuilder confirmationCodeBuilder = new StringBuilder(CONFIRMATION_PREFIX)
        .append(RandomStringUtils.randomAlphanumeric(RANDOM_CODE_LENGTH)).append(seatHoldId);
    final String confirmationCode = confirmationCodeBuilder.toString();
    boolean isSuccess = ticketDao.confirmBooking(seatHoldId, customerEmail, confirmationCode);
    if (false == isSuccess) {
      Booking existingBooking = ticketDao.getBookingByIdAndEmail(seatHoldId, customerEmail);
      if (null != existingBooking) {
        log.info("Booking already exists");
        throw new BookingExistsException();
      } else {
        log.info("Something seems wrong here!");
        throw new ServerException();
      }
    }
    return confirmationCode;
  }

  @Override
  public List<Seat> getSeatsInformation() {
    List<Seat> seatsList = null;
    try {
      seatsList = ticketDao.getAllSeats();
    } catch (Exception e) {
      log.error("unhandled error", e);
      throw new ServerException();
    }
    return seatsList;
  }

  /**
   * get the number of seats by the venueLevel
   * 
   * @param venueLevel
   *          the level_id of the seat
   * @return count of the seats available in the level
   */
  private int getAvailableSeatsByLevel(final int venueLevel) {
    int availableSeats = 0;
    availableSeats = ticketDao.getAvailableSeatsByLevel(venueLevel);
    return availableSeats;
  }

  @Override
  public Map<Integer, Integer> getSeatAvailabilityMap() {
    Map<Integer, Integer> seatAvailabilityMap = null;
    List<Ticket> allAvailableSeatsList = ticketDao.getHeldSeatsByLevels();
    seatAvailabilityMap = new HashMap<Integer, Integer>(allAvailableSeatsList.size());
    for (Ticket ticket : allAvailableSeatsList) {
      seatAvailabilityMap.put(ticket.getLevel_id(), ticket.getCount());
    }
    return seatAvailabilityMap;
  }

  /**
   * Calculate the seats available across all tickets
   * 
   * @return count of the tickets available
   */
  private int getAvailableSeats() {
    int allAvailableSeats = 0;
    List<Ticket> allAvailableSeatsList = ticketDao.getHeldSeatsByLevels();
    for (Ticket ticket : allAvailableSeatsList) {
      allAvailableSeats += ticket.getCount();
    }
    return allAvailableSeats;
  }

  /**
   * Tries to find the best tickets possible as per the booking request
   * 
   * @param seatHoldId
   *          the id which is generate for the booking request
   * @param numSeats
   *          number of seats requested by the customer
   * @param minLevel
   *          the min seat level request
   * @param maxLevel
   *          the max seat level requested
   * @return list of the best tickets possible for the request
   */
  private List<Ticket> getBestTicketsPossible(long seatHoldId, int numSeats, Optional<Integer> minLevel,
      Optional<Integer> maxLevel) {

    List<Ticket> ticketList = null;

    int minLevelAvailable = 0;
    int maxLevelAvailable = 0;

    if (minLevel.isPresent()) {
      minLevelAvailable = minLevel.get();
    } else {
      minLevelAvailable = 0;
    }

    if (maxLevel.isPresent()) {
      if (maxLevel.get() < minLevelAvailable) {
        throw new ClientParameterError();
      }
      maxLevelAvailable = maxLevel.get();
    } else {
      maxLevelAvailable = 0;
    }

    log.debug("pre min level " + minLevelAvailable + " max level " + maxLevelAvailable);

    Map<Integer, Integer> seatAvailabilityMap = getSeatAvailabilityMap();

    for (Integer level : seatAvailabilityMap.keySet()) {
      int seatsAvailable = seatAvailabilityMap.get(level);
      if (seatsAvailable > 0) {
        if (minLevelAvailable < 1 || (false == minLevel.isPresent() && minLevelAvailable > level)) {
          minLevelAvailable = level;
        }

        if (maxLevelAvailable < 1 || (false == maxLevel.isPresent() && maxLevelAvailable < level)) {
          maxLevelAvailable = level;
        }

      }
    }

    log.debug("post min level " + minLevelAvailable + " max level " + maxLevelAvailable);

    if (minLevelAvailable == 0 || maxLevelAvailable == 0) {
      throw new DataProcessingException();
    }

    // Generate the best ticket groups possible
    ticketList = new ArrayList<>(maxLevelAvailable);
    int seatsHeld = generateBestTicketList(ticketList, seatHoldId, numSeats, minLevelAvailable, maxLevelAvailable,
        seatAvailabilityMap);

    // Unable to hold seats as requested! booking failed
    if ((true == ticketList.isEmpty()) || (seatsHeld != numSeats)) {
      throw new NotEnoughSeatsException();
    }

    return ticketList;
  }

  /**
   * Logic to get the best seats with the constraints given
   * 
   * @param ticketList
   *          reference to the ticket list
   * @param seatHoldId
   *          the id which is generate for the booking request
   * @param numSeats
   *          number of seats requested by the customer
   * @param minLevel
   *          the min seat level request
   * @param maxLevel
   *          the max seat level requested
   * @param seatAvailabilityMap
   *          the seat availability map
   * @return the number of seats which can be held as per constraint
   */
  private int generateBestTicketList(List<Ticket> ticketList, long seatHoldId, int numSeats, int minLevelAvailable,
      int maxLevelAvailable, Map<Integer, Integer> seatAvailabilityMap) {
    int seatsHeld = 0;
    int seatsToHold = (numSeats - seatsHeld);
    for (int i = minLevelAvailable; i <= maxLevelAvailable; i++) {
      log.debug("Level " + i + " SeatsHeld " + seatsHeld);
      if (seatsHeld == numSeats) {
        break;
      }

      int seatsAvailableInLevel = seatAvailabilityMap.get(i);
      log.debug("Level " + i + " Seats Avail " + seatsAvailableInLevel);
      seatsToHold = (numSeats - seatsHeld);
      if ((seatsAvailableInLevel - (numSeats - seatsHeld)) >= 0) {
        log.debug("All Seats Stat by lvl " + i);
        ticketList.add(new Ticket(seatHoldId, i, seatsToHold));
        seatsHeld += seatsToHold;
        break;
      } else if (seatsAvailableInLevel < 1) {
        log.debug("None Seats Left Stat by lvl " + i);
        continue;
      } else {
        log.info("Some Seats Stat by lvl " + i + " > " + seatsAvailableInLevel);
        seatsToHold = seatsAvailableInLevel;
        ticketList.add(new Ticket(seatHoldId, i, seatsToHold));
        seatsHeld += seatsToHold;
        log.debug("SeatsHeld " + seatsHeld + " numSeats" + numSeats);
        if (seatsHeld == numSeats) {
          break;
        } else if (seatsHeld < numSeats) {
          continue;
        } else {
          throw new SeatHoldRequestException();
        }
      }
    }
    return seatsHeld;
  }

  @Override
  public List<Booking> getAllBookings() {
    return ticketDao.getAllBookings();
  }

  @Override
  public List<Ticket> getAllTickets() {
    return ticketDao.getAllTickets();
  }

  @Override
  public String reserveSeats(int bookingId, HttpServletRequest req) {
    try {
      String customerEmail = req.getParameter(EMAIL);
      return reserveSeats(bookingId, customerEmail);
    } catch (NullPointerException e) {
      log.error("Check the request parameters", e);
      throw new ClientParameterError();
    }
  }

  @Override
  public SeatHold findAndHoldSeats(HttpServletRequest req) {

    Optional<Integer> minLevel = Optional.empty();
    Optional<Integer> maxLevel = Optional.empty();

    int numSeats = Integer.parseInt(req.getParameter(NUM_SEATS));

    String customerEmail = req.getParameter(EMAIL);

    String minLevelParam = req.getParameter(MIN_LEVEL);
    String maxLevelParam = req.getParameter(MAX_LEVEL);

    if (null != minLevelParam) {
      minLevel = Optional.of(Integer.parseInt(minLevelParam));
    }

    if (null != maxLevelParam) {
      maxLevel = Optional.of(Integer.parseInt(maxLevelParam));
    }

    return findAndHoldSeats(numSeats, minLevel, maxLevel, customerEmail);

  }

  @Override
  @Transactional
  public void bookingExpiry() {
    log.info("expire bookings");
    long currentTime = System.currentTimeMillis();
    List<Long> bookingsToExpire = new ArrayList<>();
    ticketDao.lockForBooking();
    List<Booking> bookings = ticketDao.getAllUnconfirmedBookings();
    for (Booking booking : bookings) {
      log.info("difference " + (currentTime - booking.getTime().getTime()));
      if (currentTime - booking.getTime().getTime() >= BOOKING_HOLD_TIME) {
        bookingsToExpire.add(new Long(booking.getBooking_id()));
      }
    }
    log.info("Bookings to expire " + bookingsToExpire);
    ticketDao.deleteTicketsByBookingId(bookingsToExpire);
    ticketDao.unLockBooking();
  }

  public String getCONFIRMATION_PREFIX() {
    return CONFIRMATION_PREFIX;
  }

  public void setCONFIRMATION_PREFIX(String cONFIRMATION_PREFIX) {
    CONFIRMATION_PREFIX = cONFIRMATION_PREFIX;
  }

  public int getRANDOM_CODE_LENGTH() {
    return RANDOM_CODE_LENGTH;
  }

  public void setRANDOM_CODE_LENGTH(int rANDOM_CODE_LENGTH) {
    RANDOM_CODE_LENGTH = rANDOM_CODE_LENGTH;
  }

  public long getBOOKING_HOLD_TIME() {
    return BOOKING_HOLD_TIME;
  }

  public void setBOOKING_HOLD_TIME(long bOOKING_HOLD_TIME) {
    BOOKING_HOLD_TIME = bOOKING_HOLD_TIME;
  }

}
