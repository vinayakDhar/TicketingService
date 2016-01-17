package com.tickets.online.rowmappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.tickets.online.domain.Ticket;

public class HeldSeatsRowMapper implements RowMapper<Ticket>{

  private static final String LEVEL_ID   = "level_id";
  private static final String COUNT      = "count";

  @Override
  public Ticket mapRow(ResultSet rs, int i) throws SQLException {
    return new Ticket(rs.getInt(LEVEL_ID), rs.getInt(COUNT));
  }

}
