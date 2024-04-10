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

import org.openqa.selenium.JavascriptExecutor;
import java.time.Duration;

@SpringBootTest
public class AddPatientTest {

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

    void logout() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement menuToggle = wait
                .until(ExpectedConditions
                        .elementToBeClickable(By.xpath("//a[@class='dropdown-toggle nav-link user-link']")));
        menuToggle.click();

        WebElement logoutLink = wait
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Cerrar sesión')]")));
        logoutLink.click();
    }

    @Test
    public void addPatient() {
      driver.get("https://smilelink.es/");
      driver.manage().window().maximize();
    
      // Espera hasta que el elemento esté presente antes de hacer clic
      new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.elementToBeClickable(By.cssSelector(".form-group:nth-child(1) > .form-control"))).click();
      driver.findElement(By.cssSelector(".form-group:nth-child(1) > .form-control")).sendKeys("entidadtest@smilelink.es");
    
      new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.elementToBeClickable(By.cssSelector(".pass-input"))).click();
      driver.findElement(By.cssSelector(".pass-input")).sendKeys("N55566678");
    
      new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn"))).click();
    
      // Asegurarte de que la página ha cargado completamente antes de buscar el elemento
      new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".sidebar > .sidebarNew:nth-child(3) > #dropdownBasic1"))).click();
      
      // Para dar de alta al paciente, esperar hasta que el link sea clickeable
      new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.elementToBeClickable(By.linkText("Dar de alta paciente"))).click();
    
      driver.findElement(By.name("name")).sendKeys("Paciente1");
      driver.findElement(By.name("lastName")).sendKeys("ApellidosPaciente1");
      driver.findElement(By.id("gender_male")).click();
      driver.findElement(By.name("phone")).sendKeys("666666666");
      driver.findElement(By.name("username")).sendKeys("66666666W");
      driver.findElement(By.name("email")).sendKeys("s.cuadros.2020@alumnos.urjc.es");
      driver.findElement(By.name("birth")).sendKeys("13-02-1996");
      driver.findElement(By.name("address")).sendKeys("calle");
      driver.findElement(By.name("city")).sendKeys("ciudad");
      driver.findElement(By.name("country")).sendKeys("pais");
      driver.findElement(By.name("postalCode")).sendKeys("28000");
      
      // Hacer clic en el botón de guardar y esperar por la confirmación
      driver.findElement(By.cssSelector(".btn-primary")).click();
      
      // Esperar hasta que el botón de confirmación en la alerta sea clickeable
      new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.swal2-confirm"))).click();


      new WebDriverWait(driver, Duration.ofSeconds(10));
    }
}