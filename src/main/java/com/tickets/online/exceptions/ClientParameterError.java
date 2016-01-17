package com.tickets.online.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Request is invalid! Please check parameters")
public class ClientParameterError extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -6279616380419276246L;

}
