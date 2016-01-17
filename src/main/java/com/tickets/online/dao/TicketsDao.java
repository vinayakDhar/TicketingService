package com.tickets.online.dao;

import java.util.List;

import com.tickets.online.domain.Booking;
import com.tickets.online.domain.Seat;
import com.tickets.online.domain.Ticket;

/**
 * The DAO for the ticket database
 * 
 * @author Vinayak_Dhar
 *
 */
public interface TicketsDao {

  /**
   * Get the information of all the seats
   * 
   * @return list of seats
   */
  List<Seat> getAllSeats();

  /**
   * Get the information of Seat for a level
   * 
   * @param venueLevel
   *          the level id for the seat
   * @return the object containing details for the Seat
   */
  Seat seatByLevel(int venueLevel);

  /**
   * Get all possible bookings in the system
   * 
   * @return
   */
  List<Booking> getAllBookings();

  /**
   * Create a new booking
   * 
   * @param newBooking
   *          object containing the booking details
   * @return
   */
  int createNewBooking(Booking newBooking);

  /**
   * Add tickets related to a booking
   * 
   * @param tickets
   *          object containing the ticket details
   */
  void updateTicketsForBooking(List<Ticket> tickets);

  /**
   * Get all tickets in the system
   * 
   * @return lis of all tickets
   */
  List<Ticket> getAllTickets();

  /**
   * get the count of seats available by level
   * 
   * @param level
   *          the level_id of a seat
   * @return
   */
  int getAvailableSeatsByLevel(int level);

  /**
   * Confirm the booking of a customer
   * 
   * @param booking_id
   *          seathold id shared with the customer
   * @param email
   *          customer's email id
   * @param confirmation_code
   *          confirmation code generated by service layer
   * @return if booking confirmation is a success or a failure
   */
  boolean confirmBooking(long booking_id, String email, String confirmation_code);

  /**
   * Get details of a specific booking
   * 
   * @param bookingId
   *          the seat hold id shared with the customer
   * @param email
   *          customer's email id
   * @return the booking object containing information about the booking
   */
  Booking getBookingByIdAndEmail(int bookingId, String email);

  /**
   * Get the aggregated seats count which are held by the level
   * 
   * @return List of ticket object , one each for level
   */
  List<Ticket> getHeldSeatsByLevels();

  /**
   * Get lock for write transactions
   */
  void lockForBooking();

  /**
   * Release lock for write transaction
   * 
   * @return
   */
  int unLockBooking();

  /**
   * Delete a booking by the booking id
   * 
   * @param bookingId
   *          seatHoldId for the booking
   * @return rows affected
   */
  int deleteTicketsByBookingId(long bookingId);

  /**
   * batch delete of bookings by list of ids
   * 
   * @param bookingIds
   *          list of seatHoldId's for the bookings
   * @return rows affected by each query
   */
  int[] deleteTicketsByBookingId(List<Long> bookingIds);

  /**
   * Get a list of all unconfirmed bookings 
   * @return list of bookings which are not confirmed
   */
  List<Booking> getAllUnconfirmedBookings();

}
