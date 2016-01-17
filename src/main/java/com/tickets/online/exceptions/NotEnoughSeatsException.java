package com.tickets.online.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.CONFLICT, reason="Not enought seats available!")
public class NotEnoughSeatsException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -4137599748827407709L;

}
