package com.fantacarmo.fanta_carmo_pro.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "partite_serie_a")
@Getter
@Setter
public class PartitaSerieAEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer giornata;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "squadra_casa_id", nullable = false)
    private SquadraSerieAEntity squadraCasa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "squadra_ospite_id", nullable = false)
    private SquadraSerieAEntity squadraOspite;

    private LocalDateTime dataPartita;
}
