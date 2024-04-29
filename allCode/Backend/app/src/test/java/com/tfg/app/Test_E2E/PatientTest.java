// package com.tfg.app.Test_E2E;

// import org.junit.Test;
// import org.junit.Before;
// import org.junit.After;
// import org.openqa.selenium.By;
// import org.openqa.selenium.WebDriver;
// import org.openqa.selenium.WebElement;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.chrome.ChromeOptions;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;
// import org.springframework.boot.test.context.SpringBootTest;

// import org.openqa.selenium.JavascriptExecutor;
// import java.time.Duration;

// @SpringBootTest
// public class PatientTest {

//     private WebDriver driver;
//     JavascriptExecutor js;
//     private WebDriverWait wait;

//     @Before
//     public void setUp() {
//         System.setProperty("webdriver.chrome.driver",
//                 "C:\\Users\\sercu_zc5bz5j\\Desktop\\chromedriver-win64\\chromedriver.exe");
//         ChromeOptions options = new ChromeOptions();
//         options.addArguments("--remote-allow-origins=*");
//         driver = new ChromeDriver(options);
//         js = (JavascriptExecutor) driver;
//         wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//     }

//     @After
//     public void tearDown() {
//         driver.quit();
//     }

//     void logout() {
//         WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//         WebElement menuToggle = wait
//                 .until(ExpectedConditions
//                         .elementToBeClickable(By.xpath("//a[@class='dropdown-toggle nav-link user-link']")));
//         menuToggle.click();

//         WebElement logoutLink = wait
//                 .until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Cerrar sesión')]")));
//         logoutLink.click();
//     }

//     @Test
//     public void addPatient() {
//         driver.get("https://smilelink.es/");
//         driver.manage().window().maximize();

//         new WebDriverWait(driver, Duration.ofSeconds(3)).until(
//                 ExpectedConditions.elementToBeClickable(By.cssSelector(".form-group:nth-child(1) > .form-control")))
//                 .click();
//         driver.findElement(By.cssSelector(".form-group:nth-child(1) > .form-control"))
//                 .sendKeys("entidadtest@smilelink.es");
//         new WebDriverWait(driver, Duration.ofSeconds(3))
//                 .until(ExpectedConditions.elementToBeClickable(By.cssSelector(".pass-input"))).click();
//         driver.findElement(By.cssSelector(".pass-input")).sendKeys("N55566678");
//         new WebDriverWait(driver, Duration.ofSeconds(3))
//                 .until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn"))).click();
//         new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions
//                 .visibilityOfElementLocated(By.cssSelector(".sidebar > .sidebarNew:nth-child(3) > #dropdownBasic1")))
//                 .click();
//         new WebDriverWait(driver, Duration.ofSeconds(3))
//                 .until(ExpectedConditions.elementToBeClickable(By.linkText("Dar de alta paciente"))).click();
//         driver.findElement(By.name("name")).sendKeys("Paciente1");
//         driver.findElement(By.name("lastName")).sendKeys("ApellidosPaciente1");
//         driver.findElement(By.id("gender_male")).click();
//         driver.findElement(By.name("phone")).sendKeys("666666666");
//         driver.findElement(By.name("username")).sendKeys("66666666W");
//         driver.findElement(By.name("email")).sendKeys("s.cuadros.2020@alumnos.urjc.es");
//         driver.findElement(By.name("birth")).sendKeys("13-02-1996");
//         driver.findElement(By.name("address")).sendKeys("calle");
//         driver.findElement(By.name("city")).sendKeys("ciudad");
//         driver.findElement(By.name("country")).sendKeys("pais");
//         driver.findElement(By.name("postalCode")).sendKeys("28000");
//         driver.findElement(By.cssSelector(".btn-primary")).click();
//         new WebDriverWait(driver, Duration.ofSeconds(3))
//                 .until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.swal2-confirm"))).click();
//         new WebDriverWait(driver, Duration.ofSeconds(10));
//     }

//     @Test
//     public void editPatient() {
//         driver.get("https://smilelink.es/");
//         driver.manage().window().maximize();
//         wait.until(ExpectedConditions
//                 .elementToBeClickable(By.cssSelector(".form-group:nth-child(1) > .form-control")))
//                 .sendKeys("jaime.flores@smilelink.es");
//         wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".pass-input")))
//                 .sendKeys("pass");
//         wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn"))).click();
//         wait.until(ExpectedConditions
//                 .visibilityOfElementLocated(By
//                         .cssSelector(".sidebar > .sidebarNew:nth-child(3) > #dropdownBasic1")))
//                 .click();
//         wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Lista de pacientes"))).click();
//         wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Sergio"))).click();
//         wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Editar perfil"))).click();
//         js.executeScript("window.scrollTo(0, document.body.scrollHeight)");

//         wait.until(ExpectedConditions.elementToBeClickable(By.name("address"))).click();

//         WebElement addressInput = driver.findElement(By.cssSelector("input[type='text'][name='address']"));
//         addressInput.sendKeys("Nueva direccion");

//         WebElement saveButton = driver.findElement(By.cssSelector("button.btn.btn-primary.submit-btn"));
//         ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveButton);
//         wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.swal2-confirm"))).click();
//         wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.swal2-confirm"))).click();
//     }

//     @Test
//     public void editMyself() {
//         driver.get("https://smilelink.es/");
//         driver.manage().window().maximize();
//         wait.until(ExpectedConditions
//                 .elementToBeClickable(By.cssSelector(".form-group:nth-child(1) > .form-control")))
//                 .sendKeys("sercua.flores@gmail.com");
//         wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".pass-input")))
//                 .sendKeys("pass");
//         wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn"))).click();

//         WebElement menuToggle = wait
//                 .until(ExpectedConditions
//                         .elementToBeClickable(By.xpath("//a[@class='dropdown-toggle nav-link user-link']")));
//         menuToggle.click();

//         WebElement profileLink = wait
//                 .until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Mi perfil')]")));
//         profileLink.click();
       
//         menuToggle.click();

//         wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Editar perfil"))).click();
//         js.executeScript("window.scrollTo(0, document.body.scrollHeight)");

//         wait.until(ExpectedConditions.elementToBeClickable(By.name("address"))).click();

//         WebElement addressInput = driver.findElement(By.cssSelector("input[type='text'][name='address']"));
//         addressInput.sendKeys("Nueva direccion");

//         WebElement saveButton = driver.findElement(By.cssSelector("button.btn.btn-primary.submit-btn"));
//         ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveButton);
//     }
// }