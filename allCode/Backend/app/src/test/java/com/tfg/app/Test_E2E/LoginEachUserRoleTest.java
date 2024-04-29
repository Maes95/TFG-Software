package com.tfg.app.Test_E2E;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import com.tfg.app.AppApplication;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import java.time.Duration;

@SpringBootTest(classes = AppApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LoginEachUserRoleTest {

  @LocalServerPort
  int port;

  private static WebDriver driver;
  JavascriptExecutor js;
  private WebDriverWait wait;

  @BeforeAll
  public static void setUpClass() {
    WebDriverManager.chromedriver().setup();
  }

  @BeforeEach
  public void setupTest() {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--remote-allow-origins=*");
    options.setAcceptInsecureCerts(true);
    // options.addArguments("--headless");
    driver = new ChromeDriver(options);
    wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Un único WebDriverWait reutilizable
  }

  @AfterEach
  public void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }

  void logout() {
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
  public void loginSuperAdmin() throws InterruptedException {
    driver.get("https://localhost:" + this.port + "/");
    driver.manage().window().setSize(new Dimension(1936, 1056));
    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".form-group:nth-child(1) > .form-control")));
    driver.findElement(By.cssSelector(".form-group:nth-child(1) > .form-control")).click();
    driver.findElement(By.cssSelector(".form-group:nth-child(1) > .form-control")).sendKeys("admin@smilelink.es");
    driver.findElement(By.cssSelector(".pass-input")).sendKeys("superpassword12345");
    driver.findElement(By.cssSelector(".btn")).click();

    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Dar de alta entidad")));
    // logout();

  }

  // @Test
  // public void loginAdminEntidad() {
  // // Test name: Log in Admin Entidad
  // // Step # | name | target | value | comment
  // // 1 | open | / | |
  // driver.get("https://smilelink.es/");
  // // 2 | setWindowSize | 1552x840 | |
  // driver.manage().window().maximize();
  // // 3 | click | css=.form-group:nth-child(1) > .form-control | |
  // driver.findElement(By.cssSelector(".form-group:nth-child(1) >
  // .form-control")).click();
  // // 4 | type | css=.form-group:nth-child(1) > .form-control |
  // // admin100@smilelink.com |
  // driver.findElement(By.cssSelector(".form-group:nth-child(1) >
  // .form-control")).sendKeys("admin100@smilelink.es");
  // // 5 | type | css=.pass-input | 12345 |
  // driver.findElement(By.cssSelector(".pass-input")).sendKeys("12345");
  // // 6 | click | css=.btn | |
  // driver.findElement(By.cssSelector(".btn")).click();
  // logout();
  // }

  // @Test
  // public void loginDoctor() {
  // driver.get("https://smilelink.es/");
  // driver.manage().window().maximize();
  // driver.findElement(By.cssSelector(".form-group:nth-child(1) >
  // .form-control")).click();
  // driver.findElement(By.cssSelector(".form-group:nth-child(1) >
  // .form-control"))
  // .sendKeys("jaime.flores@smilelink.es");
  // driver.findElement(By.cssSelector(".pass-input")).sendKeys("pass");
  // driver.findElement(By.cssSelector(".pass-input")).sendKeys(Keys.ENTER);
  // logout();
  // }

  // @Test
  // public void loginPatient() {
  // driver.get("https://smilelink.es/");
  // driver.manage().window().maximize();
  // driver.findElement(By.cssSelector(".form-group:nth-child(1) >
  // .form-control")).click();
  // driver.findElement(By.cssSelector(".form-group:nth-child(1) >
  // .form-control"))
  // .sendKeys("sercua.flores@gmail.com");
  // driver.findElement(By.cssSelector(".pass-input")).sendKeys("pass");
  // driver.findElement(By.cssSelector(".pass-input")).sendKeys(Keys.ENTER);
  // logout();
  // }
}
