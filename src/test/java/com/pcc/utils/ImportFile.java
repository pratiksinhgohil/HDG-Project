package com.pcc.utils;

import java.awt.AWTException;
import java.awt.Dialog;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.lang.StackWalker.Option;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Pdf;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.print.PrintOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.PageFactory;

import com.google.common.io.Files;
import com.pcc.app.Application;

import ch.qos.logback.core.joran.action.Action;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImportFile extends ImportFileOr {
	WebDriver driver;
	private String[] dialog;

	public ImportFile(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
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

	public void ac_pay() throws InterruptedException {
		acpay.click();
		Thread.sleep(2000);
	}

	public void browse() throws InterruptedException {
		impfile.click();
		Thread.sleep(2000);
	}

	public void uploadfile(String filePath) throws InterruptedException {
		String oldTab = driver.getWindowHandle();
		ArrayList<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
		tabs2.remove(oldTab);
		driver.switchTo().window(tabs2.get(0));
		uploadfile.sendKeys(filePath);// "C://FTP File//HDG_invout_HDG-2_20201130_TEST3_29-12-2022 13-36-24.csv"
		Thread.sleep(2000);
	}

	public void loadfile() throws InterruptedException {
		loadbtn.click();
		Thread.sleep(2000);
	}

	public void exception(String fileName) throws AWTException, InterruptedException, IOException {
		System.out.println("exc_repo.getAccessibleName() >>> " + exc_repo.getAccessibleName());
		if (exc_repo.getAccessibleName().equalsIgnoreCase("Exceptions Report")) {


			exc_repo.click();
			Thread.sleep(4000);
			Robot rb = new Robot();
			rb.keyPress(KeyEvent.VK_CONTROL);
			rb.keyPress(KeyEvent.VK_P);
			rb.keyRelease(KeyEvent.VK_CONTROL);
			rb.keyRelease(KeyEvent.VK_P);
			Thread.sleep(5000);
			rb.keyPress(KeyEvent.VK_ENTER);
			rb.keyRelease(KeyEvent.VK_ENTER);
			
			 Thread.sleep(3000);
			rb.keyPress(KeyEvent.VK_CONTROL);
			rb.keyPress(KeyEvent.VK_V);
			rb.keyRelease(KeyEvent.VK_V);
			rb.keyRelease(KeyEvent.VK_CONTROL);
			Thread.sleep(3000);


			rb.keyPress(KeyEvent.VK_ENTER);
			rb.keyRelease(KeyEvent.VK_ENTER);
			Thread.sleep(5000);
			
			
			//option.addArguments("--disable-extensions");
			//option.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, false);
			//ChromeDriver driver2= new ChromeDriver(options);
			
			Thread.sleep(5000);
			log.info("File Imported and Downloaded Exception Report");
		} else if (exc_repo.getAccessibleName().equalsIgnoreCase("Commit")) {
			commit();
		} else {
			System.out.println("Unknown button " + exc_repo.getAccessibleName());
		}

	}

	public void commit() {
		commit.click();
		close.click();
	}

	public void close() throws AWTException, InterruptedException {
		driver.quit();

	}
	
	/*
	 * driver.switchTo().window(oldTab);
	 * 
	 * Thread.sleep(4000); rb.keyPress(KeyEvent.VK_CONTROL);
	 * rb.keyPress(KeyEvent.VK_TAB); rb.keyRelease(KeyEvent.VK_CONTROL);
	 * rb.keyRelease(KeyEvent.VK_TAB); rb.keyPress(KeyEvent.VK_CONTROL);
	 * rb.keyPress(KeyEvent.VK_S); rb.keyRelease(KeyEvent.VK_CONTROL);
	 * rb.keyRelease(KeyEvent.VK_S);
	 */

	// Actions ac1=new Actions(driver);
	// ac1.moveToElement(up).click();

	// up.sendKeys("C:\\FTP File\\HDG_invout_HDG-2_20201130_TEST3_29-12-2022
	// 13-36-24.csv");
	// up.click();

}
