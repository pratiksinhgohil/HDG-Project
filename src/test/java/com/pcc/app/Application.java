package com.pcc.app;

import java.awt.AWTException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.pcc.utils.EmailConfig;
import com.pcc.utils.FileValidator;
import com.pcc.utils.FtpConnection;
import com.pcc.utils.ImportFile;

/**
 * @author dell
 *
 *         Create following folders C://PCC// C://PCC//DOWNLOADED_FILES//
 *
 */
public class Application {

	WebDriver driver;

	public static String FTP_USERNAME = null;
	public static Properties configProps = null;
	static LocalDateTime CURRENT_TIME = LocalDateTime.now();
	public static String CURRENT_HOUR_FOLDER = null;
	public static String CURRENT_HOUR_FOLDER_VALID_FILES = null;
	public static String CURRENT_HOUR_FOLDER_IN_VALID_FILES = null;

	public static String APP_BASE_PATH = System.getenv("PCC_BASE_PATH");

	@BeforeClass
	public void setup() throws InterruptedException, IOException {

		configProps = loadProperties();

		System.setProperty("webdriver.chrome.driver", configProps.getProperty("webdriver.chrome.driver"));// "C:/Users/Administrator/Downloads/chromedriver_win32new/chromedriver.exe"

		// Connect to FTP and download files
		CURRENT_HOUR_FOLDER = APP_BASE_PATH + CURRENT_TIME.getYear() + "//" + CURRENT_TIME.getMonth() + "//"
				+ CURRENT_TIME.getDayOfMonth() + "//" + CURRENT_TIME.getHour();
		CURRENT_HOUR_FOLDER_VALID_FILES = CURRENT_HOUR_FOLDER + "//valid";
		CURRENT_HOUR_FOLDER_IN_VALID_FILES = CURRENT_HOUR_FOLDER + "//invalid";

		FtpConnection pccFTPConn = new FtpConnection();
		if (pccFTPConn.connect()) {
			if (pccFTPConn.downloadFiles() > 0) {

				FileValidator validator = new FileValidator();
				validator.validateFiles();

				if (validator.hashValidFiles() > 0) {
					driver = new ChromeDriver();
					driver.manage().window().maximize();
					driver.get(configProps.getProperty("pcc.website"));// "https://www25.pointclickcare.com/home/login.jsp?ESOLGuid=40_1672328090402"
					Thread.sleep(2000);
				}

				if (validator.hashInvalidFiles() > 0) {
					EmailConfig.sendEmail(true, null);
				}

			}
		} else {
			System.out.println("Issue in FTP connection");
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
			driver.close();
			Runtime.getRuntime().exec("taskkill /F /IM chromedriver*");
		} catch (Exception anException) {
			anException.printStackTrace();
		}
	}

	@Test
	public void Import_File() throws InterruptedException, AWTException {

		ImportFile imf = new ImportFile(driver);
		imf.username();
		imf.password();

		File folder = new File(CURRENT_HOUR_FOLDER_VALID_FILES);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			try {
				if (file.isFile()) {
					imf.hovermenu();
					imf.ac_pay();
					imf.browse();
					imf.uploadfile(file.getCanonicalPath());
					// TODO - Add dynamic file path here :
					// C://FTP/File//HDG_invout_HDG-2_20201130_TEST3_29-12-2022/13-36-24.csv
					imf.loadfile();
					imf.exception();

				} else if (file.isDirectory()) {
					System.out.println(file.getName() + " is not file");
				}
			} catch (InterruptedException | IOException e) {
				System.out.println("Error while reading file " + file.getName());
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
	private static Properties loadProperties() {

		String path = APP_BASE_PATH + "//pcc.properties";

		try (InputStream input = new FileInputStream(path)) {
			Properties prop = new Properties();
			prop.load(input);
			for (String key : prop.stringPropertyNames()) {
				System.out.println(key + " >> " + prop.getProperty(key));
			}
			return prop;
		} catch (IOException ex) {
			System.out.println("Error while reading file " + path);
			ex.printStackTrace();
			return null;
		}
	}
}
