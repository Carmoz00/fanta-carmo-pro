package com.fantacarmo.fanta_carmo_pro.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "squadra_fanta_giocatori",
            joinColumns = @JoinColumn(name = "squadra_fanta_id"),
            inverseJoinColumns = @JoinColumn(name = "giocatore_id")
    )
    private Set<GiocatoreEntity> giocatori;
}
