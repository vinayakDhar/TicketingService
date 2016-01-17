package com.tickets.online.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR, reason="Unable to find seats!")
public class SeatHoldRequestException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -5073503186291052125L;

}
