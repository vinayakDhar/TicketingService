package com.tickets.online.unit;

import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.tickets.online.domain.SeatHold;
import com.tickets.online.exceptions.BookingExistsException;
import com.tickets.online.exceptions.ClientParameterError;
import com.tickets.online.exceptions.DataProcessingException;
import com.tickets.online.exceptions.LevelNotFoundException;
import com.tickets.online.exceptions.NotEnoughSeatsException;
import com.tickets.online.service.TicketService;
import com.tickets.online.springboot.Application;
import com.tickets.online.tester.AbstractTester;

public class TicketServiceTest extends AbstractTester {
  
  private static final Logger log = LoggerFactory.getLogger(Application.class);
  
  @Autowired
  TicketService ticketService;
  
  @Test
  public void totalSeatsAvailable() {
    log.info(" seats  " + ticketService.numSeatsAvailable(Optional.empty()));
    assertTrue(ticketService.numSeatsAvailable(Optional.empty()) == LEVEL_ALL_SEATS);
  }
  
  @Test
  public void levelOneSeatsAvailable() {
    log.info(" seats  " + ticketService.numSeatsAvailable(Optional.of(ONE)));
    assertTrue(ticketService.numSeatsAvailable(Optional.of(ONE)) == LEVEL_ONE_SEATS);
  }
  
  @Test
  public void levelTwoSeatsAvailable() {
    log.info(" seats  " + ticketService.numSeatsAvailable(Optional.of(TWO)));
    assertTrue(ticketService.numSeatsAvailable(Optional.of(TWO)) == LEVEL_TWO_SEATS);
  }
  
  @Test
  public void levelThreeSeatsAvailable() {
    log.info(" seats  " + ticketService.numSeatsAvailable(Optional.of(THREE)));
    assertTrue(ticketService.numSeatsAvailable(Optional.of(THREE)) == LEVEL_THREE_SEATS);
  }
  
  @Test
  public void levelFourSeatsAvailable() {
    log.info(" seats  " + ticketService.numSeatsAvailable(Optional.of(FOUR)));
    assertTrue(ticketService.numSeatsAvailable(Optional.of(FOUR)) == LEVEL_FOUR_SEATS);
  }
  
  @Test(expected=LevelNotFoundException.class)
  public void nonExistantLevelSeatsAvailable() {
    log.info(" seats  " + ticketService.numSeatsAvailable(Optional.of(NON_EXISTANT_LEVEL)));
    assertTrue(ticketService.numSeatsAvailable(Optional.of(NON_EXISTANT_LEVEL)) == LEVEL_FOUR_SEATS);
  }
  
  @Test
  public void seatAvailableAfterBooking() {
    int numSeats = 20;
    ticketService.findAndHoldSeats(numSeats, Optional.empty(), Optional.empty(), "test@test.com");
    assertTrue(ticketService.numSeatsAvailable(Optional.of(ONE)) == LEVEL_ONE_SEATS - numSeats);
    assertTrue(ticketService.numSeatsAvailable(Optional.empty()) == LEVEL_ALL_SEATS - numSeats);
  }
  
  @Test
  public void seatAvailableAfterBookingBetweenOneAndFour() {
    int numSeats = 20;
    ticketService.findAndHoldSeats(numSeats, Optional.of(ONE), Optional.of(FOUR), "test@test.com");
    assertTrue(ticketService.numSeatsAvailable(Optional.of(ONE)) == LEVEL_ONE_SEATS - numSeats);
    assertTrue(ticketService.numSeatsAvailable(Optional.empty()) == LEVEL_ALL_SEATS - numSeats);
  }
  
  
  @Test
  public void seatAvailableAfterBookingBetweenTwoAndThree() {
    int numSeats = 20;
    ticketService.findAndHoldSeats(numSeats, Optional.of(TWO), Optional.of(THREE), "test@test.com");
    assertTrue(ticketService.numSeatsAvailable(Optional.of(TWO)) == LEVEL_TWO_SEATS - numSeats);
    assertTrue(ticketService.numSeatsAvailable(Optional.empty()) == LEVEL_ALL_SEATS - numSeats);
  }
  
  @Test
  public void seatAvailableAfterBookingBetweenThreeAndThree() {
    int numSeats = 200;
    ticketService.findAndHoldSeats(numSeats, Optional.of(THREE), Optional.of(THREE), "test@test.com");
    assertTrue(ticketService.numSeatsAvailable(Optional.of(THREE)) == LEVEL_THREE_SEATS - numSeats);
    assertTrue(ticketService.numSeatsAvailable(Optional.empty()) == LEVEL_ALL_SEATS - numSeats);
  }
  
  
  @Test
  public void seatAvailableAfterBookingBetweenOneAndThreeWithRollOver() {
    int numSeats = 2000;
    ticketService.findAndHoldSeats(numSeats, Optional.of(ONE), Optional.of(THREE), "test@test.com");
    assertTrue(ticketService.numSeatsAvailable(Optional.of(ONE)) == 0);
    assertTrue(ticketService.numSeatsAvailable(Optional.of(TWO)) == LEVEL_TWO_SEATS - 750);
    assertTrue(ticketService.numSeatsAvailable(Optional.empty()) == LEVEL_ALL_SEATS - numSeats);
  }
  
  @Test
  public void seatAvailableAfterBookingBetweenTwoAndFourWithRollOver() {
    int numSeats = 3700;
    ticketService.findAndHoldSeats(numSeats, Optional.of(TWO), Optional.of(FOUR), "test@test.com");
    assertTrue(ticketService.numSeatsAvailable(Optional.of(TWO)) == 0);
    assertTrue(ticketService.numSeatsAvailable(Optional.of(THREE)) == 0);
    assertTrue(ticketService.numSeatsAvailable(Optional.of(FOUR)) == LEVEL_FOUR_SEATS - 200);
    assertTrue(ticketService.numSeatsAvailable(Optional.empty()) == LEVEL_ALL_SEATS - numSeats);
  }
  
  @Test
  public void isBookingSuccessful() {
    int numSeats = 10;
    SeatHold seatHoldId = ticketService.findAndHoldSeats(numSeats, Optional.of(ONE), Optional.of(FOUR), "test@test.com");
    assertTrue(ticketService.getAllBookings().get(0).getBooking_id() == seatHoldId.getSeatHoldId());
  }
  
  
  @Test(expected=ClientParameterError.class)
  public void isBookingFailureDetected() {
    int numSeats = 0;
    SeatHold seatHoldId = ticketService.findAndHoldSeats(numSeats, Optional.of(ONE), Optional.of(FOUR), "test@test.com");
  }
  
  @Test(expected=NotEnoughSeatsException.class)
  public void checkForOverBooking() {
    int numSeats = 15000;
    SeatHold seatHoldId = ticketService.findAndHoldSeats(numSeats, Optional.of(ONE), Optional.of(ONE), "test@test.com");
  }
  
  @Test(expected=ClientParameterError.class)
  public void invalidEmailInBooking() {
    int numSeats = 150;
    SeatHold seatHoldId = ticketService.findAndHoldSeats(numSeats, Optional.of(ONE), Optional.of(ONE), "test@test");
  }
  
  @Test
  public void reserveSeats() {
    int numSeats = 150;
    String email = "test@test.com";
    SeatHold seatHoldId = ticketService.findAndHoldSeats(numSeats, Optional.of(ONE), Optional.of(ONE), email);
    String confirmationCode = ticketService.reserveSeats(seatHoldId.getSeatHoldId(), email);
    assertTrue(confirmationCode.length() > 10);
    log.info("confirmation code " + confirmationCode);
  }
  
  @Test(expected=BookingExistsException.class)
  public void doubleReserveSeatsError() {
    int numSeats = 150;
    String email = "test@test.com";
    SeatHold seatHoldId = ticketService.findAndHoldSeats(numSeats, Optional.of(ONE), Optional.of(ONE), email);
    String confirmationCode = ticketService.reserveSeats(seatHoldId.getSeatHoldId(), email);
    confirmationCode = ticketService.reserveSeats(seatHoldId.getSeatHoldId(), email);
  }
  
  @Test(expected=DataProcessingException.class)
  public void nonExistantReserveSeats() {
    int numSeats = 150;
    String email = "test@test.com";
    SeatHold seatHoldId = ticketService.findAndHoldSeats(numSeats, Optional.of(ONE), Optional.of(ONE), email);
    String confirmationCode = ticketService.reserveSeats(seatHoldId.getSeatHoldId() - 1, email);
  }
  
  @Test(expected=DataProcessingException.class)
  public void invalidDetailsReserveSeats() {
    int numSeats = 150;
    String email = "test@test.com";
    String invalidEmail = "invalid@test.com";
    SeatHold seatHoldId = ticketService.findAndHoldSeats(numSeats, Optional.of(ONE), Optional.of(ONE), email);
    String confirmationCode = ticketService.reserveSeats(seatHoldId.getSeatHoldId(), invalidEmail);
  }
  
  
}
