package com.tickets.online.daoimpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.tickets.online.dao.TicketsDao;
import com.tickets.online.domain.Booking;
import com.tickets.online.domain.Seat;
import com.tickets.online.domain.Ticket;
import com.tickets.online.exceptions.DataProcessingException;
import com.tickets.online.rowmappers.BookingRowMapper;
import com.tickets.online.rowmappers.HeldSeatsRowMapper;
import com.tickets.online.rowmappers.SeatRowMapper;
import com.tickets.online.rowmappers.TicketRowMapper;

/**
 * 
 * @author Vinayak_Dhar
 *
 */
@Component
@Configuration
@PropertySource("classpath:ticket.properties")
@ConfigurationProperties(prefix = "ticket")
public class TicketsDaoImpl implements TicketsDao {

  private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private String fetch_seat_by_level;
  private String fetch_seats;

  private String fetch_all_bookings;
  private String fetch_all_unconfirmed_bookings;
  private String fetch_booking_by_id_and_email;
  private String create_booking;
  private String add_tickets_for_booking;
  private String fetch_all_tickets;

  private String fetch_available_tickets_for_level;
  private String fetch_available_tickets_by_level;

  private String confirm_booking_by_id;

  private String fetch_booking_lock;
  private String update_booking_lock;

  private String delete_booking_by_booking_id;
  private String delete_tickets_by_booking_id;

  @Override
  public Seat seatByLevel(final int venueLevel) {
    Seat seat = null;
    seat = jdbcTemplate.queryForObject(fetch_seat_by_level, new Object[] { venueLevel }, new SeatRowMapper());
    return seat;
  }

  @Override
  public List<Seat> getAllSeats() {
    List<Seat> seats = null;
    seats = jdbcTemplate.query(fetch_seats, new SeatRowMapper());
    return seats;
  }

  @Override
  public List<Booking> getAllBookings() {
    List<Booking> bookings = null;
    bookings = jdbcTemplate.query(fetch_all_bookings, new BookingRowMapper());
    return bookings;
  }
  
  @Override
  public List<Booking> getAllUnconfirmedBookings() {
    List<Booking> bookings = null;
    bookings = jdbcTemplate.query(fetch_all_unconfirmed_bookings, new BookingRowMapper());
    return bookings;
  }

  @Override
  public Booking getBookingByIdAndEmail(final int bookingId, final String email) {
    Booking booking = null;
    try {
      log.info("Getting booking details for id : " + bookingId + " email : " + email);
      booking = jdbcTemplate.queryForObject(fetch_booking_by_id_and_email, new Object[] { bookingId, email },
          new BookingRowMapper());
    } catch (DataAccessException e) {
      log.error("data access issue", e);
      throw new DataProcessingException();
    }
    return booking;
  }

  @Override
  public List<Ticket> getAllTickets() {
    List<Ticket> tickets = null;
    tickets = jdbcTemplate.query(fetch_all_tickets, new TicketRowMapper());
    return tickets;
  }

  @Override
  public int createNewBooking(final Booking newBooking) {
    int booking_id = 0;
    KeyHolder keyHolder = new GeneratedKeyHolder();
    PreparedStatementCreator psc = new PreparedStatementCreator() {

      @Override
      public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(create_booking);
        ps.setString(1, newBooking.getEmail());
        return ps;
      }
    };

    jdbcTemplate.update(psc, keyHolder);
    booking_id = keyHolder.getKey().intValue();
    return booking_id;
  }

  @Override
  public void updateTicketsForBooking(final List<Ticket> tickets) {

    int[] result = jdbcTemplate.batchUpdate(add_tickets_for_booking, new BatchPreparedStatementSetter() {

      @Override
      public void setValues(PreparedStatement ps, int i) throws SQLException {
        Ticket ticket = tickets.get(i);
        ps.setLong(1, ticket.getBooking_id());
        ps.setLong(2, ticket.getLevel_id());
        ps.setInt(3, ticket.getCount());
      }

      @Override
      public int getBatchSize() {
        return tickets.size();
      }
    });
  }

  @Override
  public int getAvailableSeatsByLevel(final int level) {
    log.info("Fetching available seats for level " + level);
    return jdbcTemplate.queryForObject(fetch_available_tickets_for_level, new Object[] { level }, Integer.class);
  }

  @Override
  public List<Ticket> getHeldSeatsByLevels() {
    log.info("Fetching all available seats");
    return jdbcTemplate.query(fetch_available_tickets_by_level, new HeldSeatsRowMapper());
  }

  @Override
  public boolean confirmBooking(final long bookingId, final String email, final String confirmationCode) {
    log.info("Confirm booking for id " + bookingId);
    boolean isSuccess = false;
    int affectedRows = jdbcTemplate.update(confirm_booking_by_id,
        new Object[] { true, confirmationCode, bookingId, email });
    isSuccess = affectedRows > 0 ? true : false;
    return isSuccess;
  }
  
  private List<Long> getBookingIntervals() {
    return jdbcTemplate.queryForList("select NOW() FROM bookings", Long.class);
  }
  
  @Override
  public int deleteTicketsByBookingId(final long bookingId) {
    return jdbcTemplate.update(delete_tickets_by_booking_id, new Object[] { bookingId} );
  }
  
  @Override
  public int[] deleteTicketsByBookingId(final List<Long> bookingIds) {
    
    return jdbcTemplate.batchUpdate(delete_tickets_by_booking_id, new BatchPreparedStatementSetter() {

      @Override
      public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setLong(1, bookingIds.get(i));
      }

      @Override
      public int getBatchSize() {
        return bookingIds.size();
      }
    });
  }

  @Override
  public void lockForBooking() {
    jdbcTemplate.execute(fetch_booking_lock);
  }

  @Override
  public int unLockBooking() {
    return jdbcTemplate.update(update_booking_lock);
  }

  public String getFetch_seat_by_level() {
    return fetch_seat_by_level;
  }

  public void setFetch_seat_by_level(String fetch_seat_by_level) {
    this.fetch_seat_by_level = fetch_seat_by_level;
  }

  public String getFetch_seats() {
    return fetch_seats;
  }

  public void setFetch_seats(String fetch_seats) {
    this.fetch_seats = fetch_seats;
  }

  public String getFetch_all_bookings() {
    return fetch_all_bookings;
  }

  public void setFetch_all_bookings(String fetch_all_bookings) {
    this.fetch_all_bookings = fetch_all_bookings;
  }

  public String getFetch_booking_by_id_and_email() {
    return fetch_booking_by_id_and_email;
  }

  public void setFetch_booking_by_id_and_email(String fetch_booking_by_id_and_email) {
    this.fetch_booking_by_id_and_email = fetch_booking_by_id_and_email;
  }

  public String getCreate_booking() {
    return create_booking;
  }

  public void setCreate_booking(String create_booking) {
    this.create_booking = create_booking;
  }

  public String getAdd_tickets_for_booking() {
    return add_tickets_for_booking;
  }

  public void setAdd_tickets_for_booking(String add_tickets_for_booking) {
    this.add_tickets_for_booking = add_tickets_for_booking;
  }

  public String getFetch_all_tickets() {
    return fetch_all_tickets;
  }

  public void setFetch_all_tickets(String fetch_all_tickets) {
    this.fetch_all_tickets = fetch_all_tickets;
  }

  public String getFetch_available_tickets_for_level() {
    return fetch_available_tickets_for_level;
  }

  public void setFetch_available_tickets_for_level(String fetch_available_tickets_for_level) {
    this.fetch_available_tickets_for_level = fetch_available_tickets_for_level;
  }

  public String getFetch_available_tickets_by_level() {
    return fetch_available_tickets_by_level;
  }

  public void setFetch_available_tickets_by_level(String fetch_available_tickets_by_level) {
    this.fetch_available_tickets_by_level = fetch_available_tickets_by_level;
  }

  public String getConfirm_booking_by_id() {
    return confirm_booking_by_id;
  }

  public void setConfirm_booking_by_id(String confirm_booking_by_id) {
    this.confirm_booking_by_id = confirm_booking_by_id;
  }

  public String getFetch_booking_lock() {
    return fetch_booking_lock;
  }

  public void setFetch_booking_lock(String fetch_booking_lock) {
    this.fetch_booking_lock = fetch_booking_lock;
  }

  public String getUpdate_booking_lock() {
    return update_booking_lock;
  }

  public void setUpdate_booking_lock(String update_booking_lock) {
    this.update_booking_lock = update_booking_lock;
  }

  public String getDelete_booking_by_booking_id() {
    return delete_booking_by_booking_id;
  }

  public void setDelete_booking_by_booking_id(String delete_booking_by_booking_id) {
    this.delete_booking_by_booking_id = delete_booking_by_booking_id;
  }

  public String getDelete_tickets_by_booking_id() {
    return delete_tickets_by_booking_id;
  }

  public void setDelete_tickets_by_booking_id(String delete_tickets_by_booking_id) {
    this.delete_tickets_by_booking_id = delete_tickets_by_booking_id;
  }

  public String getFetch_all_unconfirmed_bookings() {
    return fetch_all_unconfirmed_bookings;
  }

  public void setFetch_all_unconfirmed_bookings(String fetch_all_unconfirmed_bookings) {
    this.fetch_all_unconfirmed_bookings = fetch_all_unconfirmed_bookings;
  }

}
