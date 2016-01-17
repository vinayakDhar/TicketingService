package com.tickets.online.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR, reason="Unable to Process Request")
public class ServerException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -121837978572159823L;

}
