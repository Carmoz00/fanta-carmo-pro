package com.fantacarmo.fanta_carmo_pro.service;

import com.fantacarmo.fanta_carmo_pro.dto.ScrapedPlayerDTO;
import com.fantacarmo.fanta_carmo_pro.entity.LegaFantaEntity;
import com.fantacarmo.fanta_carmo_pro.repository.LegaFantaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class FantacalcioScraperService {

    private final SeleniumLoginService seleniumLoginService;
    private final LegaFantaRepository legaFantaRepository;
    private final FantaTeamService fantaTeamService; // Iniettiamo il nostro nuovo service

    private record LegaConfig(String nomeLega, String slug, String divisione) {
        LegaConfig(String nomeLega, String slug) { this(nomeLega, slug, null); }
    }

    @Transactional
    public void scrapeAllFantacalcioData() {
        log.info("Avvio scraping completo per le leghe su Fantacalcio.it...");

        List<LegaConfig> legheDaScrapare = List.of(
                new LegaConfig("Lega FantaLipari League", "fantaliparigroup", "A"),
                new LegaConfig("Lega Fantasburro League", "fantasburroland"),
                new LegaConfig("Lega FilippoPummaroroLeague", "filippopummaroroleague")
        );

        WebDriver driver = seleniumLoginService.loginAndGetDriver();
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            for (LegaConfig config : legheDaScrapare) {
                log.info("--- Inizio scraping per la lega: {} ---", config.nomeLega());

                LegaFantaEntity legaEntity = legaFantaRepository.findByNome(config.nomeLega())
                        .orElseThrow(() -> new RuntimeException("Lega " + config.nomeLega() + " non trovata nel DB"));

                scrapeRosters(driver, wait, legaEntity, config);
            }
        } catch (Exception e) {
            log.error("Errore critico durante lo scraping di Fantacalcio.it", e);
        } finally {
            if (driver != null) {
                driver.quit();
                log.info("WebDriver chiuso correttamente.");
            }
        }
    }

    private void scrapeRosters(WebDriver driver, WebDriverWait wait, LegaFantaEntity lega, LegaConfig config) {
        String urlRose = "https://leghe.fantacalcio.it/" + config.slug() + "/rose";
        if (config.divisione() != null) {
            urlRose += "?d=" + config.divisione();
        }

        driver.get(urlRose);
        log.info("Navigazione alla pagina rose: {}", urlRose);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li.list-rosters-item")));
        List<WebElement> teamBoxes = driver.findElements(By.cssSelector("li.list-rosters-item"));
        log.info("Trovati {} blocchi squadra nella pagina per la lega '{}'.", teamBoxes.size(), lega.getNome());

        for (WebElement teamBox : teamBoxes) {
            String nomeSquadraFanta = teamBox.findElement(By.cssSelector(".media-body > h4.media-heading")).getText().trim();

            List<ScrapedPlayerDTO> giocatoriScraped = new ArrayList<>();
            List<WebElement> playerRows = teamBox.findElements(By.cssSelector("table#rosterTable > tbody > tr[data-id]"));

            for (WebElement playerRow : playerRows) {
                String nome = playerRow.findElement(By.cssSelector("td[data-key='name'] a.player-link b.capitalize")).getText().trim();
                String prezzoRaw = playerRow.findElement(By.cssSelector("td[data-key='price'] b")).getText().trim();
                try {
                    int prezzo = Integer.parseInt(prezzoRaw);
                    giocatoriScraped.add(new ScrapedPlayerDTO(nome, prezzo));
                } catch (NumberFormatException e) {
                    log.warn("Impossibile parsare il prezzo '{}' per il giocatore {}", prezzoRaw, nome);
                }
            }

            // Unica chiamata al nostro nuovo service!
            fantaTeamService.aggiornaRosa(lega, nomeSquadraFanta, config.slug(), giocatoriScraped);
        }
    }
}