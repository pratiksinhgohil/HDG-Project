package com.pcc.utils;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;

import com.pcc.app.Application;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ImportFile extends ImportFileOr {
	WebDriver driver;

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
		uploadfile.sendKeys(filePath);//"C://FTP File//HDG_invout_HDG-2_20201130_TEST3_29-12-2022 13-36-24.csv"
		Thread.sleep(2000);
	}

	public void loadfile() throws InterruptedException {
		loadbtn.click();
		Thread.sleep(2000);
	}

	public void exception() throws AWTException, InterruptedException {
		exc_repo.click();
		Thread.sleep(2000);
		Robot rb = new Robot();
		rb.keyPress(KeyEvent.VK_CONTROL);
		rb.keyPress(KeyEvent.VK_S);
		rb.keyRelease(KeyEvent.VK_CONTROL);
		rb.keyRelease(KeyEvent.VK_S);
		Thread.sleep(4000);
		String downloadFilepath = "C:/Users/ER/Documents/QRC";
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("download.default_directory", downloadFilepath);
		String s = Keys.chord(Keys.CONTROL, "enter");
		ChromeOptions options = new ChromeOptions();
		Map<String, Object> prefs = new HashMap<String, Object>();
	    prefs.put("profile.default_content_settings.popups", 0);
	    prefs.put("download.default_directory",System.getProperty("user.dir") + File.separator + "externalFiles" + File.separator + "New folder"+ "");
		options.setExperimentalOption("prefs", prefs);
		//ChromeDriver driver= new ChromeDriver(options)
		rb.keyPress(KeyEvent.VK_ENTER);
		rb.keyRelease(KeyEvent.VK_ENTER);
		Thread.sleep(5000);
		log.info("File Imported and Downloaded Exception Report");
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
