package com.tickets.online.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.CONFLICT, reason="Booking already confirmed!")
public class BookingExistsException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 5309235798228836295L;

}
