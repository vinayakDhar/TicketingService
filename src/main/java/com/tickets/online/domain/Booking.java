package com.tickets.online.domain;

import java.sql.Timestamp;

/**
 * 
 * @author Vinayak_Dhar
 *
 */
public class Booking {

  private int      booking_id;
  private String    email;
  private Timestamp time;
  private boolean   confirmation;
  private String    confirmation_code;

  /**
   * 
   * @param email
   * @param time
   */
  public Booking(String email, Timestamp time) {
    this.email = email;
    this.time = time;
  }
  
  /**
   * Parameterized constructor
   * 
   * @param booking_id
   * @param email
   * @param time
   * @param confirmation
   */
  public Booking(int booking_id, String email, Timestamp time, boolean confirmation, String confirmation_code) {
    this.booking_id = booking_id;
    this.email = email;
    this.time = time;
    this.confirmation = confirmation;
    this.confirmation_code = confirmation_code;
  }

  public int getBooking_id() {
    return booking_id;
  }

  public void setBooking_id(int booking_id) {
    this.booking_id = booking_id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Timestamp getTime() {
    return time;
  }

  public void setTime(Timestamp time) {
    this.time = time;
  }

  public boolean isConfirmation() {
    return confirmation;
  }

  public void setConfirmation(boolean confirmation) {
    this.confirmation = confirmation;
  }

  public String getConfirmation_code() {
    return confirmation_code;
  }

  public void setConfirmation_code(String confirmation_code) {
    this.confirmation_code = confirmation_code;
  }

}
