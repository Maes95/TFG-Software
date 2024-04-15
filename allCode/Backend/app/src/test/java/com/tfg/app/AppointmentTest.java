package com.tfg.app;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

@SpringBootTest
public class AppointmentTest {

        private WebDriver driver;
        JavascriptExecutor js;
        private WebDriverWait wait;

        @Before
        public void setUp() {
                System.setProperty("webdriver.chrome.driver",
                                "C:\\Users\\sercu_zc5bz5j\\Desktop\\chromedriver-win64\\chromedriver.exe");
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--remote-allow-origins=*");
                driver = new ChromeDriver(options);
                js = (JavascriptExecutor) driver;
                wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        }

        @After
        public void tearDown() {
                driver.quit();
        }

        @Test
        public void addAppointmentToPatient() {
                driver.get("https://smilelink.es/");
                driver.manage().window().maximize();
                wait.until(ExpectedConditions
                                .elementToBeClickable(By.cssSelector(".form-group:nth-child(1) > .form-control")))
                                .sendKeys("jaime.flores@smilelink.es");
                wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".pass-input")))
                                .sendKeys("pass");
                wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn"))).click();
                wait.until(ExpectedConditions
                                .visibilityOfElementLocated(By
                                                .cssSelector(".sidebar > .sidebarNew:nth-child(3) > #dropdownBasic1")))
                                .click();
                wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Lista de pacientes"))).click();
                wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Sergio"))).click();
                wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Dar cita"))).click();
                // wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("bookDate"))).click();
                // JavascriptExecutor js = (JavascriptExecutor) driver;
                // js.executeScript("document.getElementsByName('bookDate')[0].value =
                // '17-06-2024';");
                driver.findElement(By.name("bookDate")).click();
                driver.findElement(By.name("bookDate")).sendKeys("17-04-2024");
                driver.findElement(By.name("fromDate")).sendKeys("17:33");
                wait.until(ExpectedConditions
                                .visibilityOfElementLocated(By.xpath("//option[. = 'Chequeos y limpiezas regulares']")))
                                .click();
                WebElement dropdownDoctor = driver.findElement(By.name("doctorName"));
                dropdownDoctor.click();
                dropdownDoctor.findElement(By.xpath("//option[. = 'Dr. Jaime']")).click();
                WebElement element = driver.findElement(By.cssSelector(".btn.btn-primary.submit-form.me-2"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.swal2-confirm"))).click();
        }

        @Test
        public void addAppointmentByPatient() {
                driver.get("https://smilelink.es/");
                driver.manage().window().maximize();
                wait.until(ExpectedConditions
                                .elementToBeClickable(By.cssSelector(".form-group:nth-child(1) > .form-control")))
                                .sendKeys("sercua.flores@gmail.com");
                wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".pass-input")))
                                .sendKeys("pass");
                wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn"))).click();
                WebElement link = wait
                                .until(ExpectedConditions.elementToBeClickable(By.linkText("Pedir cita")));
                link.click();
                js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
                // JavascriptExecutor js = (JavascriptExecutor) driver;
                // js.executeScript("document.getElementsByName('bookDate')[0].value =
                // '05-06-2024';");
                driver.findElement(By.name("bookDate")).click();
                driver.findElement(By.name("bookDate")).sendKeys("17-04-2024");
                driver.findElement(By.name("fromDate")).sendKeys("17:33");
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
