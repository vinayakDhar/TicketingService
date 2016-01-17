package com.tickets.online.domain;

/**
 * Defines the characteristics of a seat
 * 
 * @author Vinayak_Dhar
 *
 */
public class Seat {

  private int   level_id;
  private String level_name;
  private double price;
  private int    rows;
  private int    seats_in_row;

  /**
   * Seat parameterized constructor
   * 
   * @param id
   *          level id of the seat
   * @param name
   *          the type of the seat
   * @param price
   *          cost per seat in $
   * @param rows
   *          number of rows for this seat
   * @param seats_in_row
   *          number of seats per row
   */
  public Seat(int id, String name, double price, int rows, int seats_in_row) {
    this.level_id = id;
    this.level_name = name;
    this.price = price;
    this.rows = rows;
    this.seats_in_row = seats_in_row;
  }

  /**
   * Over
   * 
   * @return
   */
  @Override
  public String toString() {
    return String.format("Seat[LevelId=%d, LevelName='%s', Price=$'%.2f', Rows='%d', Seats in Row='%d']", level_id,
        level_name, price, rows, seats_in_row);
  }

  public int getLevel_id() {
    return level_id;
  }

  public void setLevel_id(int level_id) {
    this.level_id = level_id;
  }

  public String getLevel_name() {
    return level_name;
  }

  public void setLevel_name(String level_name) {
    this.level_name = level_name;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public int getRows() {
    return rows;
  }

  public void setRows(int rows) {
    this.rows = rows;
  }

  public int getSeats_in_row() {
    return seats_in_row;
  }

  public void setSeats_in_row(int seats_in_row) {
    this.seats_in_row = seats_in_row;
  }

}
