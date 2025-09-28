package com.fantacarmo.fanta_carmo_pro.service;

import com.fantacarmo.fanta_carmo_pro.dto.ScrapedPlayerDTO;
import com.fantacarmo.fanta_carmo_pro.entity.LegaFantaEntity;

import java.util.List;

public interface FantaTeamService {
    void aggiornaRosa(LegaFantaEntity lega, String nomeSquadraFanta, String legaSlug, List<ScrapedPlayerDTO> giocatoriScraped);
}