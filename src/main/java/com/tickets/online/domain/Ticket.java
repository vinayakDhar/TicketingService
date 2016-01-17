package com.tickets.online.domain;

/**
 * 
 * @author Vinayak_Dhar
 *
 */
public class Ticket {

  private long booking_id;
  private int level_id;
  private int  count;

  /**
   * Parameterized constructor
   * 
   * @param booking_id
   * @param level_id
   * @param count
   */
  public Ticket(long booking_id, int level_id, int count) {
    this.booking_id = booking_id;
    this.level_id = level_id;
    this.count = count;
  }
  
  /**
   * Parameterized constructor
   * 
   * @param level_id
   * @param count
   */
  public Ticket(int level_id, int count) {
    this.level_id = level_id;
    this.count = count;
  }

  public long getBooking_id() {
    return booking_id;
  }

  public void setBooking_id(long booking_id) {
    this.booking_id = booking_id;
  }

  public int getLevel_id() {
    return level_id;
  }

  public void setLevel_id(int level_id) {
    this.level_id = level_id;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

}
