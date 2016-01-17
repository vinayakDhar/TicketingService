package com.tickets.online.unit;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tickets.online.dao.TicketsDao;
import com.tickets.online.domain.Seat;
import com.tickets.online.springboot.Application;
import com.tickets.online.tester.AbstractTester;


public class TicketsDaoTest extends AbstractTester {

  private static final Logger log = LoggerFactory.getLogger(Application.class);

  @Autowired
  private TicketsDao ticketDao;

  @Test
  public void testSeatLevelInLevelOne() {
    assertTrue(ticketDao.getAvailableSeatsByLevel(ONE) == LEVEL_ONE_SEATS);
  }

  @Test
  public void testSeatLevelInLevelTwo() {
    assertTrue(ticketDao.getAvailableSeatsByLevel(TWO) == LEVEL_TWO_SEATS);
  }

  @Test
  public void testSeatLevelInLevelThree() {
    assertTrue(ticketDao.getAvailableSeatsByLevel(THREE) == LEVEL_THREE_SEATS);
  }

  @Test
  public void testSeatLevelInLevelFour() {
    assertTrue(ticketDao.getAvailableSeatsByLevel(FOUR) == LEVEL_FOUR_SEATS);
  }

}
