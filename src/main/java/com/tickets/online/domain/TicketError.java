package com.tickets.online.domain;

/**
 * 
 * @author Vinayak_Dhar
 *
 */
public class TicketError {
  
  private int error_code;
  private String error_description;
  
  public TicketError(int error_code, String error_description) {
    this.error_code = error_code;
    this.error_description = error_description;
  }
  
  public int getError_code() {
    return error_code;
  }
  public void setError_code(int error_code) {
    this.error_code = error_code;
  }
  public String getError_description() {
    return error_description;
  }
  public void setError_description(String error_description) {
    this.error_description = error_description;
  }
  
  
}
