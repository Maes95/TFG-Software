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

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.Duration;

@SpringBootTest(classes = AppApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AppointmentTest {

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
        js = (JavascriptExecutor) driver;

    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    void login(String email, String pass) {
        driver.findElement(By.cssSelector(".form-group:nth-child(1) > .form-control")).click();
        driver.findElement(By.cssSelector(".form-group:nth-child(1) > .form-control")).sendKeys(email);
        driver.findElement(By.cssSelector(".pass-input")).sendKeys(pass);
        driver.findElement(By.cssSelector(".pass-input")).sendKeys(Keys.ENTER);
    }

    @Test
    public void addAppointmentToPatient() {
        driver.get("https://localhost:" + this.port + "/");
        driver.manage().window().maximize();

        login("jaime.flores@smilelink.es", "pass");

        wait.until(ExpectedConditions
                .visibilityOfElementLocated(By
                        .cssSelector(".sidebar > .sidebarNew:nth-child(3) > #dropdownBasic1")))
                .click();
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Lista de pacientes"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Mostrar todos los pacientes"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Sergio"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Dar cita"))).click();

        driver.findElement(By.name("bookDate")).click();
        driver.findElement(By.name("bookDate")).sendKeys("17-04-2024");
        driver.findElement(By.name("fromDate")).sendKeys("17:33");
        driver.findElement(By.name("description")).click();
        wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//option[. = 'Chequeos y limpiezas regulares']")))
                .click();
        WebElement dropdownDoctor = driver.findElement(By.name("doctorName"));
        dropdownDoctor.click();
        dropdownDoctor.findElement(By.xpath("//option[. = 'Dr. Jaime']")).click();
        WebElement element = driver.findElement(By.cssSelector(".btn.btn-primary.submit-form.me-2"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.swal2-confirm"))).click();

        // TO CHECK OK
        wait.until(ExpectedConditions
                .visibilityOfElementLocated(By
                        .cssSelector(".sidebar > .sidebarNew:nth-child(4) > #dropdownBasic1")))
                .click();
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Lista de citas"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Mostrar todos los pacientes"))).click();
        WebElement checkAppointment = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//td[contains(text(), 'Chequeos y limpiezas regulares')]")));

        String toCheck = "Chequeos y limpiezas regulares";
        assertEquals(toCheck, checkAppointment.getText());
    }

    @Test
    public void addAppointmentByPatient() {
        driver.get("https://localhost:" + this.port + "/");
        driver.manage().window().maximize();

        login("sercua.flores@gmail.com", "pass");
    
        WebElement link = wait
                .until(ExpectedConditions.elementToBeClickable(By.linkText("Pedir cita")));
        link.click();
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");

        driver.findElement(By.name("bookDate")).click();
        driver.findElement(By.name("bookDate")).sendKeys("17-04-2024");
        driver.findElement(By.name("fromDate")).sendKeys("17:33");
        driver.findElement(By.name("description")).click();
        wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//option[. = 'Chequeos y limpiezas regulares']")))
                .click();
        WebElement dropdownDoctor = driver.findElement(By.name("doctorName"));
        dropdownDoctor.click();
        dropdownDoctor.findElement(By.xpath("//option[. = 'Dr. Jaime']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(3));
        WebElement element = driver.findElement(By.cssSelector(".btn.btn-primary.submit-form.me-2"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.swal2-confirm"))).click();
    }

}
