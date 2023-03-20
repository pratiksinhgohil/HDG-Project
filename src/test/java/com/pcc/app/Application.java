package com.pcc.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.pcc.utils.AppConfig;
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

	public static AppConfig APP_CONFIG = null;

	/**
	 * Setup method initialize system and prepare necessary folders 1) Connect to
	 * FTP server 2) Validate file 3) Send invalid file email 4) Initiate chrome
	 * browser
	 *
	 * @throws InterruptedException the interrupted exception
	 * @throws IOException          Signals that an I/O exception has occurred.
	 * @throws AddressException     the address exception
	 * @throws MessagingException   the messaging exception
	 */
	@BeforeClass
	public void setup() throws InterruptedException, IOException, AddressException, MessagingException {
		APP_CONFIG = new AppConfig();
		APP_CONFIG.setCurrentTime(LocalDateTime.now());
		LocalDateTime CURRENT_TIME = LocalDateTime.now();

		log.info("Starting file processing {}", APP_CONFIG.getCurrentTime());

		Properties configProps = loadProperties();
		String APP_BASE_PATH = System.getenv("PCC_BASE_PATH");

		APP_CONFIG.setConfigProps(configProps);
		APP_CONFIG.setEmailSender(configProps.getProperty("pcc.mail.sender.password"));

		String[] receiverEmails = configProps.getProperty("pcc.mail.receivers.emailids").split(",");

		List<InternetAddress> EMAIL_RECEIVER = new ArrayList<>();

		for (String receivers : receiverEmails) {
			EMAIL_RECEIVER.add(new InternetAddress(receivers));
		}

		String CURRENT_HOUR = CURRENT_TIME.getYear() + "//" + CURRENT_TIME.getMonth() + "//"
				+ CURRENT_TIME.getDayOfMonth() + "//" + CURRENT_TIME.getHour();

		String CURRENT_HOUR_FOLDER = APP_BASE_PATH + "//" + CURRENT_HOUR;
		String CURRENT_HOUR_FOLDER_VALID_FILES = CURRENT_HOUR_FOLDER + "//valid";
		String CURRENT_HOUR_FOLDER_IN_VALID_FILES = CURRENT_HOUR_FOLDER + "//invalid";
		String ERROR_REPORT_PATH = APP_BASE_PATH + "//errorfiles//" + CURRENT_HOUR;

		WebDriverManager.edgedriver().setup();

		APP_CONFIG.setEmailReceiver(EMAIL_RECEIVER);
		APP_CONFIG.setCurrentHour(CURRENT_HOUR);
		APP_CONFIG.setCurrentHourFolder(CURRENT_HOUR_FOLDER);
		APP_CONFIG.setCurrentHourFolderValidFiles(CURRENT_HOUR_FOLDER_VALID_FILES);
		APP_CONFIG.setCurrentHourFolderInValidFiles(CURRENT_HOUR_FOLDER_IN_VALID_FILES);
		APP_CONFIG.setErrorReportFilesPath(ERROR_REPORT_PATH);

		log.info("Processing for folder > " + ERROR_REPORT_PATH);

		createDir(CURRENT_HOUR_FOLDER);
		createDir(CURRENT_HOUR_FOLDER_VALID_FILES);
		createDir(CURRENT_HOUR_FOLDER_IN_VALID_FILES);
		createDir(ERROR_REPORT_PATH);

		prepareCommunityCodeMap();
		log.info("CURRENT_HOUR_FOLDER >> " + CURRENT_HOUR_FOLDER);
		log.info("CURRENT_HOUR_FOLDER_VALID_FILES >> " + CURRENT_HOUR_FOLDER_VALID_FILES);
		log.info("CURRENT_HOUR_FOLDER_IN_VALID_FILES >> " + CURRENT_HOUR_FOLDER_IN_VALID_FILES);
		log.info("ERROR_REPORT_PATH >> " + ERROR_REPORT_PATH);

		FtpConnection pccFTPConn = new FtpConnection();

		if (pccFTPConn.connect()) {
			if (pccFTPConn.downloadFiles() > 0) {

				FileValidator validator = new FileValidator();
				validator.validateFiles();

				if (validator.hashValidFiles() > 0) {
					log.info("Opening chrome");

					APP_CONFIG.setAnyValidFile(true);
					driver = new EdgeDriver();
					driver.manage().window().maximize();
					Map<String, Object> prefs = new HashMap<String, Object>();
					prefs.put("download.default_directory", System.getProperty("user.dir") + File.separator
							+ "externalFiles" + File.separator + "downloadFiles");
					EdgeOptions op = new EdgeOptions();
					op.setExperimentalOption("prefs", prefs);
					driver.get(configProps.getProperty("pcc.website"));// "https://www25.pointclickcare.com/home/login.jsp?ESOLGuid=40_1672328090402"
					log.info("configProps.getProperty(\"pcc.website\") " + configProps.getProperty("pcc.website"));
					Thread.sleep(2000);
				}

				if (validator.hashInvalidFiles() > 0) {
					EmailConfig.sendInvalidFiles();
				}

				if (!Application.APP_CONFIG.getLineDescriptionFiles().isEmpty()) {
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
		if (!APP_CONFIG.isAnyValidFile()) {
			log.info("No files to process , check valid and invalid folders at {} ", Application.APP_CONFIG.getCurrentHourFolder());
			processingStatus = "No files to process for hour " + Application.APP_CONFIG.getCurrentHour();
			attachFiles = false;
		} else {
			if (driver == null) {
				log.info("Edge driver is null");
				processingStatus = "(Edge driver is null) File processing finished for hour "
						+ Application.APP_CONFIG.getCurrentHour();
			} else {
				ImportFile imf = new ImportFile(driver);
				imf.username();
				imf.password();
				imf.submit();
				// imf.hovermenu();
				// imf.ac_pay();
				log.info("Password entered");
				File folder = new File(APP_CONFIG.getCurrentHourFolderValidFiles());// CURRENT_HOUR_FOLDER_VALID_FILES
				File[] listOfFiles = folder.listFiles();
				log.info("Got list of files");
				for (File file : listOfFiles) {
					String fileName = file.getName();
					try {
						if (file.isFile()) {
							String fileNameWithPath = file.getCanonicalPath();
							;
							String pdfName = (Application.APP_CONFIG.getErrorReportFilesPath() + "//" + fileName)
									.replace("//", "\\").replace(".csv", "") + "_" + System.currentTimeMillis()
									+ ".pdf";
							log.info("Uploading file " + fileNameWithPath);

							// imf.browse();
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
						APP_CONFIG.getUploadProcessingStatus().put(fileName,
								"Error while uploading file" + e.getMessage());
					}
				}
				processingStatus = "File processing finished for hour " + Application.APP_CONFIG.getCurrentHour();
				log.info("Processing finished");
			}

			//
		}
		log.info("Processing status " + processingStatus);
		EmailConfig.sendProcessingStatusEmail(processingStatus, attachFiles);
	}

	/**
	 * Dry run. Do not un-comment test annotation, this method used while
	 * development
	 * 
	 * @throws Exception the exception
	 */
	// @Test
	public void DryRun() throws Exception {

		driver.manage().window().maximize();
		// driver.get(configProps.getProperty("pcc.website"));//
		// "https://www25.pointclickcare.com/home/login.jsp?ESOLGuid=40_1672328090402"
		Thread.sleep(2000);

		ImportFile imf = new ImportFile(driver);
		imf.username();
		imf.password();
		imf.submit();
		String filePath = "C:\\PCC\\2023\\JANUARY\\20\\6\\HDG_invout_HDG-2_20201130_TEST3.csv";
		imf.uploadfile(filePath, "HDG_invout_HDG-2_20201130_TEST3.csv");
		imf.loadfile();
		// String pdfFileName = (Application.ERROR_REPORT_PATH + "//" +
		// "HDG_invout_HDG-2_20201130_TEST3.csv").replace("//", "\\").replace(".csv",
		// ".pdf");
		// imf.popUpHandler(filePath, pdfFileName,
		// "HDG_invout_HDG-2_20201130_TEST3.csv");

	}

	/**
	 * Load properties from file pcc.properties
	 *
	 * @return the properties
	 */
	private static Properties loadProperties() {
		String basepath = System.getenv("PCC_BASE_PATH");
		if (basepath == null) {
			log.info("Please set APP_BASE_PATH");
			System.exit(0);
		}
		String path = basepath + "//pcc.properties";

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
		String basepath = System.getenv("PCC_BASE_PATH");
		if (basepath == null) {
			log.info("Please set APP_BASE_PATH");
			System.exit(0);
		}
		String path = basepath + "//hdg-pcc-code-mapping.properties";

		try (InputStream input = new FileInputStream(path)) {
			Properties prop = new Properties();
			prop.load(input);
			for (String key : prop.stringPropertyNames()) {
				log.info("HDG to PCC Code map>>" + key + " >> " + prop.getProperty(key));
			}
			APP_CONFIG.getHdgPccCodeMap().putAll(prop);
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
