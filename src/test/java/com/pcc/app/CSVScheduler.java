package com.pcc.app;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.testng.TestNG;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CSVScheduler implements Job {

  public void execute(JobExecutionContext context) throws JobExecutionException {
    System.out.println("Starting CSVScheduler" + new Date()+" file :"+LoadRunner.TEST_NG_FILE);
    TestNG testng = new TestNG();
    List<String> suites = new ArrayList<String>();
    suites.add(LoadRunner.TEST_NG_FILE);
    testng.setTestSuites(suites);
    testng.run();
    log.info("Finished process at " + new Date());
  }
}
