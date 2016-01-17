package com.tickets.online.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such Seating Level")
public class LevelNotFoundException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -5934471402057651992L;

}
