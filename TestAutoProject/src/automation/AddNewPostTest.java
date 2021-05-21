package automation;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AddNewPostTest extends BaseTest {
	String URL_dashBoard = "http://localhost/wordpress/wp-admin/";
	String user_login = "user_login";
	String user_pass = "user_pass";
	String submitBtn = "wp-submit";
	WebDriverWait wait;
	public void login(String userName, String password) {
		driver.findElement(By.id(user_login)).sendKeys(userName);
		driver.findElement(By.id(user_pass)).sendKeys(password);
		driver.findElement(By.id(submitBtn)).click();
	}
	
	@Test
	public void addNewPost() throws InterruptedException {
		login("hanttautotest", "123456aA@_");
		WebDriverWait wait = new WebDriverWait(driver, 60);
		driver.get("http://localhost/wordpress/wp-admin/post-new.php");
		driver.findElement(By.id("title")).sendKeys("This is a title");
		driver.switchTo().frame("content_ifr");
		driver.findElement(By.id("tinymce")).sendKeys("This is the body");
		driver.switchTo().defaultContent();
		System.out.println("click vao publish");
		wait.until(ExpectedConditions.elementToBeClickable(By.id("publish")));
		
		driver.findElement(By.id("publish")).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[text()='View post']")));
//		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//*[text()='View post']"))));
		driver.findElement(By.xpath("//*[text()='View post']")).click();
		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.tagName("h1"))));
		String title = driver.findElement(By.tagName("h1")).getText();
		System.out.println(title);
		Assert.assertEquals(title, "This is a title");
		driver.close();
	}
	

}
