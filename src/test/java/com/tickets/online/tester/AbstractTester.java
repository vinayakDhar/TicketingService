package com.tickets.online.tester;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tickets.online.domain.Seat;
import com.tickets.online.springboot.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringApplicationConfiguration(classes = Application.class)
public abstract class AbstractTester {

  private static final Logger log = LoggerFactory.getLogger(Application.class);
  
  @Autowired
  private JdbcTemplate jdbcTemplate;
  
  protected final int ONE   = 1;
  protected final int TWO   = 2;
  protected final int THREE = 3;
  protected final int FOUR  = 4;
  protected final int NON_EXISTANT_LEVEL = 9999;

  protected final int LEVEL_ONE_SEATS   = 1250;
  protected final int LEVEL_TWO_SEATS   = 2000;
  protected final int LEVEL_THREE_SEATS = 1500;
  protected final int LEVEL_FOUR_SEATS  = 1500;
  protected final int LEVEL_ALL_SEATS  = 6250;

  @Before
  public void setupDB() {
    log.info("Creating in-memory tables");

    jdbcTemplate.execute("DROP TABLE seats IF EXISTS");
    jdbcTemplate.execute("CREATE TABLE seats("
        + "level_id Integer UNSIGNED NOT NULL UNIQUE, level_name VARCHAR(255), price DOUBLE, rows Integer, seats_in_row Integer)");

    Object[] orchestra = { 1, "Orchestra", 100.00f, 25, 50 };
    Object[] main = { 2, "Main", 75.00, 20, 100 };
    Object[] balcony_1 = { 3, "Balcony 1", 50.00f, 15, 100 };
    Object[] balcony_2 = { 4, "Balcony 2", 40.00f, 15, 100 };

    List<Object[]> seats = Arrays.asList(orchestra, main, balcony_1, balcony_2);

    // Uses JdbcTemplate's batchUpdate operation to bulk load data
    jdbcTemplate.batchUpdate("INSERT INTO seats(level_id, level_name, price, rows, seats_in_row) VALUES (?,?,?,?,?)",
        seats);

    // Query data for test purpose
    jdbcTemplate.query("SELECT level_id, level_name, price, rows, seats_in_row FROM seats",
        (rs, rowNum) -> new Seat(rs.getInt("level_id"), rs.getString("level_name"), rs.getDouble("price"),
            rs.getInt("rows"), rs.getInt("seats_in_row")))
        .forEach(seat -> log.info(seat.toString()));

    log.info("Create transactional Tables");

    // Booking table
    jdbcTemplate.execute("DROP TABLE bookings IF EXISTS");
    jdbcTemplate.execute("CREATE TABLE bookings("
        + "booking_id Integer UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE, email VARCHAR(255) NOT NULL, time DATETIME DEFAULT NOW(),confirmation Boolean DEFAULT false, confirmation_code VARCHAR(30))");

    // tickets table
    jdbcTemplate.execute("DROP TABLE tickets IF EXISTS");
    jdbcTemplate.execute("CREATE TABLE tickets("
        + "booking_id Integer UNSIGNED NOT NULL,level_id Integer UNSIGNED NOT NULL, count Integer UNSIGNED NOT NULL, )");
    jdbcTemplate.execute(
        "ALTER TABLE tickets ADD FOREIGN KEY ( booking_id ) REFERENCES bookings ( booking_id ) ON DELETE CASCADE");

    // sync table
    jdbcTemplate.execute("DROP TABLE sync IF EXISTS");
    jdbcTemplate.execute("CREATE TABLE sync( lock boolean)");
    jdbcTemplate.update("INSERT INTO sync(lock ) VALUES (?)", true);
  }

}
