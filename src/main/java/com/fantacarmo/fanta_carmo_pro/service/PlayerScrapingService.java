package com.fantacarmo.fanta_carmo_pro.service;

import com.fantacarmo.fanta_carmo_pro.entity.GiocatoreEntity;
import com.fantacarmo.fanta_carmo_pro.entity.SquadraSerieAEntity;
import com.fantacarmo.fanta_carmo_pro.entity.enums.Ruolo;
import com.fantacarmo.fanta_carmo_pro.repository.GiocatoreRepository;
import com.fantacarmo.fanta_carmo_pro.repository.SquadraSerieARepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerScrapingService {

    private final GiocatoreRepository giocatoreRepository;
    private final SquadraSerieARepository squadraSerieARepository;

    private static final String QUOTAZIONI_URL = "https://www.fantacalcio.it/quotazioni-fantacalcio";

    // Mappa per convertire abbreviazioni squadre (dal sito) in nomi completi (nel nostro DB)
    private static final Map<String, String> ABBREV_TO_FULL = new HashMap<>();
    static {
        ABBREV_TO_FULL.put("ATA", "Atalanta");
        ABBREV_TO_FULL.put("BOL", "Bologna");
        ABBREV_TO_FULL.put("CAG", "Cagliari");
        ABBREV_TO_FULL.put("COM", "Como");
        ABBREV_TO_FULL.put("CRE", "Cremonese");
        ABBREV_TO_FULL.put("FIO", "Fiorentina");
        ABBREV_TO_FULL.put("GEN", "Genoa");
        ABBREV_TO_FULL.put("INT", "Inter");
        ABBREV_TO_FULL.put("JUV", "Juventus");
        ABBREV_TO_FULL.put("LAZ", "Lazio");
        ABBREV_TO_FULL.put("LEC", "Lecce");
        ABBREV_TO_FULL.put("MIL", "Milan");
        ABBREV_TO_FULL.put("NAP", "Napoli");
        ABBREV_TO_FULL.put("PAR", "Parma");
        ABBREV_TO_FULL.put("PIS", "Pisa");
        ABBREV_TO_FULL.put("ROM", "Roma");
        ABBREV_TO_FULL.put("SAS", "Sassuolo");
        ABBREV_TO_FULL.put("TOR", "Torino");
        ABBREV_TO_FULL.put("UDI", "Udinese");
        ABBREV_TO_FULL.put("VER", "Verona");
        // Aggiungi eventuali varianti se il sito usa abbrev diversi
    }

    @Transactional
    public void scrapeAndSavePlayers() {
        System.out.println("Inizio refresh scraping giocatori da: " + QUOTAZIONI_URL);

        try {
            Map<String, SquadraSerieAEntity> squadreMap =
                    squadraSerieARepository.findAll().stream()
                            .collect(Collectors.toMap(s -> s.getNome().toUpperCase(), Function.identity()));

            Document doc = Jsoup.connect(QUOTAZIONI_URL).get();
            Elements playerRows = doc.select("tr.player-row");  // Selector basato sull'HTML fornito, per righe dei giocatori

            if (playerRows.isEmpty()) {
                System.out.println("DEBUG: Nessuna riga trovata con il selector 'tr.player-row'. Controlla la struttura HTML del sito.");
            }

            List<GiocatoreEntity> giocatoriDaSalvare = new ArrayList<>();

            for (Element row : playerRows) {
                Elements cells = row.select("th, td");  // Seleziona sia th che td per catturare tutte le celle

                // Debug: Stampa i contenuti di tutte le celle per questa riga
                List<String> cellTexts = cells.stream().map(Element::text).map(String::trim).collect(Collectors.toList());
                System.out.println("DEBUG Cells in row: " + cellTexts);

                if (cells.size() >= 8) {  // Minimo per ruolo, nome, squadra, QI, QA, ecc.
                    String ruoloRaw = cells.get(1).select("span.role").attr("data-value").trim().toUpperCase();  // Ruolo da data-value (es. "a")
                    String nomeGiocatore = cells.get(3).select("a").text().trim();  // Nome
                    String squadraAbbrev = cells.get(4).text().trim().toUpperCase();  // Squadra
                    String quotazioneRaw = cells.get(6).text().trim();  // QA (quotazione attuale)

                    String fullSquadraName = ABBREV_TO_FULL.get(squadraAbbrev);
                    if (fullSquadraName == null) {
                        System.out.println("DEBUG: Squadra non mappata: " + squadraAbbrev);
                        continue;
                    }

                    SquadraSerieAEntity squadra = squadreMap.get(fullSquadraName.toUpperCase());
                    if (squadra == null || nomeGiocatore.isEmpty() || quotazioneRaw.isEmpty() || ruoloRaw.isEmpty()) {
                        System.out.println("DEBUG: Dati incompleti per riga: " + row.text());
                        continue;
                    }

                    // Logica upsert
                    GiocatoreEntity existing = giocatoreRepository.findByNomeAndSquadraSerieA(nomeGiocatore, squadra);
                    GiocatoreEntity giocatore;
                    if (existing != null) {
                        giocatore = existing;
                        System.out.println("Aggiorno quotazione per: " + nomeGiocatore);
                    } else {
                        giocatore = new GiocatoreEntity();
                        giocatore.setNome(nomeGiocatore);
                        giocatore.setRuolo(Ruolo.valueOf(ruoloRaw));
                        giocatore.setSquadraSerieA(squadra);
                        System.out.println("Aggiungo nuovo giocatore: " + nomeGiocatore);
                    }
                    giocatore.setQuotazione(Integer.parseInt(quotazioneRaw));
                    giocatoriDaSalvare.add(giocatore);
                } else {
                    System.out.println("DEBUG: Riga con celle insufficienti: " + cells.size());
                }
            }

            giocatoreRepository.saveAll(giocatoriDaSalvare);
            System.out.println("Refresh completato. Aggiornati/Inseriti " + giocatoriDaSalvare.size() + " giocatori.");

        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Errore durante il refresh scraping: " + e.getMessage());
            e.printStackTrace();
        }
    }
}