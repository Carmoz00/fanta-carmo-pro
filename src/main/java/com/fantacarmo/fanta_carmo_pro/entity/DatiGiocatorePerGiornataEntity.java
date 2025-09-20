package com.fantacarmo.fanta_carmo_pro.entity;

import com.fantacarmo.fanta_carmo_pro.entity.enums.StatusDisponibilita;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dati_giocatore_giornata")
@Getter
@Setter
public class DatiGiocatorePerGiornataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer giornata;

    private boolean giocaInCasa;
    private Integer difficoltaPartita;
    private boolean probabileTitolare;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatusDisponibilita statusDisponibilita;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giocatore_id", nullable = false)
    private GiocatoreEntity giocatore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partita_id", nullable = false)
    private PartitaSerieAEntity partita;
}
