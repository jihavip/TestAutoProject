package automation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

	WebDriver driver;
	public int ACTION_REPEAT = 10;
	public static final String URL_login = "http://localhost/wordpress/wp-login.php";
	public static final String URL_dashBoard = "http://localhost/wordpress/wp-admin/";
	String user_login = "user_login";
	String user_pass = "user_pass";
	String submitBtn = "wp-submit";
	WebDriverWait wait;

	public void verifyCompare(String s1, String s2) {
		if (s1 != "" && s1 != null && s2 != null && s2 != "") {
			Assert.assertFalse(!s1.equalsIgnoreCase(s2), "So sanh khong bang nhau: " + s1 + " va " + s2);
		} else if ((s1 == "" || s1 == null) && (s2 == "" || s2 == null)) {
			System.out.println("2 truong du lieu can so sanh deu null");
		} else {
			Assert.fail("Du lieu so sanh co 1 truong bi null");
		}
	}

	public void login(String userName, String password) {
		driver.findElement(By.id(user_login)).sendKeys(userName);
		driver.findElement(By.id(user_pass)).sendKeys(password);
		driver.findElement(By.id(submitBtn)).click();
		String UrlLogined = driver.getCurrentUrl();
		verifyCompare(UrlLogined, URL_dashBoard);
	}

	public void mouseOver(String locator) {
		Actions action = new Actions(driver);
		WebElement we = driver.findElement(By.xpath(locator));
		action.moveToElement(we).build().perform();
	}
	
	public void pause(long timeInMillis) {
		try {
			Thread.sleep(timeInMillis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
