package automation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

	WebDriver driver;
	public int ACTION_REPEAT = 10;
	public int WAIT_DEFAULT = 1000;
	public static final String URL_login = "http://localhost/wordpress/wp-login.php";
	public static final String URL_dashBoard = "http://localhost/wordpress/wp-admin/";
	String user_login = "user_login";
	String user_pass = "user_pass";
	String submitBtn = "wp-submit";
	WebDriverWait wait;
	private Connection conn;

	public void verifyCompare(String s1, String s2) {
		if (s1 != "" && s1 != null && s2 != null && s2 != "") {
			Assert.assertFalse(!s1.equalsIgnoreCase(s2), "So sanh khong bang nhau: " + s1 + " va " + s2);
		} else if ((s1 == "" || s1 == null) && (s2 == "" || s2 == null)) {
			System.out.println("2 truong du lieu can so sanh deu null");
		} else {
			Assert.fail("Du lieu so sanh co 1 truong bi null");
		}
	}

	public void verifyContainsText(String s1, String s2) {
		if (s1 != "" && s1 != null && s2 != null && s2 != "") {
			Assert.assertFalse(!s1.contains(s2), "Chuoi" + s1 + " khong chua chuoi " + s2);
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

	public void waitForPageLoaded(WebDriver driver) {
		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString()
						.equals("complete");
			}
		};
		try {
			Thread.sleep(1000);
			WebDriverWait wait = new WebDriverWait(driver, 20);
			wait.until(expectation);
		} catch (Throwable error) {
			Assert.fail("Timeout khi cho trang web hoan thanh load");
		}
	}

	public void connect(String urlDB, String port, String username, String password) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://" + urlDB + ":" + port, username, password);
			System.out.println("Connect db thanh cong");
//			Statement st = conn.createStatement();
//			ResultSet r = st.executeQuery("select post_title,ID from wordpress.wp_posts limit 5");
//			while (r.next()) {
//				System.out.println(r.getString(1) + " ----------- " + r.getString(2));
//		expectedList.add(r.getString(1);)
//			}
		} catch (ClassNotFoundException ex) {
			System.out.println("Co loi : " + ex);
		} catch (SQLException ex) {
			System.out.println("Co loi : " + ex);
		}

	}

	public ResultSet executeSql(String query) {
//		try {
//			Statement st = conn.createStatement();
//		
//		ResultSet r=st.executeQuery(query);
//		while(r.next()) {
//		System.out.println(r.getString(1)+" ----------- "+r.getString(2));
//		}
//		}catch (SQLException ex) {
//			System.out.println("Co loi xay ra "+ex);
//		}

		try {
			Statement state = conn.createStatement();
			System.out.println("Thuc thi cau lenh " + query);
			ResultSet rs = state.executeQuery(query);
			return rs;
		} catch (Exception e) {
			System.out.println("Khong thuc thi duoc cau lenh: " + query);
			Assert.assertFalse(false);
		}
		return null;
	}

	public String[][] getData(String sql, String... params) {
		String[][] data = new String[100][100];
		try {
			System.out.println("Thuc thi cau lenh " + sql);
			ResultSet rs = executeSql(sql);
			int i = 0;
			if (rs != null) {
				while (rs.next()) {
					String[] currData = new String[params.length];
					for (int j = 0; j < params.length; j++) {
						System.out.println("Get collumn name " + params[j]);
						currData[j] = rs.getString(params[j]);
					}
					data[i] = currData;
					i++;
				}
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
			e.printStackTrace();
			return data;
		}
		return data;
	}

	public void close() {
		try {
			if (conn != null) {
				conn.close();
				System.out.println("Close connect to DB");
			}
		} catch (Exception e) {
			System.out.println("Khong the close connection to DB");
		}
	}

	public WebElement getElement(String xpath) {
		WebElement elem = null;

		try {
			elem = driver.findElement(By.xpath(xpath));
		} catch (InvalidSelectorException e) {
			System.out.println("Khong tim thay doi tuong " + xpath);
			for (int i = 0; i < 5; i++) {
				elem = driver.findElement(By.xpath(xpath));
				pause(1000);
			}
		}
		return elem;
	}

	public String getText(String xpath) {
		WebElement element = null;
		try {
			element = driver.findElement(By.xpath(xpath));
			return element.getText();
		} catch (StaleElementReferenceException e) {
			pause(WAIT_DEFAULT);
			return getText(xpath);
		}

	}

}
