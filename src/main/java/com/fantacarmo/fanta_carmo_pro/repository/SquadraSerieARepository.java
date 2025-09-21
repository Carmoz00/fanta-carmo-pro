package com.fantacarmo.fanta_carmo_pro.repository;

import com.fantacarmo.fanta_carmo_pro.entity.SquadraSerieAEntity;
import org.springframework.data.jpa.repository.JpaRepository;

// Estendendo JpaRepository<NomeEntita, TipoId>, otteniamo tutti i metodi CRUD
public interface SquadraSerieARepository extends JpaRepository<SquadraSerieAEntity, Long> {
    // Possiamo aggiungere metodi custom, Spring capirà cosa fare dal nome
    long count(); // Esempio: conta quante squadre ci sono
}