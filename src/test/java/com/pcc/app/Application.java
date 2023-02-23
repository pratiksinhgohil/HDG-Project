package com.pcc.app;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
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

	@BeforeClass
	public void setup() throws InterruptedException, IOException, AddressException, MessagingException {
		log.info("Starting file processing ");
		configProps = loadProperties();
		
		loadCodeMapper();

		EMAIL_SENDER = Application.configProps.getProperty("pcc.mail.sender.email");
	 
		Application.configProps.getProperty("pcc.mail.sender.password");
		String[] receiverEmails = Application.configProps.getProperty("pcc.mail.receivers.emailids").split(",");
		for (String receivers : receiverEmails) {
			EMAIL_RECEIVER.add(new InternetAddress(receivers));
		}
		System.setProperty("webdriver.chrome.driver", configProps.getProperty("webdriver.chrome.driver"));// "C:/Users/Administrator/Downloads/chromedriver_win32new/chromedriver.exe"
		// Connect to FTP and download files
		CURRENT_HOUR = CURRENT_TIME.getYear() + "//" + CURRENT_TIME.getMonth() + "//" + CURRENT_TIME.getDayOfMonth()
				+ "//" + CURRENT_TIME.getHour();
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
		System.out.println("ERROR_REPORT_PATH >> " + ERROR_REPORT_PATH);
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

			}
		} else {
			log.info("Issue in FTP connection");
		}
		// Download files
		// driver.manage().deleteAllCookies();
		// driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

	}

	/*
	 * @AfterClass public void finish() throws InterruptedException {
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

		if (!anyValidFile) {
			log.info("No files to process , check valid and invalid folders at {} ", CURRENT_HOUR_FOLDER);
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
				try {
					if (file.isFile()) {
						log.info("Uploading file " + file.getCanonicalPath());
						
						//imf.browse();
						imf.uploadfile(file.getCanonicalPath());
						
						// Success
						// imf.uploadfile("C://PCC//2023//JANUARY//17//11//HDG_invout_HDG-143_20230109_054907369_1.csv");
						// Failure
						// imf.uploadfile("C://PCC//2023//JANUARY//17//11//HDG_invout_HDG-108_20201130_TEST2.csv");
						imf.checkfile(file.getName());
						imf.loadfile();
						imf.exception(file.getCanonicalPath(), (Application.ERROR_REPORT_PATH + "//" + file.getName())
								.replace("//", "\\").replace(".csv", ".pdf"));

					} else if (file.isDirectory()) {
						log.info(file.getName() + " is not file");
					}
				} catch (InterruptedException | IOException e) {
					log.info("Error while reading file " + file.getName());
				}
			}
		}

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

		//imf.hovermenu();
		//imf.ac_pay();
	//imf.browse();
		// imf.uploadfile(file.getCanonicalPath());
		// Success
		// imf.uploadfile("C://PCC//2023//JANUARY//17//11//HDG_invout_HDG-143_20230109_054907369_1.csv");

		// Failure
		String filePath = "C:\\PCC\\2023\\JANUARY\\20\\6\\HDG_invout_HDG-2_20201130_TEST3.csv";
		imf.uploadfile(filePath);
		// TODO - Add dynamic file path here :
		// C://FTP/File//HDG_invout_HDG-2_20201130_TEST3_29-12-2022/13-36-24.csv
		imf.loadfile();
		// imf.exception((Application.ERROR_REPORT_PATH+"//HDG_invout_HDG-2_20201130_TEST3.csv").replace("//",
		// "\\"));
		imf.exception(filePath, (Application.ERROR_REPORT_PATH + "//" + "HDG_invout_HDG-2_20201130_TEST3.csv")
				.replace("//", "\\").replace(".csv", ".pdf"));

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

	public static void createDir(String path) {
		log.info("Creating folder {}", path);
		File file = new File(path + "//");
		boolean status = file.mkdirs();
		System.out.println("Error report folder created ::" + status);
	}

	private void loadCodeMapper() {
		
		
	}
}
