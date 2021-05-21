package automation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.Inet4Address;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TakesScreenshot;
import org.testng.Assert;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import static org.testng.Assert.assertFalse;


public class CommonBase {
	public WebDriver driver;
	protected String baseUrl = "";
	protected int DEFAULT_TIMEOUT = 60000;
	protected int WAIT_INTERVAL = 1000;
	public int loopCount = 0;
	public final int ACTION_REPEAT = 5;
	public Actions action;

	public WebDriver initDriverTest(String...URL){
		String url1 = URL.length > 0 ? URL[0] : baseUrl;
		String proName = URL.length > 2 ? URL[2] : "";
		String broName = URL.length > 1 ? URL[1] : "firefox";
		String url2 = System.getProperty("Url");
		String browser = System.getProperty("browser");
		String plaForm = System.getProperty("platForm");
		WebDriver dr = null;
		
		if(browser == null){
			browser = broName;
		}
		if("chrome".equals(browser)){
			if ("".equals(plaForm)){
				System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
			}else {
				System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\driver\\chromedriver.exe");
			}
			dr = new ChromeDriver();
		} else if ("iexplorer".equals(browser)){
			dr = new InternetExplorerDriver();
		} else if ("sfive".equals(browser)){
			ChromeOptions options = new ChromeOptions();
			if ("ViettelOS".equals(plaForm)) {
				System.setProperty("webdriver.chrome.driver","/usr/bin/chromedriver");
				options.setBinary("/usr/bin/sfive-browser-stable");
			} else {
				System.setProperty("webdriver.chrome.driver",System.getProperty("user.dir") + "\\driver\\chromedriver.exe");
				options.setBinary("");
				options.addArguments("enable-vsa-modify-header");
				options.setExperimentalOption("excludeSwitches", new String[]{"remote-debugging-port=0"});
//				options.addArguments("user-data-dir=" + System.getProperty("user.dir") + "\\sfive_profile");
			}
			dr = new ChromeDriver(options);
		}
		else if ("safari".equals(browser)){
			dr = new SafariDriver();
		}else {
			if (proName != null && proName != ""){
				FirefoxProfile ffprofile = new FirefoxProfile(new File(proName));
				dr = new FirefoxDriver(ffprofile);
			}else {
				FirefoxProfile fxProfile = new FirefoxProfile();
				fxProfile.setPreference("browser.download.folderList",2);
				fxProfile.setPreference("browser.download.manager.showWhenStarting",false);
				fxProfile.setPreference("browser.download.dir","E:\\apk");
				fxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk","text/csv,application/vnd.android.package-archive,application/apk,application/java-archive");				
				dr = new FirefoxDriver(fxProfile);
			}
		}
		if (url2 != "" && url2 != null){
			dr.get(url2);
		} else {
			dr.get(url1);
		}
		dr.manage().window().maximize();
		return dr;
	}
	
	/**
	 * 
	 * @param driver
	 */
	public void waitForPageLoaded(WebDriver driver) {
        ExpectedCondition<Boolean> expectation = new
                ExpectedCondition<Boolean>() {
                    public Boolean apply(WebDriver driver) {
                        return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete");
                    }
                };
        try {
            Thread.sleep(1000);
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(expectation);
        } catch (Throwable error) {
            Assert.fail("Timeout khi cho trang web hoan thanh load");
        }
    }

	
	/**
	 * Open page
	 * @param pageUrl
	 * @param driver
	 */
	public void openPage(String pageUrl, WebDriver driver) {
		String urlSys = System.getProperty("Url");
		System.out.println("Url sys" + urlSys);
		if (pageUrl != null && !pageUrl.contains("http://")){
			if(urlSys != null && urlSys != ""){
				driver.get(urlSys + "/" + pageUrl);
				System.out.println("vao nhanh sys "+ urlSys + "/" + pageUrl);
			}else {
				driver.get(baseUrl + "/" + pageUrl);
				System.out.println("vao nhanh base "+ baseUrl + "/" + pageUrl);
			}
		}else{
			driver.get(pageUrl);
		}
		waitForPageLoaded(driver);
		pause(1000);
	}
	
	/**
	 * Open page at not loaded status, as clear cache
	 * @param pageUrl
	 * @param driver
	 */
	public void openPageNotLoad(String pageUrl, WebDriver driver){
		if (pageUrl != null){
			driver.get(pageUrl);
			pause(2000);
		}
	}
	
	/**
	 * pause driver in timeInMillis
	 * @param timeInMillis
	 */
	public void pause(long timeInMillis) {
		try {
			Thread.sleep(timeInMillis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param locator
	 * @return
	 */
	public WebElement getElement(Object locator) {
		By by = locator instanceof By ? (By)locator : By.xpath(locator.toString());
		WebElement elem = null;
		try {
			elem = driver.findElement(by);
		} catch (NoSuchElementException e) {
			checkCycling(e, 10);
			pause(WAIT_INTERVAL);
			getElement(locator);
		} catch (StaleElementReferenceException e){
			checkCycling(e, 10);
			pause(WAIT_INTERVAL);
			getElement(locator);
		}
		return elem;
	}
	
	/**
	 * get a display element in web page
	 * @param locator
	 * @return
	 */
	public WebElement getDisplayedElement(Object locator) {
		By by = locator instanceof By ? (By)locator : By.xpath(locator.toString());
		WebElement e = null;
		try {
			if(by != null)
				e = driver.findElement(by);
			if (e != null){
				if (isDisplay(by)) return e;
			}
		} catch (NoSuchElementException ex) {
			checkCycling(ex, DEFAULT_TIMEOUT/WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			getDisplayedElement(locator);
		}catch(StaleElementReferenceException ex)
		{
			checkCycling(ex, 10);
			pause(WAIT_INTERVAL);
			getDisplayedElement(locator);
		}
		finally{
			loopCount=0;
		}
		return null;
	}
	
	/**
	 * 
	 * @param locator
	 * @return
	 */
	public List<WebElement> getListElement(Object locator){
		By by = locator instanceof By ? (By)locator : By.xpath(locator.toString());
		List <WebElement> elementOptions;
		try {
			elementOptions = driver.findElements(by);
		    return elementOptions;
		} catch(NoSuchElementException ex){
			checkCycling(ex, 10);
			pause(WAIT_INTERVAL);
			getListElement(locator);
		} catch(StaleElementReferenceException ex){
			checkCycling(ex, 10);
			pause(WAIT_INTERVAL);
			getListElement(locator);
		} finally {
			loopCount = 0;
		}
		return null;
	}
	
	/**
	 * lay cac gia tri thuoc tinh cua 1 mang doi tuong element
	 * @param locator
	 * @param attribute
	 * @return
	 */
	public String[] getAttOfListElement(Object locator, String attribute){
		String[] att = new String[20];
		List <WebElement> list;
		list = getListElement(locator);
		if (list.size() > 0){
			for (int i = 0; i< list.size(); i ++){
				att[i] = list.get(i).getAttribute(attribute);
			}
		}		
		return att;
	}
	
	 public String[] getTextOfListElement(Object locator){
	    String[] att = new String[20];
	    List <WebElement> list;
	    list = getListElement(locator);
	    if (list.size() > 0){
	    	for (int i = 0; i< list.size(); i ++){
	    		att[i] = list.get(i).getText();
	    	}
	    }  
	    return att;
	}
	 
	public String getSizeOfListElement(Object locator){
		return String.valueOf(getListElement(locator).size());
	}
	
	/**
	 * checking an element is displayed in web page
	 * @param locator
	 * @return
	 */
	public boolean isDisplay(Object locator) {
		boolean bool = false;
		WebElement e = getElement(locator);
		try{
			if (e!=null)
				bool = e.isDisplayed();
		}catch(StaleElementReferenceException ex)
		{
			checkCycling(ex, 10);
			pause(WAIT_INTERVAL);
			isDisplay(locator);
		}
		finally{
			loopCount=0;
		}
		return bool;
	}
	
	/**
	 * check repeat times
	 * @param e
	 * @param loopCountAllowed
	 */
	public void checkCycling(Exception e, int loopCountAllowed) {
		System.out.println("Co exception xay ra: " + e.getClass().getName());
		if (loopCount > loopCountAllowed) {
			Assert.fail("Qua thoi gian nhung khong thay hoac thay doi tuong " + e.getMessage());
		}
		System.out.println("Lap lai lan thu " + loopCount);
		loopCount++;
	}
	
	/**
	 * get an element that present in Web Page
	 * @param locator
	 * @param opParams
	 * @return
	 */
	public WebElement getElementPresent(Object locator, int... opParams) {
		WebElement elem = null;
		int timeout = opParams.length>0 ? opParams[0] : DEFAULT_TIMEOUT;
		int isAssert = opParams.length > 1 ? opParams[1]: 1;
		int notDisplayE = opParams.length > 2 ? opParams[2]: 0;
		for (int tick = 0; tick < timeout/WAIT_INTERVAL; tick++) {
			if (notDisplayE == 2){
				elem = getElement(locator);
			}else{
				elem = getDisplayedElement(locator);
			}
			if (null != elem) return elem;
			pause(WAIT_INTERVAL);
		}
		if (isAssert == 1){
			String date = getDateTime("yyyyMMddHHmmss");
			System.out.println("date");
			captureScreen(driver, "Loi_" + date + ".jpg");
			assert false: ("Qua thoi gian " + timeout + "ma khong tim thay doi tuong " + locator);
			quitDriver(driver);}
		return null;
	}
	
	/**
	 * input data to element
	 * @param locator
	 * @param value
	 * @param validate
	 */
	public void type(Object locator, String value, boolean validate, boolean...clear) {	
		boolean clean = clear.length > 0 ? clear[0]: true;
		WebDriverWait wait = new WebDriverWait(driver, 10);
		try {
		for (int loop = 1;; loop++) {
			if (loop >= ACTION_REPEAT) {
				Assert.fail("Qua thoi gian khi input du lieu: " + value + " vao doi tuong " + locator);
			}
			WebElement element = getElementPresent(locator, 10000, 0);	
			if (element != null){
				wait.until(ExpectedConditions.visibilityOf(element));
				if (clean) element.clear();
//				element.click();
				element.sendKeys(value);
				if (!validate || value.equals(getValue(locator))) {
					break;
				}
			}
			System.out.println("Lap lai tac dong input text lan thu " + loop);
			pause(WAIT_INTERVAL);
		}
		} catch (StaleElementReferenceException e) {
			checkCycling(e, DEFAULT_TIMEOUT/WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			type(locator, value, validate);
		} catch (NoSuchElementException e) {
			checkCycling(e, DEFAULT_TIMEOUT/WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			type(locator, value, validate);
		} catch (ElementNotVisibleException e) {
			checkCycling(e, DEFAULT_TIMEOUT/WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			type(locator, value, validate);
		} finally {
		loopCount = 0;
		}
	}
	
	public void typeElementIfDisplay(Object obj, String value, int... opParams ){
		int timeout = opParams.length>0 ? opParams[0] : DEFAULT_TIMEOUT;
		if (getElementPresentNoAssert(obj, timeout) != null){
			type(obj, value, true, true);
		}
	}
	
	/**
	 * 
	 * @param locator
	 * @param value
	 */
	public void inputTextJavaScript(Object locator, String value){
		WebElement e = getElementPresent(locator, DEFAULT_TIMEOUT, 1, 2);
		try {
			((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = '" + value +"'", e);
		}catch(StaleElementReferenceException ex){
			pause(1000);
			inputTextJavaScript(locator, value);
		}
	}
	
	/**
	 * get value of element in web page
	 * @param locator
	 * @return
	 */
	public String getValue(Object locator, Object... opParams) {
		int notDisplay = (Integer) (opParams.length > 0 ? opParams[0]: 0);	
		try {
			return getElementPresent(locator, DEFAULT_TIMEOUT, 1, notDisplay).getAttribute("value");
		} catch (StaleElementReferenceException e) {
			checkCycling(e, DEFAULT_TIMEOUT/WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			return getValue(locator);
		} finally {
			loopCount = 0;
		}
	}
	
	/**
	 * click on an element
	 * @param locator
	 * @param opParams
	 */
	public void click(Object locator, Object... opParams) {
		int notDisplay = (Integer) (opParams.length > 0 ? opParams[0]: 0);	
		Actions actions = new Actions(driver);
		try {
			WebElement element = getElementPresent(locator, DEFAULT_TIMEOUT, 1, notDisplay);
			if(element.isEnabled()){
				actions.click(element).perform();
			}
			else {
				System.out.println("Element is not enabled");
//				click(locator, notDisplay);
			}
		} catch (StaleElementReferenceException e) {
			checkCycling(e, DEFAULT_TIMEOUT/WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			click(locator, notDisplay);
		} catch (ElementNotVisibleException e) {
			checkCycling(e, DEFAULT_TIMEOUT/WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			click(locator, notDisplay);
		} catch (NoSuchElementException e) {
			checkCycling(e, DEFAULT_TIMEOUT/WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			click(locator, notDisplay);
		} finally {
			loopCount = 0;
		}
	}
	
	public void clickNotPassAction(Object locator, Object... opParams) {
		int notDisplay = (Integer) (opParams.length > 0 ? opParams[0]: 0);	
		try {
			WebElement element = getElementPresent(locator, DEFAULT_TIMEOUT, 1, notDisplay);
			if(element.isEnabled()){
				element.click();
			}
			else {
				System.out.println("Element is not enabled");
			}
		} catch (StaleElementReferenceException e) {
			checkCycling(e, DEFAULT_TIMEOUT/WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			click(locator, notDisplay);
		} catch (ElementNotVisibleException e) {
			checkCycling(e, DEFAULT_TIMEOUT/WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			click(locator, notDisplay);
		} catch (NoSuchElementException e) {
			checkCycling(e, DEFAULT_TIMEOUT/WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			click(locator, notDisplay);
		} finally {
			loopCount = 0;
		}
	}
	
	/**
	 * click on an element using javascript
	 * @param obj
	 */
	public void clickJavascript(Object locator, Object...opParams){
		int notDisplay = (Integer) (opParams.length > 0 ? opParams[0]: 0);	
		try {
			WebElement element = getElementPresent(locator, DEFAULT_TIMEOUT, 1, notDisplay);
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);arguments[0].click();", element);	
		}catch (StaleElementReferenceException e){
			checkCycling(e, DEFAULT_TIMEOUT/WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			clickJavascript(locator, opParams);
		}
	}
	
	/**
	 * 
	 * @param locator
	 * @param opParams
	 * @return
	 */
	public WebElement waitForElementNotPresent(Object locator, int... opParams) {
		WebElement elem = null;
		int timeout = opParams.length > 0 ? opParams[0] : DEFAULT_TIMEOUT;
		int isAssert = opParams.length > 1 ? opParams[1]: 1;
		int notDisplayE = opParams.length > 2 ? opParams[2]: 0;

		for (int tick = 0; tick < timeout/WAIT_INTERVAL; tick++) {
			if (notDisplayE == 2){
				elem = getElement(locator);
			}else{
				elem = getDisplayedElement(locator);
			}
			if (elem == null) {
				return null;
			}
			pause(WAIT_INTERVAL);
		}
		if (isAssert == 1){
			assert false: ("Timeout after " + timeout + "ms waiting for element not present: " + locator);
		}
		return elem;
	}
	
	public void checkElementNotPresent(Object locator, int... opParams){
		WebElement ele;
		By by = locator instanceof By ? (By)locator : By.xpath(locator.toString());
		int timeout = opParams.length > 0 ? opParams[0] : DEFAULT_TIMEOUT;
		for (int tick = 0; tick < timeout/WAIT_INTERVAL; tick++){
			try{
				ele = driver.findElement(by);
				if (tick == timeout/WAIT_INTERVAL && ele != null){
					Assert.fail("Qua thoi gian " + timeout + " nhung doi tuong " + locator + " van dang xuat hien");
				}
			} catch (NoSuchElementException e) {
				pause(WAIT_INTERVAL);
			} catch (StaleElementReferenceException e){
				pause(WAIT_INTERVAL);
			}
		}
	}
	
	/**
	 * 
	 * @param locator
	 * @param opParams
	 */
	public void check(Object locator, int...opParams) {
		int notDisplayE = opParams.length > 0 ? opParams[0]: 0;
		Actions actions = new Actions(driver);
		try {
			WebElement element = getElementPresent(locator, DEFAULT_TIMEOUT, 1, notDisplayE);
			boolean a = element.getAttribute("class").contains("ui-state-active");
			if (!element.isSelected() && !a) {
				actions.click(element).perform();
			} else {
				System.out.println("Element " + locator + " is already checked.");
			}
		} catch (StaleElementReferenceException e) {
			checkCycling(e, DEFAULT_TIMEOUT/WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			check(locator);
		} finally {
			loopCount = 0;
		}
	}
	
	/**
	 * 
	 * @param locator
	 * @param opParams
	 */
	public void uncheck(Object locator, int...opParams) {
		int notDisplayE = opParams.length > 0 ? opParams[0]: 0;
		Actions actions = new Actions(driver);
		try {
			WebElement element = getElementPresent(locator, DEFAULT_TIMEOUT, 1, notDisplayE);

			if (element.isSelected()) {
				actions.click(element).perform();
			} else {
				System.out.println("Element " + locator + " is already unchecked.");
			}
		} catch (StaleElementReferenceException e) {
			checkCycling(e, 5);
			pause(1000);
			uncheck(locator);
		} finally {
			loopCount = 0;
		}
	}
	
	/**
	 * get absolute path of file
	 * @param relativeFilePath
	 * @return
	 */
	public String getAbsoluteFilePath(String relativeFilePath){
		String curDir = System.getProperty("user.dir");
		String absolutePath = curDir + relativeFilePath;
		return absolutePath;
	}
	
	/**
	 * @param locator
	 */
	public void doubleClickOnElement(Object locator) {
		Actions actions = new Actions(driver);
		try {
			WebElement element = getElementPresent(locator);
			actions.doubleClick(element).perform();
		} catch (StaleElementReferenceException e) {
			checkCycling(e, 5);
			pause(1000);
			doubleClickOnElement(locator);
		} finally {
			loopCount = 0;
		}
	}
	
	public void contextClick(Object locator) {
		Actions actions = new Actions(driver);
		try {
			WebElement element = getElementPresent(locator);
			actions.contextClick(element).perform();
		} catch (StaleElementReferenceException e) {
			checkCycling(e, 5);
			pause(1000);
			contextClick(locator);
		} finally {
			loopCount = 0;
		}
	}
	
	/**
	 * get text of element
	 * @param locator
	 * @return
	 */
	public String getText(Object locator) {
		WebElement element = null;
		try {
			element = getElementPresent(locator);
			return element.getText();
		} catch (StaleElementReferenceException e) {
			checkCycling(e, DEFAULT_TIMEOUT/WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			return getText(locator);
		} finally {
			loopCount = 0;
		}
	}
	
	/**
	 * 
	 * @param locator
	 * @param safeToSERE
	 * @param opParams
	 */
	public void mouseOver(Object locator, boolean safeToSERE, Object...opParams) {
		WebElement element;
		Actions actions = new Actions(driver);
		int notDisplay = (Integer) (opParams.length > 0 ? opParams[0]: 0);
		try {
			if (safeToSERE) {
				for (int i = 1; i < ACTION_REPEAT; i++){
					System.out.println("Thuc hien mouserover repeat lan thu " + i);
					element = getElementPresent(locator, 2000, 0, notDisplay);
					System.out.println("Doi tuong " + element);
					if (element == null){
						pause(WAIT_INTERVAL);
					} else {
						System.out.println("Thuc hien action");
						actions.moveToElement(element).build().perform();
						break;
					}
				}
			} else {
				element = getElementPresent(locator);
				actions.moveToElement(element).build().perform();
			}
		} catch (StaleElementReferenceException e) {
			checkCycling(e, DEFAULT_TIMEOUT/WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			mouseOver(locator, safeToSERE);
		} finally {
			loopCount = 0;
		}
	}
	
	/**
	 * 
	 * @param locator
	 * @param opParams
	 */
	public void mouseOverAndClick(Object locator, Object...opParams) {
		WebElement element;
		int notDisplay = (Integer) (opParams.length > 0 ? opParams[0]: 0);
		Actions actions = new Actions(driver);
		try {
			element = getElementPresent(locator, DEFAULT_TIMEOUT, 1, notDisplay);
			actions.moveToElement(element).click(element).build().perform();
		}catch (StaleElementReferenceException e){
			mouseOverAndClick(locator, opParams);
		}
	}

	/**
	 * quit driver if driver existed
	 * @param dr
	 */
	public void quitDriver(WebDriver dr){
		if(dr.toString().contains("null")){
			System.out.print("All Browser windows are closed ");
		}else{
			driver.manage().deleteAllCookies();
			dr.quit();
		}
	}
	
	/**
	 * switch to a frame
	 * @param locator
	 * @param opParams
	 */
	public void switchToFrame(Object locator, Object...opParams){
		System.out.println("Switch to frame " + locator);
		int notDisplay = (Integer) (opParams.length > 0 ? opParams[0]: 0);
		try {
			driver.switchTo().frame(getElementPresent(locator, DEFAULT_TIMEOUT, 1, notDisplay));
		}catch(Exception e){
			switchToFrame(locator, notDisplay);
		}
	}
	
	/**
	 * back to main frame
	 */
	public void switchToParentFrame(){
		try {
			driver.switchTo().defaultContent();
		}catch(Exception e){
			switchToParentFrame();
		}
	}
	
	/**
	 * accept unexpected alert
	 */
	public void acceptAlert(){
		 try {
		     Alert alert = driver.switchTo().alert();
		     alert.accept();

		 } catch (NoAlertPresentException ex) {
		       System.out.println("No Alert present");;
		 }
	}
	
	/**
	 * get datetime 
	 * @param format
	 */
	public String getDateTime(String format){
		DateFormat dateFormat = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		String dateTime = dateFormat.format(cal.getTime());
		System.out.println("time at moment is " + dateTime);
		return dateTime;
	}
	
	public String subtractDate(String format, String delta){
        Date currentDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
		int del = Integer.parseInt(delta);
		c.add(Calendar.DATE, -del);
		return formatDateToString(c.getTime(), format);
	}
	
	public String addDate(String format, String delta){
        Date currentDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
		int del = Integer.parseInt(delta);
		c.add(Calendar.DATE, del);
		return formatDateToString(c.getTime(), format);
	}
	
	public String addDate(String date, String format, String delta){
        Date currentDate = formatDate(format, date);
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
		int del = Integer.parseInt(delta);
		c.add(Calendar.DATE, del);
		return formatDateToString(c.getTime(), format);
	}
	
	public String formatDateToString(Date date, String format){
		DateFormat f = new SimpleDateFormat(format);
		String dat = "";
		try {
		dat = f.format(date);
		} catch (Exception e){
			System.out.println("Exception: " + e);
			System.out.println("Can not convert date");
		}
		System.out.println("dat " + dat);
		return dat;
	}
	/**
	 * @param format
	 * @param date
	 * @return
	 */
	public Date formatDate(String format, String date){
		SimpleDateFormat f = new SimpleDateFormat(format);
		Date dat = null;
		try {
		dat = f.parse(date);
		} catch (Exception e){
			System.out.println("Exception: " + e);
			System.out.println("Can not convert date");
		}
		return dat;
	}
	
	/**
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public String formatDateToString(String date, String format){
		DateFormat f = new SimpleDateFormat(format);
		String dat = "";
		try {
		dat = f.format(date);
		} catch (Exception e){
			System.out.println("Exception: " + e);
			System.out.println("Can not convert date");
		}
		System.out.println("dat" + dat);
		return dat;
	}
	
	/**
	 * @param date
	 * @param format
	 * @return
	 */
	public String formatDateToString(String date, String originalFormat, String intendedFormat){
		DateFormat oriFormat = new SimpleDateFormat(originalFormat);
		DateFormat intendFormat = new SimpleDateFormat(intendedFormat);
		String dat = "";
		try {
		dat = intendFormat.format(oriFormat.parse(date));
		} catch (Exception e){
			System.out.println("Exception: " + e);
			System.out.println("Can not convert date");
		}
		System.out.println("dat" + dat);
		return dat;
	}
	
	/**
	 * @param locator
	 * @param att
	 * @return
	 */
	public String getAttribute(Object locator, String att, int... opParams){
		try {
			return getElementPresent(locator, opParams).getAttribute(att);
		} catch (StaleElementReferenceException e) {
			checkCycling(e, DEFAULT_TIMEOUT/WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			return getValue(locator);
		} finally {
			loopCount = 0;
		}
	}
	
	/**
	 * 
	 * @param object
	 */
	public void clickTab(By object){
		if (object != null){
			WebElement e = getElementPresent(object);
			e.sendKeys(Keys.TAB);
		}
	}
	
	/**
	 * 
	 * @param xpath
	 * @param att
	 * @return
	 */
	public String getAttributeFromJavaScript(String xpath, String att){
		WebElement e = getElementPresent(xpath);	
		JavascriptExecutor executor = (JavascriptExecutor)driver;
		String value = (String)executor.executeScript(" return arguments[0].getAttribute('" + att + "')", e);
		System.out.println("value" + value);
		return value;
	}
	
	/**
	 * 
	 * @param format
	 * @param number
	 * @return
	 */
	public String formatCurrency(String number){
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		System.out.println(formatter.format(Integer.parseInt(number)));
	    return formatter.format(Integer.parseInt(number)).replace(" đ", "");
	}

	/**
	 * 
	 * @return
	 */
	public String getIpOfMachinhe(){
		String ip = "";
		try {
			ip = Inet4Address.getLocalHost().getHostAddress();
			System.out.println("IP of local machine: " + ip);
		} catch(Exception e){
			System.out.println("Exeption: " + e);
		}
		return ip;
	}
	
	/**
	 * 
	 * @param locator
	 * @param opParams
	 */
	public void scrollToElement(Object locator, Object...opParams){
		int notDisplay = (Integer) (opParams.length > 0 ? opParams[0]: 0);	
		WebElement element = getElementPresent(locator, DEFAULT_TIMEOUT, 1, notDisplay);
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);	
	}
	
	/**
	 * type cho cac input dang so
	 * @param locator
	 * @param text
	 * @param validate
	 */
	public void typeHinput(Object locator, String text, boolean validate){
		if (text != null){
			if (getElementPresent(locator) != null) {
				try {
					WebElement e = getElementPresent(locator);
					e.click();
					e.sendKeys(Keys.CONTROL + "a");
					e.sendKeys(text);
					for (int i = 0; i < 5; i++){
						String am = getValue(locator, 2);
						if (am != null){
							if (am.equalsIgnoreCase(text)){
								break;
							} else {
								e.sendKeys(Keys.CONTROL + "a");
								e.sendKeys(text);
							}
						}
					}
				}catch (StaleElementReferenceException ex) {
					typeHinput(locator, text, validate);
				}
			}			
		}
	}
	
	/**
	 * compare 2 string
	 * @param s1
	 * @param s2
	 */
	public void verifyCompare(String s1, String s2){
		if (s1 != "" && s1 != null && s2!= null && s2 != ""){
			Assert.assertFalse(!s1.equalsIgnoreCase(s2), "So sanh khong bang nhau: " + s1 + " va " + s2);
		} else if ((s1 == "" || s1 == null) && (s2 == "" || s2==null)){
			System.out.println("2 truong du lieu can so sanh deu null");
		} else {
			Assert.fail("Du lieu so sanh co 1 truong bi null");
		}		
	}
	
	/**
	 * check field is null = ""
	 * @param s
	 */
	public void verifyNull(String s){
		if (!s.equalsIgnoreCase("")){
			Assert.fail("Du lieu khong null");
		}
	}
	/**
	 * 
	 * @param dateBefore
	 * @param dateAfter
	 */
	public void compareDateBeforeDate(String dateBefore, String dateAfter){
		Boolean compare = false;
		try{				
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			compare = sdf.parse(dateBefore).before(sdf.parse(dateAfter));
			System.out.println("Ket qua so sanh thoi gian: " + compare);
		}catch(Throwable e){
			System.out.println("Loi:" + e);
		}
		Assert.assertFalse(!compare, "Moc thoi gian " + dateAfter + " khong lon hơn " + dateBefore);
	}
	
	public void verifyContains(String s1, String s2){
		if (s1 != null && s2 != null){	
			Assert.assertFalse(!s2.contains(s1),"Chuỗi " + s1 + " không nằm trong chuỗi " + s2);			
		}		
	}
	
	/**
	 * 
	 * @param xpath
	 * @param option
	 */
	public void selectOptionFromCombobox(String xpath, String option){
		if (option != null){
			String locator = xpath.replaceAll("&option", option);
			click(locator);
			waitForElementNotPresent(locator,10000, 0);
		}
	}
	
	public void waitOptionAfterSelectFromCombox(String xpath, String data){
		getElementPresent(By.xpath(xpath.replaceAll("&option", data)), DEFAULT_TIMEOUT, 2);
	}
	
	
	/**
	 * 
	 * @param tbSearch
	 * @param textSearch
	 * @param xpath
	 */
	public void selectOptionFromComBoxSearch(Object tbSearch, String textSearch, String xpath){
		type(tbSearch, textSearch, true, false);
		pause(500);
		click(By.xpath(xpath.replaceAll("&option", textSearch)));
		pause(1000);
	}
	
	/**
	 * 
	 * @param object
	 * @param autocompletename
	 * @param option
	 */
	public void selectOptionFromAutocomplete(Object object, String autocompletename, String option, String... pannelID){		
		String autocomplete = "//*[contains(@class,'ui-autocomplete-panel')]//th[text()='" + autocompletename + "']";
		String opt = "//*[contains(@class,'ui-autocomplete-panel')]//th[text()='" + autocompletename + "']/../../..//*[contains(text(),'" + option + "')]";
		if (pannelID.length > 0){
			autocomplete = "//*[contains(@class,'ui-autocomplete-panel') and contains(@id,'"+ pannelID[0] + "')]//th[text()='" + autocompletename + "']";
			opt = "//*[contains(@class,'ui-autocomplete-panel') and contains(@id,'"+ pannelID[0] + "')]//th[text()='" + autocompletename + "']/../../..//*[contains(text(),'" + option + "')]";
		}
		type(object, option, true, true);
		getElementPresent(autocomplete);
		clickJavascript(opt);
		waitForElementNotPresent(autocomplete);
	}
	
	/**
	 * @param driver
	 */
	public void captureScreen(WebDriver driver, String fileName ){
		try {
		File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		String dir = System.getProperty("user.dir");
		FileUtils.copyFile(scrFile, new File(dir + "\\capture_screen\\" + fileName));
		} catch (Exception e){
			System.out.println("Khong capture duoc man hinh");
		}
	}
	
	/**
	 * parse to object from a xpath contains option
	 * @param xpathOption
	 * @param option
	 * @return
	 */
	public String parseStringToObject(String xpathOption, String option){
		String xpath = xpathOption.replaceAll("&option", option);
		return xpath;
	}
	
	/**
	 * select option from combobox with FW 1.0
	 * @param selectObject
	 * @param index
	 */
	public void selectOptionFW1(Object selectObject, int index){
	    WebElement ele = getElementPresent(selectObject);
	    Select select = new Select(ele);
	    select.selectByIndex(index);
	    pause(1000);
	}
	
	public String trimCharactor(String input, String trim){
		System.out.println("Xau can xu ly trim: " + input);
		if (input != "" && input != null && trim != ""){
			if (trim =="."){
				return input.replaceAll("\\.", "");
			}else{
				return input.replaceAll(trim, "");
			}
		} else
			return "";
	}
	
	/**
	 * get first day of month of next month
	 * @param addMonth
	 * @return
	 */
	public String getFirstDayOfMonth(int addMonth){
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Calendar cal = Calendar.getInstance();
		System.out.println("Current date: " + df.format(cal.getTime()));
		cal.add(Calendar.MONTH, addMonth);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return df.format(cal.getTime());
	}
	
	/**
	 * 
	 * @param file
	 * @param filePath
	 */
	public void uploadFile(Object file, String filePath){
		WebElement e = getElement(file);
		System.out.println("Upload file " + getAbsoluteFilePath("\\file_to_upload\\" + filePath));
		e.sendKeys(getAbsoluteFilePath("\\file_to_upload\\" + filePath));
	}
	
	/**
	 * 
	 * @param profileTypeCode
	 * @param fileNameUpload
	 */
	public void uploadProfileCM(String profileTypeCode, String fileNameUpload){
		WebElement e = getElement("//label[contains(text(),'[" + profileTypeCode + "]')]/../../../..//input[@type='file']");
		e.sendKeys(getAbsoluteFilePath("\\file_to_upload\\" + fileNameUpload));
		getElement(By.xpath("//a[contains(@id,'btnOverPreviewFile') and text()='" + fileNameUpload + "']"));
	}
	
	/**
	 * 
	 * @param urlText
	 * @param data
	 * @param column
	 * @return
	 */
	public static String[] callWS(String urlText, String data, String...column)  {
		String output = "";
		String[] da = new String[100];
		String urlT = "";
		if (urlText != "" && !urlText.contains("http")){
			urlT = System.getProperty("serviceLink") + urlText;
		}else {
			urlT = urlText;
		}
		try {
		    URL url = new URL(urlT);
		    URLConnection connection = url.openConnection();
		    connection.setDoOutput(true);
		    connection.setRequestProperty("Content-Type", "text/xml");
		    connection.setRequestProperty("Accept", "text/xml");
		    System.out.println("connection: " + connection);
		    OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
		    osw.write(data);
		    osw.flush();
		    osw.close();
		    InputStream in = connection.getInputStream();
		    output = read(in);
		    System.out.println("output cua WS: " + output);
		    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		    InputSource src= new InputSource();
		    src.setCharacterStream(new StringReader(output));
		    Document doc = builder.parse(src);
		    if (column.length > 0) {
		    	for (int i = 0; i < column.length; i++){
		    		if (doc.getElementsByTagName(column[i]).item(0).getTextContent() != null){
					    da[i] = doc.getElementsByTagName(column[i]).item(0).getTextContent();
					    System.out.println("Gia tri trong tag " + column[i] + "la: " + da[i]);
		    		}
		    	}
		    }
		} catch(IOException | ParserConfigurationException | SAXException e){
			e.printStackTrace();
			assertFalse(true, "Co loi khi thuc hien WS");
			
		}
	    return da;
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static String read(InputStream input) throws IOException {
	    try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
	        return buffer.lines().collect(Collectors.joining("\n"));
	    }
	}
	
	public String getValueOfSystemVariable(String var){
		return System.getProperty(var);
	}
	
	
	
	
	


	public WebElement getElementPresentNoAssert(Object locator, int... opParams) {
		 WebElement elem = null;
		 int timeout = opParams.length>0 ? opParams[0] : DEFAULT_TIMEOUT;
		 By by = locator instanceof By ? (By)locator : By.xpath(locator.toString());
		 for (int tick = 0; tick < timeout/WAIT_INTERVAL; tick++) {
		 try {
		    elem = driver.findElement(by);
		    if (null != elem) return elem;
		    pause(WAIT_INTERVAL);
		    System.out.println("Lap lai lan thu " + tick + 1);  
		  } catch (NoSuchElementException ex) {
		    if (tick==timeout/WAIT_INTERVAL) {
		     return null;
		    }
		  }catch (WebDriverException e){
		    if (tick==timeout/WAIT_INTERVAL) {
		     return null;
		    }
		  } catch (IllegalStateException e){
		    if (tick==timeout/WAIT_INTERVAL) {
		     return null;
		    }
		  }
		 }
		  return elem;  
	}
	
	/**
	 * tra ve so lan xuat hien cua 1 xau trong chuoi
	 * @param value
	 * @param array
	 * @return
	 */
	public String checkValueInArray(String value, String[] array){
		int soLan = 0;
		if (value != null && value != "" && array.length > 0){
			for (int i=0; i < array.length; i++){
				if (value.equalsIgnoreCase(array[i])){
					soLan ++;
					System.out.println("Chuoi \"" + value + "\" xuat hien trong mang lan thu: " + soLan);
				}
			}
		return String.valueOf(soLan);
		}else {
			System.out.println("Chuoi can kiem tra dang null hoac mang khong co phan tu");
			return "";
		}
	}
	
	/**
	 * 
	 * @param string
	 * @param split
	 * @return
	 */
	public String[] convertStringToArray(String string, String split){
		String[] a = new String[100];
		if (string != null && string != ""){
			a = string.split(split);
		} else {
			System.out.println("Xau ky tu can chuyen sang mang ");
		}
		return a;
	}
	
	public String subString(String s, int start, int end){
		return s.substring(start, end);
	}
	
	public void verifyNotNull(String s){
	    if (s==null || s==""){
	     Assert.fail("Du lieu  null");
	    }
	}
	
	public void clickElementIfDisplay(Object obj, int... opParams ){
		int timeout = opParams.length>0 ? opParams[0] : DEFAULT_TIMEOUT;
		if (getElementPresentNoAssert(obj, timeout) != null){
			click(obj);
		}
	}
	
	public void switchNewTab(int... index){
		int tab = index.length>0 ? index[0] : 1;
	    ArrayList<String> tabs2 = new ArrayList<String> (driver.getWindowHandles());
	    driver.switchTo().window(tabs2.get(tab));
	}
	
	public void verifyCompareNotEqual(String s1, String s2){
	    if (s1 != "" && s1 != null && s2!= null && s2 != ""){
	     Assert.assertTrue(!s1.equalsIgnoreCase(s2), "Pass neu 2 truong so sanh khong bang nhau: " + s1 + " va " + s2);
	    } else if ((s1 == "" || s1 == null) && (s2 == "" || s2==null)){
	     System.out.println("2 truong du lieu can so sanh deu null");
	    } else {
	     Assert.fail("Du lieu so sanh co 1 truong bi null");
	    }  
	}
	
	/**
	 * 
	 * @return dinh dang Tháng 1
	 */
	public String getAndFormatMonth(String... delta){
		String del = delta.length > 0 ? delta[0]:"0";
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, - Integer.parseInt(del));
		int month = cal.get(Calendar.MONTH) + 1 ;
		return "Tháng " + month;
	}
}
