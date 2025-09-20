package com.fantacarmo.fanta_carmo_pro.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "fonti_consigli")
@Getter
@Setter
public class FonteConsigliEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nome; // Es: "Carmyspecial", "Il Profeta"
}
