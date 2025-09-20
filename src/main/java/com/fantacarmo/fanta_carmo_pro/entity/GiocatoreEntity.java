package com.fantacarmo.fanta_carmo_pro.entity;

import com.fantacarmo.fanta_carmo_pro.entity.enums.Ruolo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "giocatori")
@Getter
@Setter
public class GiocatoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 1) // P, D, C, A
    private Ruolo ruolo;

    private Integer quotazione;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "squadra_serie_a_id", nullable = false)
    private SquadraSerieAEntity squadraSerieA;
}
