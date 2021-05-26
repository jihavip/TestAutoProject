package automation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Login extends BaseTest{
	WebDriverWait wait;
    @BeforeMethod
    public void setUp() {
        driver = new FirefoxDriver();
        driver.get(URL_login);
    }
 
    @AfterMethod
    public void tearDown() {
        driver.quit();
    }
 
	@Test
	public void LoginKhongNhapUser() {
		driver.findElement(By.id(user_login)).sendKeys("");
		driver.findElement(By.id(user_pass)).sendKeys("123456");
		driver.findElement(By.id(submitBtn)).click();
		//lấy đoạn text tb lỗi
		String TxtBoxContent = driver.findElement(By.id("login_error")).getText();
		System.out.println(TxtBoxContent);
		// so sánh với đoạn đoạn text mẫu
		verifyCompare(TxtBoxContent,"LỖI: Trường tên người dùng trống.");
	}
	@Test
	public void LoginKhongNhapPass() {
		driver.findElement(By.id(user_login)).sendKeys("hantt");
//		không nhập pass
//		driver.findElement(By.id(user_pass)).sendKeys("");
		driver.findElement(By.id(submitBtn)).click();
		//lấy đoạn text tb lỗi
		String TxtBoxContent = driver.findElement(By.id("login_error")).getText();
		System.out.println(TxtBoxContent);
		// so sánh với đoạn đoạn text mẫu
		verifyCompare(TxtBoxContent,"Lỗi: Xin điền mật khẩu.");
	}
	@Test
	public void LoginThanhCong() throws InterruptedException  {
//		user với quyền addmin
		driver.findElement(By.id(user_login)).sendKeys("hanttautotest");
		driver.findElement(By.id(user_pass)).sendKeys("123456aA@_");
		driver.findElement(By.id(submitBtn)).click();
		pause(5000);
		String UrlLogined =driver.getCurrentUrl();
		verifyCompare(UrlLogined, "http://localhost/wordpress/wp-admin/");
	}
	//
	@Test
	public void LogoutThanhCong() throws InterruptedException{
		login("hanttautotest","123456aA@_");
		mouseOver("//a[contains(text(),'Chào, ')]");
		pause(5000);
		driver.findElement(By.xpath("//*[@class='ab-item' and contains(text(),'Đăng xuất')]")).click();
		pause(5000);
		String UrlLogOutTrue =driver.getCurrentUrl();
		verifyCompare(UrlLogOutTrue, "http://localhost/wordpress/wp-login.php?loggedout=true");

	}
	
	@Test
	public void LoginThanhCongEditor() throws InterruptedException  {
//		user với quyền addmin
		driver.findElement(By.id(user_login)).sendKeys("nguoidonggop");
		driver.findElement(By.id(user_pass)).sendKeys("123456aA");
		driver.findElement(By.id(submitBtn)).click();
		pause(5000);
		String UrlLogined =driver.getCurrentUrl();
		verifyCompare(UrlLogined, "http://localhost/wordpress/wp-admin/");
	}
}
