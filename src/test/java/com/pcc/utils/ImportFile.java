package com.pcc.utils;

import java.awt.AWTException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.StreamSupport;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeOptions;
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
	 * @throws AWTException
	 */
	public void username() throws InterruptedException, AWTException {
		uname.sendKeys(Application.APP_CONFIG.getConfigProps().getProperty("pcc.website.username"));
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
		pwd.sendKeys(Application.APP_CONFIG.getConfigProps().getProperty("pcc.website.password"));
		Thread.sleep(2000);
	}

	/**
	 * Submit.
	 *
	 * @throws InterruptedException the interrupted exception
	 */
	public void submit() throws InterruptedException, AWTException {
		submit.click();
		driver.get("edge://settings/content/pdfDocuments?search=pdf");

		driver.findElement(By.xpath("//*[@id=\"section_pdf\"]/div[2]/div/div[1]/div/div[1]/div[2]/div/div/input"))
				.click();
		Thread.sleep(3000);
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
		return StreamSupport.stream(path.spliterator(), false).map(Path::toString).toArray(String[]::new);
	}

	/**
	 * Checkfile.
	 *
	 * @param csvFileName the csv file name
	 * @return true, if successful
	 * @throws InterruptedException the interrupted exception
	 */
	public boolean checkfile(String csvFileName) throws InterruptedException {

		log.info("Checking community code in UI for file {}", csvFileName);
		try {

			if (csvFileName != null) {
				String[] csvFileNameArr = csvFileName.split("_");
				if (csvFileNameArr != null) {

					String communityCode = csvFileNameArr[2];
					log.info("Changing community code for  " + communityCode);

					if (Application.APP_CONFIG.getHdgPccCodeMap().containsKey(communityCode)) {

						String searchText = Application.APP_CONFIG.getHdgPccCodeMap().getProperty(communityCode);
						String xpathExpression = "//a[text()='" + searchText + "']";

						driver.get("https://www25.pointclickcare.com/home/home.jsp?ESOLnewlogin=N");
						driver.findElement(By.xpath("//*[@id=\"pccFacLink\"]")).click();
						Thread.sleep(2000);
						driver.findElement(By.id("facSearchFilter")).sendKeys(searchText);
						Thread.sleep(2000);
						driver.findElement(By.className("pccButton")).click();
						Thread.sleep(2000);
						driver.findElement(By.xpath(xpathExpression)).click();
						Thread.sleep(6000);
						return true;
					} else {
						Application.APP_CONFIG.getUploadProcessingStatus().put(csvFileName,
								"Community code not found in configuration(hdg-pcc-code-mapping.properties)");
					}
				}
			}
		} catch (Exception e) {
			log.info("The file {} contains invalid commmunity code in name", csvFileName);
			Application.APP_CONFIG.getUploadProcessingStatus().put(csvFileName,
					"Error while setting community code, check hdg-pcc-code-mapping.properties and pcc web-site");
			// EmailConfig.invalidCommunityCodeInFileName(csvFileName);
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
		Thread.sleep(10000);
	}

	/**
	 * Pop up handler.
	 *
	 * @param csvFileNameWithPath the csv file name with path
	 * @param pdfName             the pdf name
	 * @param fileName            the file name
	 * @param driver2
	 * @throws Exception    the exception
	 * @throws AWTException the AWT exception
	 */
	public void popUpHandler(String csvFileNameWithPath, String pdfName, String fileName)
			throws Exception, AWTException {
		log.info("Uploaded file path" + csvFileNameWithPath);
		log.info(" Error report PDF " + pdfName + " exc_repo.getAccessibleName() >>> " + exc_repo.getAccessibleName());

		if (exc_repo.getAccessibleName().equalsIgnoreCase("Exceptions Report")) {
			Application.APP_CONFIG.getUploadProcessingStatus().put(fileName, "Exception report generated");
			exc_repo.click();
			Thread.sleep(10000);
			// File file = new File("D:\\PCC\\TempD\\invoiceimportexceptionreport.xhtml");
			File file = new File(Application.APP_CONFIG.getErrorReportFilesPath().replace("//", "\\")
					+ "\\invoiceimportexceptionreport.xhtml");
			// File (or directory) with new name
			File file2 = new File("" + pdfName);

			if (file2.exists()) {
				throw new java.io.IOException("file exists");
			}

			// Rename file (or directory)
			boolean success = file.renameTo(file2);
			Application.APP_CONFIG.getExceptionReports().add(pdfName);
			Thread.sleep(2000);
		} else if (exc_repo.getAccessibleName().equalsIgnoreCase("Commit")) {
			Thread.sleep(5000);
			commit(csvFileNameWithPath, fileName);
			Thread.sleep(5000);

		} else {
			log.info("Unknown button " + exc_repo.getAccessibleName());
			Application.APP_CONFIG.getUploadProcessingStatus().put(csvFileNameWithPath,
					"Unknown button in UI :  " + exc_repo.getAccessibleName());
		}

	}

	/**
	 * Commit.
	 *
	 * @param csvFileNameWithPath the csv file name with path
	 * @param csvFileName         the csv file name
	 * @throws Exception the exception
	 */
	public void commit(String csvFileNameWithPath, String csvFileName) throws Exception {

		commit.click();

		Thread.sleep(7000);

		try {
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			log.info("ALERT_BOX_DETECTED) - ALERT MSG : " + alertText);
			if (alertText != null && alertText.toLowerCase().contains("duplicate invoice found")) {
				log.info(csvFileName + " : Status : Duplicate invoice message : " + alertText);
				// EmailConfig.sendDuplicateInvoiceEmail(csvFileNameWithPath,csvFileName,alertText);
				Application.APP_CONFIG.getUploadProcessingStatus().put(csvFileName, "Status : " + alertText);
			} else if (alertText != null && alertText.toLowerCase().contains("commit complete")) {
				log.info(csvFileName + " : Status : Commit Complete message : " + alertText);
				Application.APP_CONFIG.getUploadProcessingStatus().put(csvFileName, "Status :" + alertText);
			} else {
				log.info(csvFileName + "  Unknown error: " + alertText);
				Application.APP_CONFIG.getUploadProcessingStatus().put(csvFileName, "Status : " + alertText);
			}

			alert.accept();
			Thread.sleep(2000);
			close.click();
			Thread.sleep(5000);

		} catch (Exception e) {
			log.info("Catch block" + e.getMessage());
			Application.APP_CONFIG.getUploadProcessingStatus().put(csvFileName,
					"Status(Exception) : Please verify file in PCC as commit message didn't observed");
		}
	}

}
