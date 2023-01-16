package com.pcc.utils;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;

import com.pcc.app.Application;

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
		rb.keyPress(KeyEvent.VK_ENTER);
		rb.keyRelease(KeyEvent.VK_ENTER);
		Thread.sleep(5000);
		System.out.println("File Imported and Downloaded Exception Report");
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
