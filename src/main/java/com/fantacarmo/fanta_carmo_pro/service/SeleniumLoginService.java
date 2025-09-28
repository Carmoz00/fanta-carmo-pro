package com.fantacarmo.fanta_carmo_pro.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
public class SeleniumLoginService {

    @Value("${fantacalcio.username}")
    private String username;

    @Value("${fantacalcio.password}")
    private String password;

    public WebDriver loginAndGetDriver() {
        log.info("Inizio processo di login con Selenium...");
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = createChromeOptions();
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        try {
            driver.get("https://leghe.fantacalcio.it/login");
            log.info("Pagina di login caricata.");

            handleCookiePopup(driver);

            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder='Username']")));
            usernameField.sendKeys(username);
            log.info("Inserito username.");

            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder='Password']")));
            passwordField.sendKeys(password);
            log.info("Inserita password.");

            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[span[contains(text(), 'LOGIN')]]")));
            loginButton.click();
            log.info("Cliccato su LOGIN.");

            wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("login")));
            log.info("Login completato con successo. Sessione attiva.");

            // Restituisce il driver con la sessione loggata
            return driver;

        } catch (Exception e) {
            log.error("Login fallito!", e);
            // Se il login fallisce, chiudi il driver e lancia un'eccezione
            if (driver != null) {
                driver.quit();
            }
            throw new RuntimeException("Impossibile completare il login su Fantacalcio.it", e);
        }
    }

    private ChromeOptions createChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-web-security");
        options.addArguments("--disable-features=VizDisplayCompositor");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-plugins");
        // Rimuovi le immagini per velocizzare, ma mantieni JS attivo che è fondamentale per il login
        // options.addArguments("--disable-images");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        // options.addArguments("--headless");
        return options;
    }

    private void handleCookiePopup(WebDriver driver) {
        try {
            Thread.sleep(2000);
            String[] cssSelectors = { "button[id*='accept']", "button[class*='accept']", "[data-testid='cookie-accept']", ".cookie-accept", "#cookie-accept" };
            String[] xpathSelectors = { "//button[contains(text(), 'Accetta')]", "//button[contains(text(), 'Accept')]", "//button[contains(text(), 'Accetto')]", "//button[contains(text(), 'OK')]" };

            for (String selector : cssSelectors) {
                if (tryClick(driver, By.cssSelector(selector), "CSS")) return;
            }
            for (String selector : xpathSelectors) {
                if (tryClick(driver, By.xpath(selector), "XPath")) return;
            }
            log.info("Nessun popup cookie ha richiesto interazione.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Thread interrotto durante la gestione del popup cookie.");
        }
    }

    private boolean tryClick(WebDriver driver, By by, String type) {
        try {
            List<WebElement> buttons = driver.findElements(by);
            for (WebElement button : buttons) {
                if (button.isDisplayed() && button.isEnabled()) {
                    button.click();
                    log.info("Popup cookie gestito con selettore {}: {}", type, by);
                    Thread.sleep(1000);
                    return true;
                }
            }
        } catch (Exception e) {
            // Ignora e prova il prossimo
        }
        return false;
    }
}