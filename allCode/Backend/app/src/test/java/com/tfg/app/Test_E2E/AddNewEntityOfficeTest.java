// package com.tfg.app.Test_E2E;

// import org.openqa.selenium.By;
// import org.openqa.selenium.WebDriver;
// import org.openqa.selenium.WebElement;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.chrome.ChromeOptions;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;
// import org.springframework.boot.test.context.SpringBootTest;

// import org.openqa.selenium.JavascriptExecutor;
// import org.openqa.selenium.Keys;

// import java.time.Duration;

// @SpringBootTest
// public class AddNewEntityOfficeTest {

//     private WebDriver driver;
//     JavascriptExecutor js;

//     @Before
//     public void setUp() {
//         System.setProperty("webdriver.chrome.driver",
//                 "C:\\Users\\sercu_zc5bz5j\\Desktop\\chromedriver-win64\\chromedriver.exe");
//         ChromeOptions options = new ChromeOptions();
//         options.addArguments("--remote-allow-origins=*");
//         driver = new ChromeDriver(options);
//         js = (JavascriptExecutor) driver;
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
//     public void addNewEntityOffice() {
//         driver.get("https://smilelink.es/");
//         driver.manage().window().maximize();
//         driver.findElement(By.cssSelector(".form-group:nth-child(1) > .form-control")).click();
//         driver.findElement(By.cssSelector(".form-group:nth-child(1) > .form-control")).sendKeys("admin@smilelink.es");
//         driver.findElement(By.cssSelector(".pass-input")).sendKeys("superpassword12345");
//         driver.findElement(By.cssSelector(".pass-input")).sendKeys(Keys.ENTER);
//         WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
//         WebElement link = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Dar de alta entidad")));
//         link.click();

//         driver.findElement(By.name("name")).click();
//         driver.findElement(By.name("name")).sendKeys("Entidad2Test");
//         driver.findElement(By.name("lastName")).click();
//         driver.findElement(By.name("lastName")).sendKeys("ApellidoEntidadTest");
//         driver.findElement(By.name("username")).click();
//         driver.findElement(By.name("username")).sendKeys("N555266678");
//         driver.findElement(By.name("email")).click();
//         driver.findElement(By.name("email")).sendKeys("entidad2test@smilelink.es");
//         driver.findElement(By.cssSelector(".btn-primary")).click();

//         WebElement confirmButton = wait
//                 .until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.swal2-confirm")));
//         confirmButton.click();
//         logout();

//         WebElement login = wait
//                 .until(ExpectedConditions
//                         .elementToBeClickable(By.cssSelector(".form-group:nth-child(1) > .form-control")));
//         login.click();
//         driver.findElement(By.cssSelector(".form-group:nth-child(1) > .form-control"))
//                 .sendKeys("entidadTest@smilelink.es");
//         driver.findElement(By.cssSelector(".pass-input")).sendKeys("N55566678");
//         driver.findElement(By.cssSelector(".btn")).click();
//         logout();
//     }

    
    

// }