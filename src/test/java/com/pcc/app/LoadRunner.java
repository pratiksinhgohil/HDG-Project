package com.pcc.app;

import java.util.ArrayList;
import java.util.List;
import org.testng.TestNG;

public class LoadRunner {

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    TestNG testng = new TestNG();
    
    List<String> suites=new ArrayList<String>();
    suites.add("C:\\PCC\\CODE\\HDG-Project\\testng.xml");
    testng.setTestSuites(suites);
    testng.run();
  }

}
