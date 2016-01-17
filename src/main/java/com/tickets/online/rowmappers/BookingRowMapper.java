package com.tickets.online.rowmappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.tickets.online.domain.Booking;

public class BookingRowMapper implements RowMapper<Booking> {

  private static final String BOOKING_ID        = "booking_id";
  private static final String EMAIL             = "email";
  private static final String TIME              = "time";
  private static final String CONFIRMATION      = "confirmation";
  private static final String CONFIRMATION_CODE = "confirmation_code";

  @Override
  public Booking mapRow(ResultSet rs, int i) throws SQLException {

    return new Booking(rs.getInt(BOOKING_ID), rs.getString(EMAIL), rs.getTimestamp(TIME), rs.getBoolean(CONFIRMATION),
        rs.getString(CONFIRMATION_CODE));
  }

}
