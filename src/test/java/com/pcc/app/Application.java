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

/**
 * The Class Application in main class for CSV processor
 * 
 * Refer process flow diagram how it works
 */
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
  public static ConcurrentHashMap<String, String> UPLOAD_PROCESSING_STATUS =
      new ConcurrentHashMap<>();
  public static Set<String> EXCEPTION_REPORTS = new HashSet<>();
  public static Properties HDG_PCC_CODE_MAP = new Properties();

  /**
   * Setup method initialize system and prepare necessary folders
   * 1) Connect to FTP server
   * 2) Validate file
   * 3) Send invalid file email
   * 4) Initiate chrome browser 
   *
   * @throws InterruptedException the interrupted exception
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws AddressException the address exception
   * @throws MessagingException the messaging exception
   */
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
    
    WebDriverManager.chromedriver().setup();
    HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
    chromePrefs.put("profile.default_content_settings.popups", 0);
    chromePrefs.put("download.default_directory", ERROR_REPORT_PATH);
    option.setExperimentalOption("prefs", chromePrefs);
    option.addArguments("--remote-allow-origins=*");

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
          log.info("Opening chrome");
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

  /**
   * Close browser.
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

  /**
   * Import file initialize file upload .
   *
   * @throws Exception the exception
   */
  @Test
  public void Import_File() throws Exception {
    String processingStatus = "";
    boolean attachFiles = true;
    if (!anyValidFile) {
      log.info("No files to process , check valid and invalid folders at {} ", CURRENT_HOUR_FOLDER);
      processingStatus = "No files to process for hour " + CURRENT_HOUR;
      attachFiles = false;
    } else {
      if (driver == null) {
        log.info("Chrome driver is null");
        processingStatus = "(Chrome driver is null) File processing finished for hour " + CURRENT_HOUR;
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
              String pdfName = (Application.ERROR_REPORT_PATH + "//" + fileName).replace("//", "\\")
                  .replace(".csv", "") + "_" + System.currentTimeMillis() + ".pdf";
              log.info("Uploading file " + fileNameWithPath);

              //imf.browse();
              if (imf.checkfile(fileName)) {
                imf.uploadfile(fileNameWithPath, fileName);
                imf.loadfile();
                imf.popUpHandler(fileNameWithPath, pdfName, fileName);
              }

            } else if (file.isDirectory()) {
              log.info(fileName + " is not file");
            }
          } catch (Exception e) {
            log.info("Error while reading file " + fileName);
            UPLOAD_PROCESSING_STATUS.put(fileName, "Error while uploading file" + e.getMessage());
          }
        }
        processingStatus = "File processing finished for hour " + CURRENT_HOUR;
        log.info("Processing finished");
      }

      //
    }
    log.info("Processing status " + processingStatus);
    EmailConfig.sendProcessingStatusEmail(processingStatus,attachFiles);
  }

  /**
   * Dry run.
   * Do not un-comment test annotation, this method used while development
   * @throws Exception the exception
   */
  //@Test
  public void DryRun() throws Exception {

    driver.manage().window().maximize();
    driver.get(configProps.getProperty("pcc.website"));// "https://www25.pointclickcare.com/home/login.jsp?ESOLGuid=40_1672328090402"
    Thread.sleep(2000);

    ImportFile imf = new ImportFile(driver);
    imf.username();
    imf.password();
    imf.submit();
    String filePath = "C:\\PCC\\2023\\JANUARY\\20\\6\\HDG_invout_HDG-2_20201130_TEST3.csv";
    imf.uploadfile(filePath, "HDG_invout_HDG-2_20201130_TEST3.csv");
    imf.loadfile();
    String pdfFileName =
        (Application.ERROR_REPORT_PATH + "//" + "HDG_invout_HDG-2_20201130_TEST3.csv")
            .replace("//", "\\").replace(".csv", ".pdf");
    imf.popUpHandler(filePath, pdfFileName, "HDG_invout_HDG-2_20201130_TEST3.csv");

  }

  /**
   * Load properties from file pcc.properties
   *
   * @return the properties
   */
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
      System.exit(0);
      return null;
    }
  }

  /**
   * Prepare community code map from file hdg-pcc-code-mapping.properties.
   */
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
      log.info("Error while reading file :  " + path);
      ex.printStackTrace();
      System.exit(0);
    }
  }

  /**
   * Creates the dir.
   *
   * @param path the path
   */
  public static void createDir(String path) {
    log.info("Creating folder {}", path);
    File file = new File(path + "//");
    boolean status = file.mkdirs();
    log.info("Error report folder created ::" + status);
  }
}
