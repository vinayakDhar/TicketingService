package com.tickets.online.domain;

import java.util.List;

/**
 * 
 * @author Vinayak_Dhar
 *
 */
public class SeatHold {

  private int         seatHoldId;
  private String       email;
  private List<Ticket> ticketList;
  
  /**
   * 
   * @param seatHoldId
   * @param email
   * @param ticketList
   */
  public SeatHold(int seatHoldId, String email, List<Ticket> ticketList) {
    this.seatHoldId = seatHoldId;
    this.email = email;
    this.ticketList = ticketList;
  }

  public int getSeatHoldId() {
    return seatHoldId;
  }

  public void setSeatHoldId(int seatHoldId) {
    this.seatHoldId = seatHoldId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public List<Ticket> getTicketList() {
    return ticketList;
  }

  public void setTicketList(List<Ticket> ticketList) {
    this.ticketList = ticketList;
  }

}
