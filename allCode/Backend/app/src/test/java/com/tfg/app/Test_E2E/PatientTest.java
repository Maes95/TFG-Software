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
public class PatientTest {

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

    void login(String email, String pass) {
        driver.findElement(By.cssSelector(".form-group:nth-child(1) > .form-control")).click();
        driver.findElement(By.cssSelector(".form-group:nth-child(1) > .form-control")).sendKeys(email);
        driver.findElement(By.cssSelector(".pass-input")).sendKeys(pass);
        driver.findElement(By.cssSelector(".pass-input")).sendKeys(Keys.ENTER);
    }

    @Test
    public void addPatient() throws InterruptedException {
        driver.get("https://localhost:" + this.port + "/");
        driver.manage().window().maximize();

        login("jaime.flores@smilelink.es", "pass");

        wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.cssSelector(".sidebar > .sidebarNew:nth-child(3) > #dropdownBasic1")))
                .click();
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Dar de alta paciente"))).click();

        driver.findElement(By.name("name")).sendKeys("Paciente1");
        driver.findElement(By.name("lastName")).sendKeys("ApellidosPaciente1");
        driver.findElement(By.id("gender_male")).click();
        driver.findElement(By.name("phone")).sendKeys("666666666");
        driver.findElement(By.name("username")).sendKeys("66566666W");
        driver.findElement(By.name("email")).sendKeys("s.cuadros.2020@alumnos.urjc.es");
        driver.findElement(By.name("birth")).sendKeys("13-02-1996");
        driver.findElement(By.name("address")).sendKeys("calle");
        driver.findElement(By.name("city")).sendKeys("ciudad");
        driver.findElement(By.name("country")).sendKeys("pais");
        driver.findElement(By.name("postalCode")).sendKeys("28000");
        driver.findElement(By.cssSelector(".btn-primary")).click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//h2[@class='swal2-title' and @id='swal2-title' and text()='Usuario registrado']")));
    }

    @Test
    public void editPatient() {
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
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Editar perfil"))).click();
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");

        wait.until(ExpectedConditions.elementToBeClickable(By.name("address"))).click();

        WebElement addressInput = driver.findElement(By.cssSelector("input[type='text'][name='address']"));
        addressInput.sendKeys("Nueva direccion");

        WebElement saveButton = driver.findElement(By.cssSelector("button.btn.btn-primary.submit-btn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveButton);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.swal2-confirm"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.swal2-confirm"))).click();

        WebElement checkAddress = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//li[span[1][contains(text(), 'Dirección')]]/span[@class='text']")));
        String newAddress = "Nueva direccion, Madrid, 28220, España";
        assertEquals(newAddress, checkAddress.getText());
    }

    @Test
    public void editMyself() {
        driver.get("https://localhost:" + this.port + "/");
        driver.manage().window().maximize();
        login("sercua.flores@gmail.com", "pass");

        WebElement menuToggle = wait
                .until(ExpectedConditions
                        .elementToBeClickable(By.xpath("//a[@class='dropdown-toggle nav-link user-link']")));
        menuToggle.click();

        WebElement profileLink = wait
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Mi perfil')]")));
        profileLink.click();

        menuToggle.click();

        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Editar perfil"))).click();
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");

        wait.until(ExpectedConditions.elementToBeClickable(By.name("address"))).click();

        WebElement addressInput = driver.findElement(By.cssSelector("input[type='text'][name='address']"));
        addressInput.sendKeys("Nueva direccion");

        WebElement saveButton = driver.findElement(By.cssSelector("button.btn.btn-primary.submit-btn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveButton);

        WebElement checkAddress = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//li[span[1][contains(text(), 'Dirección')]]/span[@class='text']")));
        String newAddress = "Nueva direccion, Madrid, 28220, España";
        assertEquals(newAddress, checkAddress.getText());
    }

}