package com.pcc.app;

import java.awt.AWTException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.pcc.utils.ImportFile;

public class Application {

	WebDriver driver;

	public static String FTP_USERNAME = null;
	public static Properties configProps = null;

	@BeforeClass
	public void setup() throws InterruptedException, IOException {

		configProps = loadProperties();
		System.setProperty("webdriver.chrome.driver", configProps.getProperty("webdriver.chrome.driver"));// "C:/Users/Administrator/Downloads/chromedriver_win32new/chromedriver.exe"
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get(configProps.getProperty("pcc.website"));// "https://www25.pointclickcare.com/home/login.jsp?ESOLGuid=40_1672328090402"
		Thread.sleep(2000);
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
		String localPath = Application.configProps.getProperty("pcc.ftp.localpath", "C://PCC_DOWNLOADED_FILES//");
		ImportFile imf = new ImportFile(driver);
		imf.username();
		imf.password();
		imf.hovermenu();
		imf.ac_pay();
		imf.browse();
		imf.uploadfile(localPath + "HDG_invout_HDG-2_20201130_TEST.csv"); // TODO - Add dynamic file path here : C://FTP
																			// File//HDG_invout_HDG-2_20201130_TEST3_29-12-2022
																			// 13-36-24.csv
		imf.loadfile();
		imf.exception();

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
		// String path = "C://PCC//pcc.properties";
		String path = "//home//dell//DSSI-PCC_WORK//CODE//HDG-Project//pcc.properties";
		try (InputStream input = new FileInputStream(path)) {
			Properties prop = new Properties();
			prop.load(input);
			for (String key : prop.stringPropertyNames()) {
				System.out.println(prop + " >> " + prop.getProperty(key));
			}
			return prop;
		} catch (IOException ex) {
			System.out.println("Error while reading file " + path);
			ex.printStackTrace();
			return null;
		}
	}
}
