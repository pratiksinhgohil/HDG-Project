package com.pcc.app;

import java.util.ArrayList;
import java.util.List;
import org.testng.TestNG;

/**
 * The Class LoadRunner is entry point for class
 */
public class LoadRunner {

  /**
   * The main method is entry of jar file
   *
   * @param args the arguments
   */
  public static void main(String[] args) {
    TestNG testng = new TestNG();
    List<String> suites=new ArrayList<String>();
    suites.add("C:\\PCC\\CODE\\HDG-Project\\testng.xml");
    testng.setTestSuites(suites);
    testng.run();
  }
}
