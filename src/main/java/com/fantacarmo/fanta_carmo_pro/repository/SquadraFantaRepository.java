package com.fantacarmo.fanta_carmo_pro.repository;

import com.fantacarmo.fanta_carmo_pro.entity.LegaFantaEntity;
import com.fantacarmo.fanta_carmo_pro.entity.SquadraFantaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SquadraFantaRepository extends JpaRepository<SquadraFantaEntity, Long> {
    // Metodo per trovare una squadra fanta conoscendo il nome e la lega
    Optional<SquadraFantaEntity> findByNomeAndLegaFanta(String nome, LegaFantaEntity legaFanta);
}