package Mail_scheduler;

import java.awt.AWTException;
import java.awt.Desktop.Action;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import org.checkerframework.checker.units.qual.Acceleration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.idealized.Javascript;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;

import ObjectRepo.Import_fileOR;

public class Import_File extends Import_fileOR  {
	WebDriver driver;
	
	public Import_File(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
public void username() throws InterruptedException {
	uname.sendKeys("hdg.hcubtech");
	Thread.sleep(2000);
	nextbtn.click();
	Thread.sleep(2000);
}
public void password() throws InterruptedException {
	pwd.sendKeys("TntraHappyCub22");
	Thread.sleep(2000);
}
public void submit() throws InterruptedException {
	submit.click();
	Thread.sleep(2000);
	
}
public void hovermenu() throws InterruptedException {
	Actions ac=new Actions(driver);
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
public void uploadfile() throws InterruptedException {
	String oldTab = driver.getWindowHandle();
	ArrayList<String> tabs2 = new ArrayList<String> (driver.getWindowHandles());
	tabs2.remove(oldTab);
	driver.switchTo().window(tabs2.get(0));
	uploadfile.sendKeys("C://FTP File//HDG_invout_HDG-2_20201130_TEST3_29-12-2022 13-36-24.csv");
	Thread.sleep(2000);
}
public void loadfile() throws InterruptedException {
	loadbtn.click();
	Thread.sleep(2000);
}		
		
public void exception() throws AWTException, InterruptedException {
	exc_repo.click();
	Thread.sleep(2000);
	Robot rb=new Robot();
	rb.keyPress(KeyEvent.VK_ENTER);
	rb.keyRelease(KeyEvent.VK_ENTER);
	
}
		
				
		
		
		
		
		
		
		
		
	/*	driver.switchTo().window(oldTab);
		
		Thread.sleep(4000);
		rb.keyPress(KeyEvent.VK_CONTROL);
		rb.keyPress(KeyEvent.VK_TAB);
		rb.keyRelease(KeyEvent.VK_CONTROL);
		rb.keyRelease(KeyEvent.VK_TAB);
		rb.keyPress(KeyEvent.VK_CONTROL);
		rb.keyPress(KeyEvent.VK_S);
		rb.keyRelease(KeyEvent.VK_CONTROL);
		rb.keyRelease(KeyEvent.VK_S); */
		
		
		
		//Actions ac1=new Actions(driver);
		//ac1.moveToElement(up).click();
	
		//up.sendKeys("C:\\FTP File\\HDG_invout_HDG-2_20201130_TEST3_29-12-2022 13-36-24.csv");
		//up.click();

	}


