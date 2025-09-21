package com.fantacarmo.fanta_carmo_pro.service;

import com.fantacarmo.fanta_carmo_pro.entity.GiocatoreEntity;
import com.fantacarmo.fanta_carmo_pro.entity.LegaFantaEntity;
import com.fantacarmo.fanta_carmo_pro.entity.SquadraFantaEntity;
import com.fantacarmo.fanta_carmo_pro.repository.GiocatoreRepository;
import com.fantacarmo.fanta_carmo_pro.repository.LegaFantaRepository;
import com.fantacarmo.fanta_carmo_pro.repository.SquadraFantaRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FantacalcioScraperService {

    @Value("${fantacalcio.username}")
    private String username;

    @Value("${fantacalcio.password}")
    private String password;

    private final GiocatoreRepository giocatoreRepository;
    private final LegaFantaRepository legaFantaRepository;
    private final SquadraFantaRepository squadraFantaRepository;

    @Transactional
    public void scrapeLegheFantacalcio() {
        log.info("Avvio scraping per le leghe su Fantacalcio.it...");

        List<GiocatoreEntity> tuttiGiocatoriDB = giocatoreRepository.findAll();

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless"); // Decommenta per modalità headless
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            driver.get("https://leghe.fantacalcio.it/login");
            log.info("Navigato alla pagina di login.");

            // Aspetta e inserisci username
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder='Username']")));
            usernameField.sendKeys(username);
            log.info("Inserito username.");

            // Aspetta e inserisci password
            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder='Password']")));
            passwordField.sendKeys(password);
            log.info("Inserita password.");

            // Aspetta e clicca sul bottone Accedi
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[span[contains(text(), 'LOGIN')]]")));
            loginButton.click();
            log.info("Cliccato su Accedi. Login effettuato.");

            // Verifica se login ok (es. redirect a homepage; aggiungi wait per elemento post-login)
            wait.until(ExpectedConditions.urlContains("home"));  // O qualsiasi URL post-login

            LegaFantaEntity lega = legaFantaRepository.findAll().stream()
                    .filter(l -> "Lega FantaLipari League".equals(l.getNome()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Lega FantaLipari League non trovata nel DB"));

            String urlRose = "https://leghe.fantacalcio.it/fantaliparigroup/rose";
            driver.get(urlRose);
            log.info("Navigato alla pagina rose: {}", urlRose);

            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".team-card-wrapper")));

            List<WebElement> teamBoxes = driver.findElements(By.cssSelector(".team-card-wrapper"));
            log.info("Trovati {} team box.", teamBoxes.size());

            for (WebElement teamBox : teamBoxes) {
                String nomeSquadraFanta = teamBox.findElement(By.cssSelector(".team-name")).getText().trim();

                SquadraFantaEntity squadraFanta = squadraFantaRepository.findByNomeAndLegaFanta(nomeSquadraFanta, lega)
                        .orElseGet(() -> {
                            log.info("Creata nuova squadra Fanta: {}", nomeSquadraFanta);
                            SquadraFantaEntity nuovaSquadra = new SquadraFantaEntity();
                            nuovaSquadra.setNome(nomeSquadraFanta);
                            nuovaSquadra.setLegaFanta(lega);
                            if ("Nico o Pazz".equalsIgnoreCase(nomeSquadraFanta)) {
                                nuovaSquadra.setMiaSquadra(true);
                            }
                            return squadraFantaRepository.save(nuovaSquadra);
                        });

                squadraFanta.setGiocatori(new HashSet<>());

                List<WebElement> playersInTeam = teamBox.findElements(By.cssSelector("li.player-info"));
                for (WebElement playerElement : playersInTeam) {
                    String nomeGiocatoreScraped = playerElement.findElement(By.cssSelector(".player-name")).getText().trim();

                    findPlayerMatch(nomeGiocatoreScraped, tuttiGiocatoriDB).ifPresent(giocatoreTrovato -> {
                        squadraFanta.getGiocatori().add(giocatoreTrovato);
                    });
                }

                squadraFantaRepository.save(squadraFanta);
                log.info("Salvata squadra '{}' con {} giocatori.", nomeSquadraFanta, squadraFanta.getGiocatori().size());
            }

        } catch (Exception e) {
            log.error("Errore durante lo scraping", e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    // Metodo findPlayerMatch invariato...
    private Optional<GiocatoreEntity> findPlayerMatch(String scrapedName, List<GiocatoreEntity> allPlayers) {
        String cleanScrapedName = scrapedName.toUpperCase().replace(".", "");
        String[] parts = cleanScrapedName.split(" ");
        if (parts.length == 0) return Optional.empty();

        String lastName = parts[0];
        String firstNameInitial = (parts.length > 1) ? parts[1] : null;

        for (GiocatoreEntity playerDB : allPlayers) {
            String dbNameUpper = playerDB.getNome().toUpperCase();
            if (dbNameUpper.contains(lastName)) {
                if (firstNameInitial != null) {
                    if (dbNameUpper.startsWith(firstNameInitial)) {
                        return Optional.of(playerDB);
                    }
                } else {
                    return Optional.of(playerDB);
                }
            }
        }
        log.warn("Nessuna match per giocatore: {}", scrapedName);
        return Optional.empty();
    }
}