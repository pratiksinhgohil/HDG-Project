package com.pcc.utils;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.StreamSupport;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Pdf;
import org.openqa.selenium.PrintsPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.print.PrintOptions;
import org.openqa.selenium.support.PageFactory;

import com.pcc.app.Application;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImportFile extends ImportFileOr {
	WebDriver driver;

	public ImportFile(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);

		// driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
	}

	public void username() throws InterruptedException {
		uname.sendKeys(Application.configProps.getProperty("pcc.website.username"));
		Thread.sleep(2000);
		nextbtn.click();
		Thread.sleep(2000);
	}

	public void password() throws InterruptedException {
		pwd.sendKeys(Application.configProps.getProperty("pcc.website.password"));
		Thread.sleep(2000);
	}

	public void submit() throws InterruptedException {
		submit.click();
		Thread.sleep(7000);

	}

	public void hovermenu() throws InterruptedException {
		Actions ac = new Actions(driver);
		ac.moveToElement(hover).perform();
		Thread.sleep(2000);

	}

//	public void ac_pay() throws InterruptedException {
//		//acpay.click();
//		Thread.sleep(2000);
//	}

//	public void browse() throws InterruptedException {
//		impfile.click();
//		Thread.sleep(2000);
//	}

	public void uploadfile(String filePath) throws InterruptedException {
//		String oldTab = driver.getWindowHandle();
//		ArrayList<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
//		tabs2.remove(oldTab);
//		driver.switchTo().window(tabs2.get(0));
		driver.navigate().to("https://www25.pointclickcare.com/glap/ap/processing/invoiceimport.xhtml");
		Thread.sleep(2000);

		uploadfile.sendKeys(filePath);// "C://FTP File//HDG_invout_HDG-2_20201130_TEST3_29-12-2022 13-36-24.csv"
		Thread.sleep(2000);

	}

	public static String[] splitPath(String pathString) {
		Path path = Paths.get(pathString);
		return StreamSupport.stream(path.spliterator(), false).map(Path::toString).toArray(String[]::new);
	}

	public void checkfile(String csvFileName) throws InterruptedException {

		// System.out.println((Arrays.toString((paths[paths.length - 1] ).split("-", 2))
		// ));
		System.out.println(csvFileName);
		if (csvFileName != null) {

			String[] csvFileNameArr = csvFileName.split("_");
			if (csvFileNameArr != null) {

				String communityCode = csvFileNameArr[2];
				System.out.println("Community Code " + communityCode);
				if (communityCode.equalsIgnoreCase("VHCF-1")) {
					/*
					 * String originalWindow = driver.getWindowHandle();
					 * driver.switchTo().newWindow(WindowType.TAB);
					 * driver.get("https://www25.pointclickcare.com/home/home.jsp?ESOLnewlogin=N");
					 * driver.findElement(By.xpath("//*[@id=\"pccFacLink\"]")).click();
					 * driver.findElement(By.xpath("//*[@id=\"optionList\"]/li[10]/a")).click();
					 * driver.switchTo().window(originalWindow);
					 */} else if (communityCode.equalsIgnoreCase("HDG-161")) {
					/*
					 * String originalWindow = driver.getWindowHandle();
					 * driver.switchTo().newWindow(WindowType.TAB);
					 * driver.get("https://www25.pointclickcare.com/home/home.jsp?ESOLnewlogin=N");
					 * driver.findElement(By.xpath("//*[@id=\"pccFacLink\"]")).click();
					 * driver.findElement(By.xpath("//*[@id=\"optionList\"]/li[10]/a")).click();
					 * driver.switchTo().window(originalWindow);
					 */}
				// String filename = (csvFileNameArr[csvFileNameArr.length - 1]).split("-",
				// 2)[1];
				// System.out.println(filename);
				// String FirstChartFilename = (filename).split("_", 2)[0];

				// System.out.println(FirstChartFilename);
				// if (FirstChartFilename.equalsIgnoreCase("120")) {
				// System.out.println(true);
				// } else
				// System.out.println(false);
			}
		}
//			  else {
//				  String filename2 = (csvFileName[csvFileName.length - 1] ).split("-", 2)[1];
//			      System.out.println(filename2);
//			      String FirstChartFilename = (filename2).split("_", 2)[0];
//			      
//			      System.out.println(FirstChartFilename);
//			      if(FirstChartFilename.equalsIgnoreCase("120")) {
//			    	  System.out.println(true);
//			      }
//			      else 
//			    	  System.out.println(false);
//				}
	}

//		String originalWindow = driver.getWindowHandle();
//		driver.switchTo().newWindow(WindowType.TAB);
//		driver.get("https://www25.pointclickcare.com/home/home.jsp?ESOLnewlogin=N");
//		driver.findElement(By.xpath("//*[@id=\"pccFacLink\"]")).click();
//		driver.findElement(By.xpath("//*[@id=\"optionList\"]/li[10]/a")).click();
//		driver.switchTo().window(originalWindow);
//	}
	// }

	public void loadfile() throws InterruptedException {
		loadbtn.click();
		Thread.sleep(6000);
	}

	public void exception(String csvFileName, String errorFilePath) throws Exception, AWTException {

		System.out.println("Uploaded file path" + csvFileName);
		System.out.println("Error report PDF " + errorFilePath);
		System.out.println("exc_repo.getAccessibleName() >>> " + exc_repo.getAccessibleName());
		if (exc_repo.getAccessibleName().equalsIgnoreCase("Exceptions Report")) {

			exc_repo.click();
			Thread.sleep(5000);
			ChromeOptions option = new ChromeOptions();
			option.addArguments("headless");
			ChromeDriver driver = new ChromeDriver(option);
			String originalWindow = driver.getWindowHandle();
			Thread.sleep(1000);
			Pdf pdf = ((PrintsPage) driver).print(new PrintOptions());
			Files.write(Paths.get(errorFilePath), OutputType.BYTES.convertFromBase64Png(pdf.getContent()));
			log.info("File Imported and Downloaded Exception Report");
			Thread.sleep(2000);

			Robot rb = new Robot();
			rb.keyPress(KeyEvent.VK_CONTROL);
			rb.keyPress(KeyEvent.VK_W);

			rb.keyRelease(KeyEvent.VK_CONTROL);
			rb.keyRelease(KeyEvent.VK_W);

			rb.keyPress(KeyEvent.VK_CONTROL);
			rb.keyPress(KeyEvent.VK_SHIFT);
			rb.keyPress(KeyEvent.VK_TAB);
			rb.keyRelease(KeyEvent.VK_CONTROL);
			rb.keyRelease(KeyEvent.VK_SHIFT);
			rb.keyRelease(KeyEvent.VK_TAB);

			// Send email
			EmailConfig.sendExceptionReport(errorFilePath, csvFileName);
			Thread.sleep(5000);

			//
		} else if (exc_repo.getAccessibleName().equalsIgnoreCase("Commit")) {
			Thread.sleep(5000);
			commit();
			Thread.sleep(5000);
//			Robot rb =new Robot();
//			rb.keyPress(KeyEvent.VK_ALT);
//			rb.keyPress(KeyEvent.VK_F4);
//			rb.keyRelease(KeyEvent.VK_ALT);
//			rb.keyRelease(KeyEvent.VK_F4);
		} else {
			System.out.println("Unknown button " + exc_repo.getAccessibleName());
		}

	}

	public void commit() throws Exception {

		commit.click();
		Thread.sleep(5000);

		try {
			Thread.sleep(2000);
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			System.out.println("ERROR: (ALERT BOX DETECTED) - ALERT MSG : " + alertText);
			alert.accept();
			Thread.sleep(2000);
			close.click();
			Thread.sleep(5000);
//			Robot rb =new Robot();
//			rb.keyPress(KeyEvent.VK_ALT);
//			rb.keyPress(KeyEvent.VK_F4);
//			rb.keyRelease(KeyEvent.VK_ALT);
//			rb.keyRelease(KeyEvent.VK_F4);

		} catch (Exception e) {

		}
		// driver.close();
	}

}
