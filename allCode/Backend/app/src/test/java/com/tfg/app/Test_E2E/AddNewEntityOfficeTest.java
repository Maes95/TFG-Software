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

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;

import java.time.Duration;

@SpringBootTest(classes = AppApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AddNewEntityOfficeTest {

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
        WebElement menuToggle = wait
                .until(ExpectedConditions
                        .elementToBeClickable(By.xpath("//a[@class='dropdown-toggle nav-link user-link']")));
        menuToggle.click();

        // Hacer clic en "Cerrar sesión"
        WebElement logoutLink = wait
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Cerrar sesión')]")));
        logoutLink.click();
    }
    
    void login(String email, String pass){
        driver.findElement(By.cssSelector(".form-group:nth-child(1) > .form-control")).click();
        driver.findElement(By.cssSelector(".form-group:nth-child(1) > .form-control")).sendKeys(email);
        driver.findElement(By.cssSelector(".pass-input")).sendKeys(pass);
        driver.findElement(By.cssSelector(".pass-input")).sendKeys(Keys.ENTER);
    }

    @Test
    public void addNewEntityOffice() throws InterruptedException {
        driver.get("https://localhost:" + this.port + "/");
        driver.manage().window().maximize();
       login("admin@smilelink.es","superpassword12345");
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Dar de alta entidad")));
        link.click();

        driver.findElement(By.name("name")).click();
        driver.findElement(By.name("name")).sendKeys("Entidad2Test");
        driver.findElement(By.name("lastName")).click();
        driver.findElement(By.name("lastName")).sendKeys("ApellidoEntidadTest");
        driver.findElement(By.name("username")).click();
        driver.findElement(By.name("username")).sendKeys("N55526667");
        driver.findElement(By.name("email")).click();
        driver.findElement(By.name("email")).sendKeys("entidadTest@smilelink.es");
        driver.findElement(By.cssSelector(".btn-primary")).click();

        WebElement confirmButton = wait
                .until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.swal2-confirm")));
        confirmButton.click();
        logout();

        WebElement login = wait
                .until(ExpectedConditions
                        .elementToBeClickable(By.cssSelector(".form-group:nth-child(1) > .form-control")));
        login.click();

        login("entidadTest@smilelink.es", "N55526667");

        // ASSERT TO CHECK
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
            "//ul[@class='nav user-menu float-end']//li[@class='nav-item dropdown has-arrow user-profile-list']//div[@class='user-names']//span[text()='Doctor']")));
    }
}