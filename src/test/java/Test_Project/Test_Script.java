package Test_Project;

import java.awt.AWTException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.UsernameAndPassword;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import Mail_scheduler.Import_File;
import ObjectRepo.Import_fileOR;
import io.netty.channel.unix.Socket;

public class Test_Script {
	
	WebDriver driver;
	private String h1;
	
	@BeforeClass
	public void setup() throws InterruptedException, IOException{
		System.setProperty("webdriver.chrome.driver", "C:/Users/Administrator/Downloads/chromedriver_win32new/chromedriver.exe");
		
		driver = new ChromeDriver();
		//driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		//driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.get("https://www25.pointclickcare.com/home/login.jsp?ESOLGuid=40_1672328090402");
		Thread.sleep(2000);
		
		
		
		
		
	}
	
	/*@AfterClass
	public void finish() throws InterruptedException {
		Thread.sleep(2000);
		driver.close();
	}*/
	@AfterClass
	public void closeBrowser()
	{
	    try 
	    {
	        driver.close();
	        Runtime.getRuntime().exec("taskkill /F /IM chromedriver*");

	    }
	    catch (Exception anException) 
	    {
	        anException.printStackTrace();
	    }
	}
	
	
		
	
	
	  @Test 
	  public void Import_File() throws InterruptedException, AWTException {
	  
		 Import_File imf = new Import_File(driver);
		 imf.username();
		 imf.password();
		 imf.hovermenu();
		 imf.ac_pay();
		 imf.browse();
		 imf.uploadfile();
		 imf.loadfile();
		 imf.exception();
	
		 
	  }
	/*  @Test 
	  public void FTP_Connection() throws InterruptedException, AWTException {
		  Mail_scheduler.FTP_Connection conn = new Mail_scheduler.FTP_Connection();
		  
		  conn.con();
		 
	  }
	  
	  @Test 
	  public void Setup_email() throws InterruptedException, AWTException, IOException {
		  Mail_scheduler.Setup_email email = new Mail_scheduler.Setup_email();
		  
		  email.emailsent();
		 
	  }*/


}
