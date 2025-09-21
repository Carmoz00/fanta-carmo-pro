package com.fantacarmo.fanta_carmo_pro.repository;

import com.fantacarmo.fanta_carmo_pro.entity.LegaFantaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegaFantaRepository extends JpaRepository<LegaFantaEntity, Long> {
    long count();
}