// package com.tfg.app.Test_E2E;

// import org.junit.jupiter.api.AfterAll;
// import org.junit.jupiter.api.BeforeAll;
// import org.openqa.selenium.By;
// import org.openqa.selenium.JavascriptExecutor;
// import org.openqa.selenium.WebDriver;
// import org.openqa.selenium.WebElement;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.chrome.ChromeOptions;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;
// import org.springframework.boot.test.context.SpringBootTest;

// import java.time.Duration;

// @SpringBootTest
// public class AddInterventionTest {

//     private WebDriver driver;
//     JavascriptExecutor js;
//     private WebDriverWait wait;

//     @BeforeAll
//     public void setUp() {
//         // System.setProperty("webdriver.chrome.driver",
//         //         "C:\\Users\\sercu_zc5bz5j\\Desktop\\chromedriver-win64\\chromedriver.exe");
//         // ChromeOptions options = new ChromeOptions();
//         // options.addArguments("--remote-allow-origins=*");
//         // driver = new ChromeDriver(options);
//         // js = (JavascriptExecutor) driver;
//         wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Un único WebDriverWait reutilizable
//     }

//     @AfterAll
//     public void tearDown() {
//         driver.quit();
//     }

//     @org.junit.jupiter.api.Test
//     public void addAppointmentToPatient() {
//         driver.get("https://localhost/");
//         driver.manage().window().maximize();

//         wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".form-group:nth-child(1) > .form-control")))
//                 .sendKeys("jaime.flores@smilelink.es");

//         wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".pass-input")))
//                 .sendKeys("pass");

//         wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn"))).click();

//         wait.until(ExpectedConditions
//                 .visibilityOfElementLocated(By.cssSelector(".sidebar > .sidebarNew:nth-child(4) > #dropdownBasic1")))
//                 .click();

//         wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Lista de citas"))).click();
//         wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Sergio"))).click();
//         wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Añadir intervención"))).click();
//         wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Mantenimiento y Prevención"))).click();
//         wait.until(ExpectedConditions.elementToBeClickable(By.name("type"))).click();
//         driver.findElement(By.name("type")).sendKeys("Realizada una limpieza");

//         js.executeScript("window.scrollTo(0, document.body.scrollHeight)");

//         wait.until(ExpectedConditions
//                 .visibilityOfElementLocated(By.cssSelector(".btn-outline-success")))
//                 .click();

//         WebElement element = driver.findElement(By.cssSelector(".btn:nth-child(2)"));
//         ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);

//         wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.swal2-confirm"))).click();

//         wait.until(ExpectedConditions
//                 .visibilityOfElementLocated(By.cssSelector(".sidebar > .sidebarNew:nth-child(4) > #dropdownBasic1")))
//                 .click();

//         wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Lista de citas"))).click();
//         wait.until(ExpectedConditions
//                 .visibilityOfElementLocated(By.cssSelector("tr:nth-child(1) > td .form-check-input"))).click();

//         wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.swal2-confirm"))).click();
//         wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.swal2-confirm"))).click();
//     }
// }
