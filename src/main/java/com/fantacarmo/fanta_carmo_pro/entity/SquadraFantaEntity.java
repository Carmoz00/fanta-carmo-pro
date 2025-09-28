package com.fantacarmo.fanta_carmo_pro.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "squadre_fanta")
@Getter
@Setter
public class SquadraFantaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String nomeAllenatore;

    private boolean isMiaSquadra; // Flag per identificare le tue squadre

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lega_fanta_id", nullable = false)
    private LegaFantaEntity legaFanta;


    // SOSTITUISCI il vecchio campo @ManyToMany con questo
    @OneToMany(mappedBy = "squadraFanta", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RosaGiocatoreEntity> rosa = new HashSet<>(); // <-- Inizializza subito la collezione!

    private Integer posizione;
    private Integer punti;
    private Integer vittorie;
    private Integer pareggi;
    private Integer sconfitte;
    private Integer golFatti;
    private Integer golSubiti;
}
