package com.tickets.online.rowmappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.tickets.online.domain.Seat;

public class SeatRowMapper implements RowMapper<Seat> {

  private static final String LEVEL_ID     = "level_id";
  private static final String LEVEL_NAME   = "level_name";
  private static final String PRICE        = "price";
  private static final String ROWS         = "rows";
  private static final String SEATS_IN_ROW = "seats_in_row";

  @Override
  public Seat mapRow(ResultSet rs, int i) throws SQLException {
    // TODO Auto-generated method stub
    return new Seat(rs.getInt(LEVEL_ID), rs.getString(LEVEL_NAME), rs.getDouble(PRICE), rs.getInt(ROWS),
        rs.getInt(SEATS_IN_ROW));
  }

}
