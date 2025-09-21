package com.fantacarmo.fanta_carmo_pro.repository;

import com.fantacarmo.fanta_carmo_pro.entity.GiocatoreEntity;
import com.fantacarmo.fanta_carmo_pro.entity.SquadraSerieAEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiocatoreRepository extends JpaRepository<GiocatoreEntity, Long> {
    long count();
    GiocatoreEntity findByNomeAndSquadraSerieA(String nome, SquadraSerieAEntity squadraSerieA);  // Nuovo metodo per upsert
}