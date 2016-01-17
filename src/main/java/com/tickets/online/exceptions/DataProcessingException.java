package com.tickets.online.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR, reason="Database error!")
public class DataProcessingException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -905042316974721208L;

}
