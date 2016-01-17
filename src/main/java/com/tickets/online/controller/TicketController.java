package com.tickets.online.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tickets.online.domain.Booking;
import com.tickets.online.domain.Seat;
import com.tickets.online.domain.SeatHold;
import com.tickets.online.domain.Ticket;
import com.tickets.online.service.TicketService;

/**
 * 
 * @author Vinayak_Dhar
 *
 */
@Controller
public class TicketController {

  private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

  @Autowired
  private TicketService ticketService;

  @RequestMapping(value = "/seats")
  @ResponseBody
  public List<Seat> seatLevels() {
    return ticketService.getSeatsInformation();
  }

  @RequestMapping(value = "/seats/available")
  @ResponseBody
  public int numSeatsAvailable() {
    log.info("Getting available seats");
    return ticketService.numSeatsAvailable(Optional.empty());
  }

  @RequestMapping(value = "/seats/available/{level}")
  @ResponseBody
  public int numSeatsAvailableByLevel(@PathVariable int level) {
    return ticketService.numSeatsAvailable(Optional.of(level));
  }

  @RequestMapping(value = "/bookings")
  @ResponseBody
  public List<Booking> getAllBookings() {
    return ticketService.getAllBookings();
  }

  @RequestMapping(value = "/tickets")
  @ResponseBody
  public List<Ticket> getAllTickeets() {
    return ticketService.getAllTickets();
  }

  @RequestMapping(value = "/bookseats")
  @ResponseBody
  public SeatHold bookSeats(HttpServletRequest req) {
    return ticketService.findAndHoldSeats(req);
  }

  @RequestMapping(value = "/reserve/{bookingId}")
  @ResponseBody
  public String reserveSeats(@PathVariable int bookingId, HttpServletRequest req) {
    return ticketService.reserveSeats(bookingId, req);
  }

  @RequestMapping(value = "/map")
  @ResponseBody
  public Map<Integer, Integer> map(HttpServletRequest req) {
    return ticketService.getSeatAvailabilityMap();
  }

}
