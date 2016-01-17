package com.tickets.online.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.tickets.online.service.TicketService;

/**
 * Scheduled job to handle the expiry of seat holding
 * 
 * @author Vinayak_Dhar
 *
 */
public class TicketHoldExpiry extends QuartzJobBean {

  private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

  @Autowired
  private TicketService ticketService;

  @Override
  protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
    log.info("Running expiry job");
    ticketService.bookingExpiry();
    log.info("Expired bookings");
  }

}
