package com.tickets.online.rowmappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.tickets.online.domain.Ticket;

public class TicketRowMapper implements RowMapper<Ticket> {

  private static final String BOOKING_ID = "booking_id";
  private static final String LEVEL_ID   = "level_id";
  private static final String COUNT      = "count";

  @Override
  public Ticket mapRow(ResultSet rs, int i) throws SQLException {
    return new Ticket(rs.getLong(BOOKING_ID), rs.getInt(LEVEL_ID), rs.getInt(COUNT));
  }

}
