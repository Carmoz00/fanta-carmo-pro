package com.fantacarmo.fanta_carmo_pro.service.impl;

import com.fantacarmo.fanta_carmo_pro.dto.ScrapedPlayerDTO;
import com.fantacarmo.fanta_carmo_pro.service.FantaTeamService;
import com.fantacarmo.fanta_carmo_pro.entity.GiocatoreEntity;
import com.fantacarmo.fanta_carmo_pro.entity.LegaFantaEntity;
import com.fantacarmo.fanta_carmo_pro.entity.RosaGiocatoreEntity;
import com.fantacarmo.fanta_carmo_pro.entity.SquadraFantaEntity;
import com.fantacarmo.fanta_carmo_pro.repository.GiocatoreRepository;
import com.fantacarmo.fanta_carmo_pro.repository.RosaGiocatoreRepository;
import com.fantacarmo.fanta_carmo_pro.repository.SquadraFantaRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FantaTeamServiceImpl implements FantaTeamService {

    private final SquadraFantaRepository squadraFantaRepository;
    private final GiocatoreRepository giocatoreRepository;
    private final RosaGiocatoreRepository rosaGiocatoreRepository;

    private List<GiocatoreEntity> tuttiGiocatoriCache;

    // Mappa per identificare le tue squadre
    private static final Map<String, String> MIE_SQUADRE_PER_LEGA = Map.of(
            "fantaliparigroup", "Nico o Pazz",
            "fantasburroland", "Bayer Muten",
            "filippopummaroroleague", "Kevin De Brawnie"
    );

    // Mettiamo in cache tutti i giocatori all'avvio per non interrogare il DB ripetutamente
    @PostConstruct
    public void initGiocatoriCache() {
        log.info("Inizializzazione cache giocatori...");
        this.tuttiGiocatoriCache = giocatoreRepository.findAll();
        log.info("Cache inizializzata con {} giocatori.", this.tuttiGiocatoriCache.size());
    }

    @Override
    public void aggiornaRosa(LegaFantaEntity lega, String nomeSquadraFanta, String legaSlug, List<ScrapedPlayerDTO> giocatoriScraped) {
        SquadraFantaEntity squadraFanta = squadraFantaRepository.findByNomeAndLegaFanta(nomeSquadraFanta, lega)
                .orElseGet(() -> createNewSquadraFanta(nomeSquadraFanta, lega, legaSlug));

        Set<RosaGiocatoreEntity> rosaAttuale = squadraFanta.getRosa();

        // --- MODIFICA CHIAVE ---
        // Rimuoviamo la cancellazione esplicita e lasciamo che orphanRemoval faccia il suo lavoro tramite .clear()
        if (rosaAttuale != null) {
            rosaAttuale.clear();
        }
        // --- FINE MODIFICA ---

        for (ScrapedPlayerDTO playerDTO : giocatoriScraped) {
            findPlayerMatch(playerDTO.nome()).ifPresent(giocatoreTrovato -> {
                RosaGiocatoreEntity nuovoRosaGiocatore = new RosaGiocatoreEntity();
                nuovoRosaGiocatore.setSquadraFanta(squadraFanta);
                nuovoRosaGiocatore.setGiocatore(giocatoreTrovato);
                nuovoRosaGiocatore.setPrezzoAcquisto(playerDTO.prezzoAcquisto());
                rosaAttuale.add(nuovoRosaGiocatore);
            });
        }

        squadraFantaRepository.save(squadraFanta);
        log.info("Processata e salvata rosa per '{}' con {} giocatori.", nomeSquadraFanta, squadraFanta.getRosa().size());
    }

    private SquadraFantaEntity createNewSquadraFanta(String nome, LegaFantaEntity lega, String legaSlug) {
        log.info("Squadra Fanta '{}' non trovata, la creo.", nome);
        SquadraFantaEntity nuovaSquadra = new SquadraFantaEntity();
        nuovaSquadra.setNome(nome);
        nuovaSquadra.setLegaFanta(lega);

        String miaSquadraInQuestaLega = MIE_SQUADRE_PER_LEGA.get(legaSlug);
        if (nome.equalsIgnoreCase(miaSquadraInQuestaLega)) {
            nuovaSquadra.setMiaSquadra(true);
            log.info(">>> Squadra '{}' identificata come MIA SQUADRA. <<<", nome);
        }
        return squadraFantaRepository.save(nuovaSquadra);
    }

    private Optional<GiocatoreEntity> findPlayerMatch(String scrapedName) {
        String normalizedScrapedName = normalizeName(scrapedName);
        GiocatoreEntity bestMatch = null;
        double highestSimilarity = 0.0;
        JaroWinklerSimilarity jwSimilarity = new JaroWinklerSimilarity();

        for (GiocatoreEntity playerDB : this.tuttiGiocatoriCache) {
            String normalizedDbName = normalizeName(playerDB.getNome());
            double similarity = jwSimilarity.apply(normalizedScrapedName, normalizedDbName);
            if (similarity > highestSimilarity) {
                highestSimilarity = similarity;
                bestMatch = playerDB;
            }
        }

        double SIMILARITY_THRESHOLD = 0.90;
        if (bestMatch != null && highestSimilarity >= SIMILARITY_THRESHOLD) {
            log.debug("Match trovato per '{}' -> '{}' (Similarità: {})", scrapedName, bestMatch.getNome(), String.format("%.2f", highestSimilarity));
            return Optional.of(bestMatch);
        }
        log.warn("Nessuna corrispondenza valida trovata per: {} (Best match: {} con similarità {})", scrapedName, (bestMatch != null ? bestMatch.getNome() : "N/A"), String.format("%.2f", highestSimilarity));
        return Optional.empty();
    }

    private String normalizeName(String name) {
        if (name == null) return "";
        String normalized = name.toUpperCase();
        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        normalized = normalized.replaceAll("[.'-]", " ");
        normalized = normalized.replaceAll("\\s+", " ").trim();
        return normalized;
    }
}