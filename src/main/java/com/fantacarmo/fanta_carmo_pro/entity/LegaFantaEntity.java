package com.fantacarmo.fanta_carmo_pro.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "leghe_fanta")
@Getter
@Setter
public class LegaFantaEntity {

    /*

    --- Link leghe

    - Lega Idroscimmia League (fantamaster, richiesto login):
    link homepage: https://leghe.fantamaster.it/league/1546989/
    link pagina squadre: https://leghe.fantamaster.it/league/1546989/teams/
    link pagina squadra individuale: https://leghe.fantamaster.it/league/1546989/teams/?team=7123631 (?team=7123444, ?team=7123598, ?team=7123654, ?team=7123826, ?team=7123664, ?team=7123684, ?team=7123622)
    link pagina formazioni: https://leghe.fantamaster.it/league/1546989/lineups/
    link pagina formazione individuale: https://leghe.fantamaster.it/league/1546989/lineupdetail/?lineup=2980681 (?lineup=3348783, ?lineup=3321063, ?lineup=2858294, ?lineup=3026323, ?lineup=3003772, ?lineup=3279925, ?lineup=3208251)
    link calendario: https://leghe.fantamaster.it/league/1546989/fixtures/
    link classifica: https://leghe.fantamaster.it/league/1546989/ranking/
    link scambi: https://leghe.fantamaster.it/league/1546989/exchanges/

    - Lega Fantalipari League (fantacalcio, richiesto login):
    link login: https://leghe.fantacalcio.it/login
    link homepage: https://leghe.fantacalcio.it/fantaliparigroup
    link pagina squadre: https://leghe.fantacalcio.it/fantaliparigroup/squadre
    link pagina rose: https://leghe.fantacalcio.it/fantaliparigroup/rose
    link pagina formazioni: https://leghe.fantacalcio.it/fantaliparigroup/formazioni
    link pagina calendario: https://leghe.fantacalcio.it/fantaliparigroup/calendario
    link pagina classifica: https://leghe.fantacalcio.it/fantaliparigroup/classifica

    - Lega Fantasburroland League (fantacalcio, richiesto login):
    link login: https://leghe.fantacalcio.it/login
    link homepage: https://leghe.fantacalcio.it/fantasburroland
    link pagina squadre: https://leghe.fantacalcio.it/fantasburroland/squadre
    link pagina rose: https://leghe.fantacalcio.it/fantasburroland/rose
    link pagina formazioni: https://leghe.fantacalcio.it/fantasburroland/formazioni
    link pagina calendario: https://leghe.fantacalcio.it/fantasburroland/calendario
    link pagina classifica: https://leghe.fantacalcio.it/fantasburroland/classifica

    - Lega Filippopummarorol League (fantacalcio, richiesto login):
    link login: https://leghe.fantacalcio.it/login
    link homepage: https://leghe.fantacalcio.it/filippopummaroroleague
    link pagina squadre: https://leghe.fantacalcio.it/filippopummaroroleague/squadre
    link pagina rose: https://leghe.fantacalcio.it/filippopummaroroleague/rose
    link pagina formazioni: https://leghe.fantacalcio.it/filippopummaroroleague/formazioni
    link pagina calendario: https://leghe.fantacalcio.it/filippopummaroroleague/calendario
    link pagina classifica: https://leghe.fantacalcio.it/filippopummaroroleague/classifica
     */


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nome;
}
