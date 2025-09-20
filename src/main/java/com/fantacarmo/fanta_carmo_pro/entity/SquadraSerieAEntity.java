package com.fantacarmo.fanta_carmo_pro.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "squadre_serie_a")
@Getter
@Setter
public class SquadraSerieAEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nome;

    private String citta;
    private String allenatore;
    private Integer valoreSquadra; // Da 1 a 10 per calcolo difficoltà
    private String logoUrl; // da implementare
}

