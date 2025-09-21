package com.fantacarmo.fanta_carmo_pro.service;

import com.fantacarmo.fanta_carmo_pro.entity.LegaFantaEntity;
import com.fantacarmo.fanta_carmo_pro.entity.SquadraSerieAEntity;
import com.fantacarmo.fanta_carmo_pro.repository.LegaFantaRepository;
import com.fantacarmo.fanta_carmo_pro.repository.SquadraSerieARepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service // Rende questa classe un componente gestito da Spring
@RequiredArgsConstructor // Lombok: crea un costruttore con i campi 'final'
public class DataInitializerService {

    // Spring inietterà automaticamente le istanze dei repository
    private final SquadraSerieARepository squadraSerieARepository;
    private final LegaFantaRepository legaFantaRepository;

    @PostConstruct // Questa annotazione fa eseguire il metodo subito dopo la creazione del componente
    @Transactional // Assicura che tutte le operazioni sul DB avvengano in un'unica transazione
    public void initializeData() {
        // Popoliamo le squadre di Serie A solo se la tabella è vuota
        if (squadraSerieARepository.count() == 0) {
            System.out.println("Popolamento tabella squadre_serie_a...");
            List<SquadraSerieAEntity> squadre = Arrays.asList(
                    createSquadra("Atalanta", "Bergamo"),
                    createSquadra("Bologna", "Bologna"),
                    createSquadra("Cagliari", "Cagliari"),
                    createSquadra("Como", "Como"),
                    createSquadra("Cremonese", "Cremona"),
                    createSquadra("Fiorentina", "Firenze"),
                    createSquadra("Genoa", "Genova"),
                    createSquadra("Inter", "Milano"),
                    createSquadra("Juventus", "Torino"),
                    createSquadra("Lazio", "Roma"),
                    createSquadra("Lecce", "Lecce"),
                    createSquadra("Milan", "Milano"),
                    createSquadra("Napoli", "Napoli"),
                    createSquadra("Parma", "Parma"),
                    createSquadra("Pisa", "Pisa"),
                    createSquadra("Roma", "Roma"),
                    createSquadra("Sassuolo", "Sassuolo"),
                    createSquadra("Torino", "Torino"),
                    createSquadra("Udinese", "Udine"),
                    createSquadra("Verona", "Verona")
            );
            squadraSerieARepository.saveAll(squadre);
            System.out.println("Tabella squadre_serie_a popolata con " + squadre.size() + " squadre.");
        }

        // Popoliamo le leghe fanta solo se la tabella è vuota
        if (legaFantaRepository.count() == 0) {
            System.out.println("Popolamento tabella leghe_fanta...");
            List<LegaFantaEntity> leghe = Arrays.asList(
                    createLega("Lega Idroscimmia League"),
                    createLega("Lega FilippoPummaroroLeague"),
                    createLega("Lega FantaLipari League"),
                    createLega("Lega Fantasburro League")
            );
            legaFantaRepository.saveAll(leghe);
            System.out.println("Tabella leghe_fanta popolata con " + leghe.size() + " leghe.");
        }
    }

    // Metodo helper per creare una SquadraSerieAEntity
    private SquadraSerieAEntity createSquadra(String nome, String citta) {
        SquadraSerieAEntity squadra = new SquadraSerieAEntity();
        squadra.setNome(nome);
        squadra.setCitta(citta);
        // Puoi impostare altri valori di default qui (es. valoreSquadra)
        return squadra;
    }

    // Metodo helper per creare una LegaFantaEntity
    private LegaFantaEntity createLega(String nome) {
        LegaFantaEntity lega = new LegaFantaEntity();
        lega.setNome(nome);
        return lega;
    }
}