package com.tickets.online.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.tickets.online.domain.Booking;
import com.tickets.online.domain.Seat;
import com.tickets.online.domain.SeatHold;
import com.tickets.online.domain.Ticket;

/**
 * The primary interface for all ticket management tasks
 * @author Vinayak_Dhar
 *
 */
public interface TicketService {
  /**
   * The number of seats in the requested level that are neither held nor
   * reserved
   *
   * @param venueLevel
   *          a numeric venue level identifier to limit the search
   * @return the number of tickets available on the provided level
   */
  int numSeatsAvailable(Optional<Integer> venueLevel);

  /**
   * Find and hold the best available seats for a customer
   *
   * @param numSeats
   *          the number of seats to find and hold
   * @param minLevel
   *          the minimum venue level
   * @param maxLevel
   *          the maximum venue level
   * @param customerEmail
   *          unique identifier for the customer
   * @return a SeatHold object identifying the specific seats and related
   *         information
   */
  SeatHold findAndHoldSeats(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel, String customerEmail);

  /**
   * Commit seats held for a specific customer
   *
   * @param seatHoldId
   *          the seat hold identifier
   * @param customerEmail
   *          the email address of the customer to which the seat hold is
   *          assigned
   * @return a reservation confirmation code
   */
  String reserveSeats(int seatHoldId, String customerEmail);

  /**
   * Returns the details of the different types of seats available
   * 
   * @return list of different seats available at the venue
   */
  List<Seat> getSeatsInformation();

  /**
   * Get all bookings in the system
   * 
   * @return list of the bookings in the system
   */
  List<Booking> getAllBookings();

  /**
   * Get list of all tickets in the system
   * 
   * @return list of tickets
   */
  List<Ticket> getAllTickets();

  /**
   * This method is created for interfacing with the controller mapping for seat
   * reservation
   * 
   * @param bookingId
   *          the seat hold identifier
   * @param req
   *          the http request object
   * @return
   */
  String reserveSeats(int bookingId, HttpServletRequest req);

  /**
   * 
   * @param req
   * @return
   */
  SeatHold findAndHoldSeats(HttpServletRequest req);

  /**
   * Get a map of the different seats and their availability
   * 
   * @return map with level_id as key and seats available as value
   */
  Map<Integer, Integer> getSeatAvailabilityMap();

  /**
   * expire bookings which are over the hold time limit
   */
  void bookingExpiry();

}
