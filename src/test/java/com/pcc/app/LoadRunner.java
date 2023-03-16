package com.pcc.app;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.testng.TestNG;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoadRunner {
  public static String TEST_NG_FILE = "";
  public static String CRON_SCHEDULE = "";

  public static void main(String[] args) {
    log.info("Starting programm for expression " + args[0] + " for file " + args[1]);
    CRON_SCHEDULE = args[0];
    TEST_NG_FILE = args[1];

    if (CRON_SCHEDULE.equalsIgnoreCase("ONETIME")) {

      System.out.println("Starting CSVScheduler one time" + new Date() + " file :" + TEST_NG_FILE);
      TestNG testng = new TestNG();
      List<String> suites = new ArrayList<String>();
      suites.add(LoadRunner.TEST_NG_FILE);
      testng.setTestSuites(suites);
      testng.run();
      log.info("Finished one time process at " + new Date());

    } else {
      JobDetail csvJob = JobBuilder.newJob(CSVScheduler.class)
          .withIdentity("CSVScheduler", "CSVSchedulerGroup").build();

      Trigger csvTrigger = TriggerBuilder.newTrigger().withIdentity("CSVScheduler", "group1")
          .withSchedule(CronScheduleBuilder.cronSchedule(args[0])).build();

      Scheduler csvSchedule;
      try {
        csvSchedule = new StdSchedulerFactory().getScheduler();
        csvSchedule.start();
        csvSchedule.scheduleJob(csvJob, csvTrigger);
      } catch (SchedulerException e) {
        e.printStackTrace();
        log.info("Error message in LoadRunner" + e.getMessage());
      }
      log.info("ending programm");
    }


  }
}
