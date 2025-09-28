package com.fantacarmo.fanta_carmo_pro.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "partite_fanta")
@Getter
@Setter
public class PartitaFantaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer giornata;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lega_fanta_id", nullable = false)
    private LegaFantaEntity legaFanta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "squadra_casa_id")
    private SquadraFantaEntity squadraCasa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "squadra_ospite_id")
    private SquadraFantaEntity squadraOspite;

    private Double punteggioCasa;
    private Double punteggioOspite;
    private Integer golCasa;
    private Integer golOspite;
}