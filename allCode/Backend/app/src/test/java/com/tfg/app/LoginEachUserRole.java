package com.tfg.app;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;

import java.time.Duration;

@SpringBootTest
public class LoginEachUserRole {

  private WebDriver driver;
  JavascriptExecutor js;

  @Before
  public void setUp() {
    System.setProperty("webdriver.chrome.driver",
        "C:\\Users\\sercu_zc5bz5j\\Desktop\\chromedriver-win64\\chromedriver.exe");
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--remote-allow-origins=*");
    driver = new ChromeDriver(options);
    js = (JavascriptExecutor) driver;
  }

  @After
  public void tearDown() {
    driver.quit();
  }

  void logout(){
    // Esperar y hacer clic en el elemento que despliega el menú
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement menuToggle = wait
        .until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@class='dropdown-toggle nav-link user-link']")));
    menuToggle.click();

    // Hacer clic en "Cerrar sesión"
    WebElement logoutLink = wait
        .until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Cerrar sesión')]")));
    logoutLink.click();
  }

  @Test
  public void loginSuperAdmin() {
    driver.get("https://smilelink.es/");
    driver.manage().window().setSize(new Dimension(1936, 1056));
    driver.findElement(By.cssSelector(".form-group:nth-child(1) > .form-control")).click();
    driver.findElement(By.cssSelector(".form-group:nth-child(1) > .form-control")).sendKeys("admin@smilelink.es");
    driver.findElement(By.cssSelector(".pass-input")).sendKeys("superpassword12345");
    driver.findElement(By.cssSelector(".btn")).click();
    logout();

  }

  @Test
  public void loginAdminEntidad() {
    // Test name: Log in Admin Entidad
    // Step # | name | target | value | comment
    // 1 | open | / | |
    driver.get("https://smilelink.es/");
    // 2 | setWindowSize | 1552x840 | |
    driver.manage().window().maximize();
    // 3 | click | css=.form-group:nth-child(1) > .form-control | |
    driver.findElement(By.cssSelector(".form-group:nth-child(1) > .form-control")).click();
    // 4 | type | css=.form-group:nth-child(1) > .form-control |
    // admin100@smilelink.com |
    driver.findElement(By.cssSelector(".form-group:nth-child(1) > .form-control")).sendKeys("admin100@smilelink.es");
    // 5 | type | css=.pass-input | 12345 |
    driver.findElement(By.cssSelector(".pass-input")).sendKeys("12345");
    // 6 | click | css=.btn | |
    driver.findElement(By.cssSelector(".btn")).click();
    logout();
  }

  @Test
  public void loginDoctor() {
    driver.get("https://smilelink.es/");
    driver.manage().window().maximize();
    driver.findElement(By.cssSelector(".form-group:nth-child(1) > .form-control")).click();
    driver.findElement(By.cssSelector(".form-group:nth-child(1) > .form-control"))
        .sendKeys("jaime.flores@smilelink.es");
    driver.findElement(By.cssSelector(".pass-input")).sendKeys("pass");
    driver.findElement(By.cssSelector(".pass-input")).sendKeys(Keys.ENTER);
    logout();
  }

  @Test
  public void loginPatient() {
    driver.get("https://smilelink.es/");
    driver.manage().window().maximize();
    driver.findElement(By.cssSelector(".form-group:nth-child(1) > .form-control")).click();
    driver.findElement(By.cssSelector(".form-group:nth-child(1) > .form-control"))
        .sendKeys("sercua.flores@gmail.com");
    driver.findElement(By.cssSelector(".pass-input")).sendKeys("pass");
    driver.findElement(By.cssSelector(".pass-input")).sendKeys(Keys.ENTER);
    logout();
  }
}
