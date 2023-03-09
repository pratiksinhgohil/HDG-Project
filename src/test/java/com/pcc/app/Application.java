package com.pcc.app;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.pcc.utils.EmailConfig;
import com.pcc.utils.FileValidator;
import com.pcc.utils.FtpConnection;
import com.pcc.utils.ImportFile;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application {

  WebDriver driver;

  public static String FTP_USERNAME = null;
  public static Properties configProps = null;
  public static LocalDateTime CURRENT_TIME = LocalDateTime.now();
  public static String CURRENT_HOUR = null;
  public static String CURRENT_HOUR_FOLDER = null;
  public static String CURRENT_HOUR_FOLDER_VALID_FILES = null;
  public static String CURRENT_HOUR_FOLDER_IN_VALID_FILES = null;

  public static String APP_BASE_PATH = System.getenv("PCC_BASE_PATH");
  public static String ERROR_REPORT_PATH = null;
  public boolean anyValidFile = false;

  public static String EMAIL_SENDER = null;
  public static List<InternetAddress> EMAIL_RECEIVER = new ArrayList<>();
  public ConcurrentHashMap<String, String> CODE_MAPPER = new ConcurrentHashMap<>();
  ChromeOptions option = new ChromeOptions();

  public static ConcurrentHashMap<String, List<String>> LINE_DESC_FILE = new ConcurrentHashMap<>();
  public static ConcurrentHashMap<String,String> UPLOAD_PROCESSING_STATUS = new ConcurrentHashMap<>();
  public static Set<String> EXCEPTION_REPORTS = new HashSet<>();
  public static Properties HDG_PCC_CODE_MAP = new Properties();

  @BeforeClass
  public void setup()
      throws InterruptedException, IOException, AddressException, MessagingException {
    log.info("Starting file processing ");
    configProps = loadProperties();

    EMAIL_SENDER = Application.configProps.getProperty("pcc.mail.sender.email");

    Application.configProps.getProperty("pcc.mail.sender.password");
    String[] receiverEmails =
        Application.configProps.getProperty("pcc.mail.receivers.emailids").split(",");
    for (String receivers : receiverEmails) {
      EMAIL_RECEIVER.add(new InternetAddress(receivers));
    }
    WebDriverManager.chromedriver().setup();
    // WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(3000));
   // driver = new ChromeDriver();
    //System.setProperty("webdriver.chrome.driver", configProps.getProperty("webdriver.chrome.driver"));// "C:/PCC/chrome/chromedriver.exe"
    // Connect to FTP and download files
    CURRENT_HOUR = CURRENT_TIME.getYear() + "//" + CURRENT_TIME.getMonth() + "//"
        + CURRENT_TIME.getDayOfMonth() + "//" + CURRENT_TIME.getHour();
    CURRENT_HOUR_FOLDER = APP_BASE_PATH + "//" + CURRENT_HOUR;
    CURRENT_HOUR_FOLDER_VALID_FILES = CURRENT_HOUR_FOLDER + "//valid";
    CURRENT_HOUR_FOLDER_IN_VALID_FILES = CURRENT_HOUR_FOLDER + "//invalid";

    ERROR_REPORT_PATH = APP_BASE_PATH + "//errorfiles//" + CURRENT_HOUR;
    HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
    chromePrefs.put("profile.default_content_settings.popups", 0);
    chromePrefs.put("download.default_directory", ERROR_REPORT_PATH);

    System.setProperty("webdriver.chrome.silentOutput", "true");
    java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
    System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");

    option.setExperimentalOption("prefs", chromePrefs);
    log.info("Processing for folder > " + CURRENT_HOUR_FOLDER);
    createDir(ERROR_REPORT_PATH);
    prepareCommunityCodeMap();
    log.info("ERROR_REPORT_PATH >> " + ERROR_REPORT_PATH);
    FtpConnection pccFTPConn = new FtpConnection();

    if (pccFTPConn.connect()) {
      if (pccFTPConn.downloadFiles() > 0) {

        FileValidator validator = new FileValidator();
        validator.validateFiles();

        if (validator.hashValidFiles() > 0) {
          anyValidFile = true;
          driver = new ChromeDriver(option);
          driver.manage().window().maximize();
          driver.get(configProps.getProperty("pcc.website"));// "https://www25.pointclickcare.com/home/login.jsp?ESOLGuid=40_1672328090402"
          Thread.sleep(2000);
        }

        if (validator.hashInvalidFiles() > 0) {
          EmailConfig.sendInvalidFiles();
        }

        if (!LINE_DESC_FILE.isEmpty()) {
          EmailConfig.sendLineDescInEmail();
        }

      }
    } else {
      log.info("Issue in FTP connection");
    }
  }

  /*
   * @AfterClass 
   * public void finish() throws InterruptedException {
   * Thread.sleep(2000); driver.close(); }
   */


  @AfterClass
  public void closeBrowser() {
    try {
      if (driver != null) {
        driver.close();
        Runtime.getRuntime().exec("taskkill /F /IM chromedriver*");

      }
    } catch (Exception anException) {
      anException.printStackTrace();
    }
  }

  @Test
  public void Import_File() throws Exception {
    String processingStatus = "";
    if (!anyValidFile) {
      log.info("No files to process , check valid and invalid folders at {} ", CURRENT_HOUR_FOLDER);
      processingStatus = "No files to process for hour " + CURRENT_HOUR;
    } else {

      ImportFile imf = new ImportFile(driver);
      imf.username();
      imf.password();
      imf.submit();
      //imf.hovermenu();
      //imf.ac_pay();
      log.info("Password entered");
      File folder = new File(CURRENT_HOUR_FOLDER_VALID_FILES);
      File[] listOfFiles = folder.listFiles();
      log.info("Got list of files");
      for (File file : listOfFiles) {
        String fileName = file.getName();
        try {
          if (file.isFile()) {
            String fileNameWithPath = file.getCanonicalPath();
            String pdfName = (Application.ERROR_REPORT_PATH + "//" + fileName).replace("//", "\\").replace(".csv", "")+"_"+System.currentTimeMillis()+".pdf";
            log.info("Uploading file " + fileNameWithPath);

            //imf.browse();
            if (imf.checkfile(fileName)) {
              imf.uploadfile(fileNameWithPath,fileName);
              imf.loadfile();
              imf.popUpHandler(fileNameWithPath,pdfName,fileName);
            }

          } else if (file.isDirectory()) {
            log.info(fileName + " is not file");
          }
        } catch (Exception e) {
          log.info("Error while reading file " + fileName);
        }
      }
      processingStatus = "File processing finished for hour " + CURRENT_HOUR;
      log.info("Processing finished");
      //
    }
    log.info("Processing status " + processingStatus);
    EmailConfig.sendProcessiongStatus(processingStatus);
  }

  /*
   * @Test public void FTP_Connection() throws InterruptedException, AWTException
   * { Mail_scheduler.FTP_Connection conn = new Mail_scheduler.FTP_Connection();
   * 
   * conn.con();
   * 
   * }
   * 
   * @Test public void Setup_email() throws InterruptedException, AWTException,
   * IOException { Mail_scheduler.Setup_email email = new
   * Mail_scheduler.Setup_email();
   * 
   * email.emailsent();
   * 
   * }
   */
  @Test
  public void DryRun() throws Exception {

    driver.manage().window().maximize();
    driver.get(configProps.getProperty("pcc.website"));// "https://www25.pointclickcare.com/home/login.jsp?ESOLGuid=40_1672328090402"
    Thread.sleep(2000);

    ImportFile imf = new ImportFile(driver);
    imf.username();
    imf.password();
    imf.submit();
    String filePath = "C:\\PCC\\2023\\JANUARY\\20\\6\\HDG_invout_HDG-2_20201130_TEST3.csv";
    imf.uploadfile(filePath,"HDG_invout_HDG-2_20201130_TEST3.csv");
    imf.loadfile();
    String pdfFileName = (Application.ERROR_REPORT_PATH + "//" + "HDG_invout_HDG-2_20201130_TEST3.csv")
        .replace("//", "\\").replace(".csv", ".pdf");
    imf.popUpHandler(filePath,pdfFileName,"HDG_invout_HDG-2_20201130_TEST3.csv");

  }

  private static Properties loadProperties() {
    if (APP_BASE_PATH == null) {
      log.info("Please set APP_BASE_PATH");
      System.exit(0);
    }
    String path = APP_BASE_PATH + "//pcc.properties";

    try (InputStream input = new FileInputStream(path)) {
      Properties prop = new Properties();
      prop.load(input);
      for (String key : prop.stringPropertyNames()) {
        log.info(key + " >> " + prop.getProperty(key));
      }
      return prop;
    } catch (IOException ex) {
      log.info("Error while reading file " + path);
      ex.printStackTrace();
      return null;
    }
  }

  private static void prepareCommunityCodeMap() {
    if (APP_BASE_PATH == null) {
      log.info("Please set APP_BASE_PATH");
      System.exit(0);
    }
    String path = APP_BASE_PATH + "//hdg-pcc-code-mapping.properties";

    try (InputStream input = new FileInputStream(path)) {
      Properties prop = new Properties();
      prop.load(input);
      for (String key : prop.stringPropertyNames()) {
        log.info("HDG to PCC Code map>>" + key + " >> " + prop.getProperty(key));
      }
      HDG_PCC_CODE_MAP.putAll(prop);
    } catch (IOException ex) {
      log.info("Error while reading file " + path);
      ex.printStackTrace();
    }
  }

  public static void createDir(String path) {
    log.info("Creating folder {}", path);
    File file = new File(path + "//");
    boolean status = file.mkdirs();
    log.info("Error report folder created ::" + status);
  }
}
