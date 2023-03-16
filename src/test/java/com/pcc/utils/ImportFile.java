package com.pcc.utils;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.StreamSupport;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import com.pcc.app.Application;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class ImportFile.
 */
@Slf4j
public class ImportFile extends ImportFileOr {
  WebDriver driver;
  private static final String URL = "https://www25.pointclickcare.com/home/home.jsp?ESOLnewlogin=N";
  private static final String ELEMENT = "//*[@id=\"pccFacLink\"]";
  
  

  /**
   * Instantiates a new import file.
   *
   * @param driver the driver
   */
  public ImportFile(WebDriver driver) {
    this.driver = driver;
    PageFactory.initElements(driver, this);
   // driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
  }

  /**
   * Username.
   *
   * @throws InterruptedException the interrupted exception
   */
  public void username() throws InterruptedException {
    uname.sendKeys(Application.configProps.getProperty("pcc.website.username"));
    Thread.sleep(2000);
    nextbtn.click();
    Thread.sleep(2000);
  }

  /**
   * Password.
   *
   * @throws InterruptedException the interrupted exception
   */
  public void password() throws InterruptedException {
    pwd.sendKeys(Application.configProps.getProperty("pcc.website.password"));
    Thread.sleep(2000);
  }

  /**
   * Submit.
   *
   * @throws InterruptedException the interrupted exception
   */
  public void submit() throws InterruptedException,  AWTException {
    submit.click();
//    driver.get("chrome://settings/content/pdfDocuments");
//    Robot a = new Robot();
//    a.keyPress(KeyEvent.VK_TAB);
//    a.keyRelease(KeyEvent.VK_TAB);
//    a.keyPress(KeyEvent.VK_UP);
//    a.keyRelease(KeyEvent.VK_UP);
//    Thread.sleep(1000);
//    driver.navigate().to("https://www25.pointclickcare.com/home/home.jsp");
    Thread.sleep(7000);

  }

  /**
   * Hovermenu.
   *
   * @throws InterruptedException the interrupted exception
   */
  public void hovermenu() throws InterruptedException {
    Actions ac = new Actions(driver);
    ac.moveToElement(hover).perform();
    Thread.sleep(2000);

  }


  /**
   * Uploadfile.
   *
   * @param filePath the file path
   * @param fileName the file name
   * @throws InterruptedException the interrupted exception
   */
  public void uploadfile(String filePath, String fileName) throws InterruptedException {
    driver.navigate().to("https://www25.pointclickcare.com/glap/ap/processing/invoiceimport.xhtml");
    Thread.sleep(2000);
    uploadfile.sendKeys(filePath);// "C://FTP File//HDG_invout_HDG-2_20201130_TEST3_29-12-2022 13-36-24.csv"
    Thread.sleep(2000);
  }

  /**
   * Split path.
   *
   * @param pathString the path string
   * @return the string[]
   */
  public static String[] splitPath(String pathString) {
    Path path = Paths.get(pathString);
    return StreamSupport.stream(path.spliterator(), false).map(Path::toString)
        .toArray(String[]::new);
  }

  /**
   * Checkfile.
   *
   * @param csvFileName the csv file name
   * @return true, if successful
   * @throws InterruptedException the interrupted exception
   */
  public boolean checkfile(String csvFileName) throws InterruptedException {
 
    log.info("Checking community code in UI for file {}",csvFileName);
    try {

      if (csvFileName != null) {
        String[] csvFileNameArr = csvFileName.split("_");
        if (csvFileNameArr != null) {

          String communityCode = csvFileNameArr[2];
          log.info("Changing community code for  " + communityCode);

          if (Application.HDG_PCC_CODE_MAP.containsKey(communityCode)) {

            String searchText = Application.HDG_PCC_CODE_MAP.getProperty(communityCode);
            String xpathExpression = "//a[text()='" + searchText + "']";

            driver.get(URL);
            driver.findElement(By.xpath(ELEMENT)).click();
            Thread.sleep(2000);
            driver.findElement(By.id("facSearchFilter")).sendKeys(searchText);
            Thread.sleep(2000);
           driver.findElement(By.className("pccButton")).click();
            Thread.sleep(2000);
            driver.findElement(By.xpath(xpathExpression)).click();
            Thread.sleep(6000);
            return true;
          }else {
            Application.UPLOAD_PROCESSING_STATUS.put(csvFileName, "Community code not found in configuration(hdg-pcc-code-mapping.properties)");
          } 
        } 
      } 
    } catch (Exception e) {
      log.info("The file {} contains invalid commmunity code in name", csvFileName);
      Application.UPLOAD_PROCESSING_STATUS.put(csvFileName, "Error while setting community code, check hdg-pcc-code-mapping.properties and pcc web-site");
      //EmailConfig.invalidCommunityCodeInFileName(csvFileName);
    }
    return false;
 
  } 

  /**
   * Loadfile.
   *
   * @throws InterruptedException the interrupted exception
   */
  public void loadfile() throws InterruptedException {
    loadbtn.click();
    Thread.sleep(6000);
  }

  /**
   * Pop up handler.
   *
   * @param csvFileNameWithPath the csv file name with path
   * @param pdfName the pdf name
   * @param fileName the file name
   * @throws Exception the exception
   * @throws AWTException the AWT exception
   */
  public void popUpHandler(String csvFileNameWithPath, String pdfName, String fileName) throws Exception, AWTException {
   log.info("Uploaded file path" + csvFileNameWithPath + " Error report PDF " + pdfName
        + " exc_repo.getAccessibleName() >>> " + exc_repo.getAccessibleName());
    if (exc_repo.getAccessibleName().equalsIgnoreCase("Exceptions Report")) {
      Application.UPLOAD_PROCESSING_STATUS.put(fileName, "Exception report generated");
      exc_repo.click();
      Thread.sleep(10000);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
      Clipboard clipboard = toolkit.getSystemClipboard();
      StringSelection strSel = new StringSelection(pdfName);
      Application.EXCEPTION_REPORTS.add(pdfName);
      clipboard.setContents(strSel, null);

      log.info("Saving pdf step 1");
      Robot rb = new Robot();

      rb.keyPress(KeyEvent.VK_CONTROL);
      rb.keyPress(KeyEvent.VK_P);
      rb.keyRelease(KeyEvent.VK_CONTROL);
      rb.keyRelease(KeyEvent.VK_P);
      Thread.sleep(5000);
      log.info("Saving pdf step 2");
      rb.keyPress(KeyEvent.VK_ENTER);
      rb.keyRelease(KeyEvent.VK_ENTER);
      log.info("Saving pdf step 3");
      Thread.sleep(3000);
      rb.keyPress(KeyEvent.VK_CONTROL);
      rb.keyPress(KeyEvent.VK_V);
      rb.keyRelease(KeyEvent.VK_V);
      rb.keyRelease(KeyEvent.VK_CONTROL);
      Thread.sleep(2000);
      log.info("Saving pdf step 4");
      rb.keyPress(KeyEvent.VK_ENTER);
      rb.keyRelease(KeyEvent.VK_ENTER);
      Thread.sleep(20000);
      
      log.info("Saving pdf step 5");
      rb.keyPress(KeyEvent.VK_CONTROL);
      rb.keyPress(KeyEvent.VK_W);
      log.info("Saving pdf step 6");
      rb.keyRelease(KeyEvent.VK_CONTROL);
      rb.keyRelease(KeyEvent.VK_W);
      log.info("Saving pdf step 7");
      rb.keyPress(KeyEvent.VK_CONTROL);
      rb.keyPress(KeyEvent.VK_SHIFT);
      rb.keyPress(KeyEvent.VK_TAB);
      rb.keyRelease(KeyEvent.VK_CONTROL);
      rb.keyRelease(KeyEvent.VK_SHIFT);
      rb.keyRelease(KeyEvent.VK_TAB);
      log.info("Saving pdf step 8");
      // Send email
      //EmailConfig.sendExceptionReport(pdfName, csvFileNameWithPath,fileName);
      Thread.sleep(5000);
      
      //
    } else if (exc_repo.getAccessibleName().equalsIgnoreCase("Commit")) {
      Thread.sleep(5000);
      commit(csvFileNameWithPath,fileName);
      Thread.sleep(5000);

    } else {
      log.info("Unknown button " + exc_repo.getAccessibleName());
      Application.UPLOAD_PROCESSING_STATUS.put(csvFileNameWithPath, "Unknown button in UI :  " + exc_repo.getAccessibleName());
    }

  }

  /**
   * Commit.
   *
   * @param csvFileNameWithPath the csv file name with path
   * @param csvFileName the csv file name
   * @throws Exception the exception
   */
  public void commit(String csvFileNameWithPath, String csvFileName) throws Exception {

    commit.click();
    Thread.sleep(5000);

    try {
      Thread.sleep(2000);
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      log.info("ALERT_BOX_DETECTED) - ALERT MSG : " + alertText);
      if(alertText != null && alertText.toLowerCase().contains("duplicate invoice found")) {
        log.info(csvFileName+ " : Status : Duplicate invoice message : "+ alertText);
        //EmailConfig.sendDuplicateInvoiceEmail(csvFileNameWithPath,csvFileName,alertText);
        Application.UPLOAD_PROCESSING_STATUS.put(csvFileName, "Status : "+ alertText);
      }else if(alertText != null && alertText.toLowerCase().contains("commit complete")) {
        log.info(csvFileName+ " : Status : Commit Complete message : "+ alertText);
        Application.UPLOAD_PROCESSING_STATUS.put(csvFileName, "Status :"+ alertText);
      }else {
        log.info(csvFileName+ "  Unknown error: "+alertText);
        Application.UPLOAD_PROCESSING_STATUS.put(csvFileName, "Status : "+ alertText);
      }
      
      alert.accept();
      Thread.sleep(2000);
      close.click();
      Thread.sleep(5000);

    } catch (Exception e) {
      log.info("Catch block" + e.getMessage());
      Application.UPLOAD_PROCESSING_STATUS.put(csvFileName, "Status(Exception) : Please verify file in PCC as commit message didn't observed");
    }
  }

}
