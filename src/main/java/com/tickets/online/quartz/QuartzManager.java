package com.tickets.online.quartz;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import com.tickets.online.jobs.TicketHoldExpiry;
import com.tickets.online.springboot.AutowiringSpringBeanJobFactory;

/**
 * Manages Quartz configuration for the application
 * 
 * @author Vinayak_Dhar
 *
 */
@Configuration
@PropertySource("classpath:quartz.properties")
@ConfigurationProperties(prefix = "quartz")
public class QuartzManager {

  private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
  
  private long EXPIRY_JOB_INTERVAL;
  private long EXPIRY_JOB_INTERVAL_START_DELAY;

  @Autowired
  private ApplicationContext applicationContext;

  @PostConstruct
  public void init() {
    log.info("Quartz initialized.");
  }

  /**
   * RAM jobs only
   * 
   * @return
   */
  @Bean
  public SchedulerFactoryBean quartzScheduler() {
    
    SchedulerFactoryBean quartzScheduler = new SchedulerFactoryBean();

    // no datasource for now since only ram jobs
    // quartzScheduler.setDataSource(dataSource);
    quartzScheduler.setOverwriteExistingJobs(true);
    quartzScheduler.setSchedulerName("agent-quartz-scheduler");

    // custom job factory for @Autowiring Support
    AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
    jobFactory.setApplicationContext(applicationContext);
    quartzScheduler.setJobFactory(jobFactory);

    quartzScheduler.setQuartzProperties(quartzProperties());

    Trigger[] triggers = { processTicketExpiryTrigger().getObject() };
    quartzScheduler.setTriggers(triggers);

    return quartzScheduler;
  }

  @Bean
  public JobDetailFactoryBean ticketExpiryJob() {
    JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
    jobDetailFactory.setJobClass(TicketHoldExpiry.class);
    jobDetailFactory.setGroup("spring4-quartz");
    return jobDetailFactory;
  }

  @Bean
  public SimpleTriggerFactoryBean processTicketExpiryTrigger() {
    
    SimpleTriggerFactoryBean simpleTriggerFactoryBean = new SimpleTriggerFactoryBean();
    simpleTriggerFactoryBean.setJobDetail(ticketExpiryJob().getObject());
    simpleTriggerFactoryBean.setStartDelay(EXPIRY_JOB_INTERVAL_START_DELAY);
    simpleTriggerFactoryBean.setRepeatInterval(EXPIRY_JOB_INTERVAL);
    simpleTriggerFactoryBean.setGroup("ticketing-quartz");
    return simpleTriggerFactoryBean;
  }

  @Bean
  public Properties quartzProperties() {
    PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
    Properties properties = null;
    try {
      propertiesFactoryBean.afterPropertiesSet();
      properties = propertiesFactoryBean.getObject();
      log.info("testing this property");
      log.info(properties.getProperty("test"));

    } catch (IOException e) {
      log.warn("Cannot load quartz.properties.");
    }

    return properties;
  }

  public long getEXPIRY_JOB_INTERVAL() {
    return EXPIRY_JOB_INTERVAL;
  }

  public void setEXPIRY_JOB_INTERVAL(long eXPIRY_JOB_INTERVAL) {
    EXPIRY_JOB_INTERVAL = eXPIRY_JOB_INTERVAL;
  }

  public long getEXPIRY_JOB_INTERVAL_START_DELAY() {
    return EXPIRY_JOB_INTERVAL_START_DELAY;
  }

  public void setEXPIRY_JOB_INTERVAL_START_DELAY(long eXPIRY_JOB_INTERVAL_START_DELAY) {
    EXPIRY_JOB_INTERVAL_START_DELAY = eXPIRY_JOB_INTERVAL_START_DELAY;
  }

}
