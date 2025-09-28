package com.fantacarmo.fanta_carmo_pro.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "rosa_giocatori")
@Getter
@Setter
public class RosaGiocatoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "squadra_fanta_id")
    private SquadraFantaEntity squadraFanta;

    @ManyToOne
    @JoinColumn(name = "giocatore_id")
    private GiocatoreEntity giocatore;

    @Column(name = "prezzo_acquisto")
    private Integer prezzoAcquisto;

    // Aggiungi qui altri campi se necessario in futuro (es. se è capitano, ecc.)
}