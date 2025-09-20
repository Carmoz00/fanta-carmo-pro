package com.fantacarmo.fanta_carmo_pro.entity;

import com.fantacarmo.fanta_carmo_pro.entity.enums.BonusConsiglio;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "consigli_giornata")
@Getter
@Setter
public class ConsiglioPerGiornataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer indiceSchierabilita; // Da 1 a 10

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private BonusConsiglio bonusConsiglio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dati_giocatore_giornata_id", nullable = false)
    private DatiGiocatorePerGiornataEntity datiGiocatoreGiornata;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fonte_consigli_id", nullable = false)
    private FonteConsigliEntity fonte;
}
