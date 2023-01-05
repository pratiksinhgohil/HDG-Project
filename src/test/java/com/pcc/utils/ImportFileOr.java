package com.pcc.utils;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ImportFileOr {
	@FindBy(how=How.XPATH,using="//*[@id=\"un\"]")
	public WebElement uname;

	@FindBy(how=How.XPATH,using="//*[@id=\"id-next\"]")
	public WebElement nextbtn;

	@FindBy(how=How.ID,using="password")
	public WebElement pwd;

	@FindBy(how=How.ID,using="id-submit")
	public WebElement submit;

	@FindBy(how=How.XPATH,using="//*[@id=\"QTF_GLTab\"]/a")
	public WebElement hover;

	@FindBy(how=How.XPATH,using="//*[@id=\"QTF_GLTab\"]/div/div[1]/ul/li[3]/a")
	public WebElement acpay;
	
	@FindBy(how=How.XPATH,using="/html/body/form/table/tbody/tr[5]/td/input[2]")
	public WebElement impfile;

	@FindBy(how=How.XPATH,using="//*[@id=\"fileUpload\"]/td/input[1]")
	public WebElement uploadfile;

	@FindBy(how=How.XPATH,using="//td[@class=\"data\"]/input[@type=\"button\" and @class=\"pccButton\"][1]")
	public WebElement loadbtn;

	@FindBy(how=How.XPATH,using="//*[@id=\"msg\"]/input[1]")
	public WebElement exc_repo;

	
}
