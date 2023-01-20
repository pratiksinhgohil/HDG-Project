package com.pcc.utils;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
//import org.sikuli.script.Pattern;
//import org.sikuli.script.Screen;

import com.pcc.app.Application;

import io.netty.channel.ThreadPerChannelEventLoopGroup;
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

	public void exception(String csvFileName, String errorFilePath) throws Exception {

		System.out.println("Uploaded file path" + csvFileName);
		System.out.println("Error report PDF " + errorFilePath);
		System.out.println("exc_repo.getAccessibleName() >>> " + exc_repo.getAccessibleName());
		if (exc_repo.getAccessibleName().equalsIgnoreCase("Exceptions Report")) {

			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Clipboard clipboard = toolkit.getSystemClipboard();
			StringSelection strSel = new StringSelection(errorFilePath);
			clipboard.setContents(strSel, null);
			exc_repo.click();
			Thread.sleep(4000);
			Robot rb = new Robot();

			rb.keyPress(KeyEvent.VK_CONTROL);
			rb.keyPress(KeyEvent.VK_P);
			rb.keyRelease(KeyEvent.VK_CONTROL);
			rb.keyRelease(KeyEvent.VK_P);
			Thread.sleep(5000);
			// Screen src=new Screen();
			// Pattern fptn2 = new Pattern("C:\\Users\\ER\\Documents\\Files\\Capture.png");
			// src.type(fptn2, "Errorreport");
			Thread.sleep(1000);

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

			
			Thread.sleep(5000);
			log.info("File Imported and Downloaded Exception Report");

			// Send email
			
			//
		} else if (exc_repo.getAccessibleName().equalsIgnoreCase("Commit")) {
			commit();
		} else {
			System.out.println("Unknown button " + exc_repo.getAccessibleName());
		}

	}

	public void commit() throws Exception {
		Thread.sleep(2000);
		commit.click();

		try {
			Thread.sleep(2000);
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			System.out.println("ERROR: (ALERT BOX DETECTED) - ALERT MSG : " + alertText);
			alert.accept();
			Thread.sleep(2000);
			close.click();
			Thread.sleep(5000);
			Robot rb =new Robot();
			rb.keyPress(KeyEvent.VK_ALT);
			rb.keyPress(KeyEvent.VK_F4);
			rb.keyRelease(KeyEvent.VK_ALT);
			rb.keyRelease(KeyEvent.VK_F4);
		} catch (Exception e) {
			
			
			// throw(e);
		}
	}
	
	

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


