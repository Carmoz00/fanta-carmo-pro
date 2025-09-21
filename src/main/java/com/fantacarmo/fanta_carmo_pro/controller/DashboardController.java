package com.fantacarmo.fanta_carmo_pro.controller;

import com.fantacarmo.fanta_carmo_pro.repository.LegaFantaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Annotazione chiave: questa classe gestisce richieste web e ritorna pagine HTML
@RequiredArgsConstructor
public class DashboardController {

    private final LegaFantaRepository legaFantaRepository;

    @GetMapping("/") // Risponde alle richieste sull'URL principale (es. http://localhost:8080/)
    public String showDashboard(Model model) {
        // 1. Recupera tutte le leghe dal database
        var leghe = legaFantaRepository.findAll();

        // 2. Aggiunge la lista di leghe al "modello", un contenitore di dati
        //    da passare alla pagina HTML. Il nome "legheList" sarà la variabile
        //    che useremo nell'HTML.
        model.addAttribute("legheList", leghe);

        // 3. Ritorna il nome del file HTML da mostrare (senza l'estensione .html)
        return "dashboard";
    }
}