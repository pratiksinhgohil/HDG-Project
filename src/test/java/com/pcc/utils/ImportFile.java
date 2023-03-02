package com.pcc.utils;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
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
import io.github.bonigarcia.wdm.WebDriverManager;

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
		try {
			
		
		if (csvFileName != null) {

			String[] csvFileNameArr = csvFileName.split("_");
			if (csvFileNameArr != null) {

				String communityCode = csvFileNameArr[2];
				System.out.println("Community Code " + communityCode);
				if (communityCode.equalsIgnoreCase("VHCF-1")) {
					
					// String originalWindow = driver.getWindowHandle();
					//  driver.switchTo().newWindow(WindowType.TAB);
					 driver.get("https://www25.pointclickcare.com/home/home.jsp?ESOLnewlogin=N");
					  driver.findElement(By.xpath("//*[@id=\"pccFacLink\"]")).click();
					  Thread.sleep(2000);
					  driver.findElement(By.xpath("//a[text()='Dimensions Management of Chippewa Falls, LLC - 126']")).click();
					  Thread.sleep(6000);
					 //driver.switchTo().window(originalWindow);
					 } else if (communityCode.equalsIgnoreCase("HDG-161")) {
						 driver.get("https://www25.pointclickcare.com/home/home.jsp?ESOLnewlogin=N");
						 driver.findElement(By.xpath("//*[@id=\"pccFacLink\"]")).click();
						  Thread.sleep(2000);
						  driver.findElement(By.xpath("//a[text()='Dimensions Living - Prospect Heights - ALF - 160']")).click();
						  Thread.sleep(6000);
					 }	 else if (communityCode.equalsIgnoreCase("HDG-100")) {
						 driver.get("https://www25.pointclickcare.com/home/home.jsp?ESOLnewlogin=N");
						 driver.findElement(By.xpath("//*[@id=\"pccFacLink\"]")).click();
						  Thread.sleep(2000);
						  driver.findElement(By.id("facSearchFilter")).sendKeys("Marycrest, LLC - GL/AP - 110");
						  Thread.sleep(2000);
						  driver.findElement(By.className("pccButton")).click();
						  Thread.sleep(2000);
						  driver.findElement(By.xpath("//a[text()='Marycrest, LLC - GL/AP - 110']")).click();
						  Thread.sleep(6000);
					 }	  else if (communityCode.equalsIgnoreCase("HDG-134")) {
						 driver.get("https://www25.pointclickcare.com/home/home.jsp?ESOLnewlogin=N");
						  driver.findElement(By.xpath("//*[@id=\"pccFacLink\"]")).click();
						  Thread.sleep(2000);
						  driver.findElement(By.id("facSearchFilter")).sendKeys("MJM - Maria Joseph Manor - ALF - 134");
						  Thread.sleep(2000);
						  driver.findElement(By.className("pccButton")).click();
						  Thread.sleep(2000);
						  driver.findElement(By.xpath("//a[text()='MJM - Maria Joseph Manor - ALF - 134']")).click();
						  Thread.sleep(6000);
					 
					 } else if (communityCode.equalsIgnoreCase("HDG-133")) {
						 driver.get("https://www25.pointclickcare.com/home/home.jsp?ESOLnewlogin=N");
						  driver.findElement(By.xpath("//*[@id=\"pccFacLink\"]")).click();
						  Thread.sleep(4000);
						  driver.findElement(By.id("facSearchFilter")).sendKeys("MJM - The Meadows at Maria Joseph - ILF - 133");
						  Thread.sleep(2000);
						  driver.findElement(By.className("pccButton")).click();
						  Thread.sleep(2000);
						  driver.findElement(By.xpath("//a[text()='MJM - The Meadows at Maria Joseph - ILF - 133']")).click();
						  Thread.sleep(6000);   
					 }    else if (communityCode.equalsIgnoreCase("HDG-9")) {
						  driver.get("https://www25.pointclickcare.com/home/home.jsp?ESOLnewlogin=N");
						  driver.findElement(By.xpath("//*[@id=\"pccFacLink\"]")).click();
						  Thread.sleep(2000);
						  driver.findElement(By.id("facSearchFilter")).sendKeys("LHS - Lutheran Homes Society - SNF - 09");
						  Thread.sleep(2000);
						  driver.findElement(By.className("pccButton")).click();
						  Thread.sleep(2000);
						  driver.findElement(By.xpath("//a[text()='LHS - Lutheran Homes Society - SNF - 09")).click();
						  Thread.sleep(6000);   
					 }    else if (communityCode.equalsIgnoreCase("HDG-118")) {
						  driver.get("https://www25.pointclickcare.com/home/home.jsp?ESOLnewlogin=N");
						  driver.findElement(By.xpath("//*[@id=\"pccFacLink\"]")).click();
						  Thread.sleep(2000);
						  driver.findElement(By.id("facSearchFilter")).sendKeys("LSS - Traverse Care Center - SNF - 118");
						  Thread.sleep(2000);
						  driver.findElement(By.className("pccButton")).click();
						  driver.findElement(By.xpath("//a[text()='LSS - Traverse Care Center - SNF - 118']")).click();
						  Thread.sleep(6000);
					 }	  else if (communityCode.equalsIgnoreCase("HDG-108")) {
						  driver.get("https://www25.pointclickcare.com/home/home.jsp?ESOLnewlogin=N");
						  driver.findElement(By.xpath("//*[@id=\"pccFacLink\"]")).click();
						  Thread.sleep(2000);
						  driver.findElement(By.id("facSearchFilter")).sendKeys("LSS - Frazee Care Center - SNF - 108");
						  Thread.sleep(2000);
						  driver.findElement(By.className("pccButton")).click();
						  Thread.sleep(2000);
						  driver.findElement(By.xpath("//a[text()='LSS - Frazee Care Center - SNF - 108']")).click();
						  Thread.sleep(6000);
					 }	  else if (communityCode.equalsIgnoreCase("HDG-143")) {
						  driver.get("https://www25.pointclickcare.com/home/home.jsp?ESOLnewlogin=N");
						  driver.findElement(By.xpath("//*[@id=\"pccFacLink\"]")).click();
						  Thread.sleep(2000);
						  driver.findElement(By.id("facSearchFilter")).sendKeys("Kruse Village - SNF - 143");
						  Thread.sleep(2000);
						  driver.findElement(By.className("pccButton")).click();
						  Thread.sleep(2000);
						  driver.findElement(By.xpath("//a[text()='Kruse Village - SNF - 143']")).click();
						  Thread.sleep(6000);  
					 }	  else if (communityCode.equalsIgnoreCase("HDG-164")) {
						  driver.get("https://www25.pointclickcare.com/home/home.jsp?ESOLnewlogin=N");
						  driver.findElement(By.xpath("//*[@id=\"pccFacLink\"]")).click();
						  Thread.sleep(2000);
						  driver.findElement(By.id("facSearchFilter")).sendKeys("Dimensions Living - Stevens Point - ALF - 164");
						  Thread.sleep(2000);
						  driver.findElement(By.className("pccButton")).click();
						  Thread.sleep(2000);
						  driver.findElement(By.xpath("//a[text()='Dimensions Living - Stevens Point - ALF - 164']")).click();
						  Thread.sleep(6000);
					 }	  else if (communityCode.equalsIgnoreCase("HDG-171")) {
						  driver.get("https://www25.pointclickcare.com/home/home.jsp?ESOLnewlogin=N");
						  driver.findElement(By.xpath("//*[@id=\"pccFacLink\"]")).click();
						  Thread.sleep(2000);
						  driver.findElement(By.id("facSearchFilter")).sendKeys("Dimensions Living - Green Bay - ALF - 171");
						  Thread.sleep(2000);
						  driver.findElement(By.className("pccButton")).click();
						  Thread.sleep(2000);
						  driver.findElement(By.xpath("//a[text()='Dimensions Living - Green Bay - ALF - 171']")).click();
						  Thread.sleep(6000);
					 }	  else if (communityCode.equalsIgnoreCase("HDG-162")) {
						  driver.get("https://www25.pointclickcare.com/home/home.jsp?ESOLnewlogin=N");
						  driver.findElement(By.xpath("//*[@id=\"pccFacLink\"]")).click();
						  Thread.sleep(2000);
						  driver.findElement(By.id("facSearchFilter")).sendKeys("Dimensions Living - Burr Ridge - SNF - 162");
						  Thread.sleep(2000);
						  driver.findElement(By.className("pccButton")).click();
						  Thread.sleep(2000);
						  driver.findElement(By.xpath("//a[text()='Dimensions Living - Burr Ridge - SNF - 162']")).click();
						  Thread.sleep(6000);
					 }    else if (communityCode.equalsIgnoreCase("HDG-163")) {
						  driver.get("pcc.hompage");
						  driver.findElement(By.xpath("//*[@id=\"pccFacLink\"]")).click();
						  Thread.sleep(2000);
						  driver.findElement(By.id("facSearchFilter")).sendKeys("Dimensions Living - Appleton - ALF - 163");
						  Thread.sleep(2000);
						  driver.findElement(By.className("pccButton")).click();
						  Thread.sleep(2000);
						  driver.findElement(By.xpath("//a[text()='Dimensions Living - Appleton - ALF - 163']")).click();
						  Thread.sleep(6000);
					 }	  else if (!communityCode.equalsIgnoreCase("HDG-120")) {
						  driver.get("pcc.hompage");
						  driver.findElement(By.xpath("//*[@id=\"pccFacLink\"]")).click();
						  Thread.sleep(2000);
						  
					      System.out.println("Pass");
						
						  Thread.sleep(6000); 	
					 } 	
				
					/*
					 * String originalWindow = driver.getWindowHandle();
					 * driver.switchTo().newWindow(WindowType.TAB);
					 * driver.get("https://www25.pointclickcare.com/home/home.jsp?ESOLnewlogin=N");
					 * driver.findElement(By.xpath("//*[@id=\"pccFacLink\"]")).click();
					 * driver.findElement(By.xpath("//*[@id=\"optionList\"]/li[10]/a")).click();
					 * driver.switchTo().window(originalWindow);
					 */
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
		} catch (Exception e) {
			System.out.println("Element Not Found");
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
			Thread.sleep(10000);
			
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Clipboard clipboard = toolkit.getSystemClipboard();
			StringSelection strSel = new StringSelection(errorFilePath);
			clipboard.setContents(strSel, null);
			
			Robot rb = new Robot();

			rb.keyPress(KeyEvent.VK_CONTROL);
			rb.keyPress(KeyEvent.VK_P);
			rb.keyRelease(KeyEvent.VK_CONTROL);
			rb.keyRelease(KeyEvent.VK_P);
			Thread.sleep(5000);
//			// Screen src=new Screen();
//			// Pattern fptn2 = new Pattern("C:\\Users\\ER\\Documents\\Files\\Capture.png");
//			// src.type(fptn2, "Errorreport");
//			Thread.sleep(1000);
//
			rb.keyPress(KeyEvent.VK_ENTER);
			rb.keyRelease(KeyEvent.VK_ENTER);

			Thread.sleep(3000);
			rb.keyPress(KeyEvent.VK_CONTROL);
			rb.keyPress(KeyEvent.VK_V);
			rb.keyRelease(KeyEvent.VK_V);
			rb.keyRelease(KeyEvent.VK_CONTROL);
			Thread.sleep(2000);

			rb.keyPress(KeyEvent.VK_ENTER);
			rb.keyRelease(KeyEvent.VK_ENTER);
			Thread.sleep(5000);
			
//			rb.keyPress(KeyEvent.VK_ALT);
//			rb.keyPress(KeyEvent.VK_F4);
//			rb.keyRelease(KeyEvent.VK_ALT);
//			rb.keyRelease(KeyEvent.VK_F4);
//			

//			ChromeOptions option = new ChromeOptions();
//			option.addArguments("headless");
//			ChromeDriver driver = new ChromeDriver(option);
//			String originalWindow = driver.getWindowHandle();
//			Thread.sleep(1000);
//			Pdf pdf = ((PrintsPage) driver).print(new PrintOptions());
//			Files.write(Paths.get(errorFilePath), OutputType.BYTES.convertFromBase64Png(pdf.getContent()));
//			log.info("File Imported and Downloaded Exception Report");
//			Thread.sleep(2000);

		//	Robot rb = new Robot();
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
