package org.hhu.chatgpt.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.hhu.chatgpt.model.Thema;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ThemenController {

    // Beispiel Data (spaeter mit DB aendern)
    private List<Thema> alleThemen = List.of(
    new Thema(
        "Webplattform für Themenvergabe",
        "Bachelorarbeit im Bereich Webentwicklung",
        "Web",
        "Grundkenntnisse",
        "Prof. Max Mustermann",
        "Entwicklung einer Webplattform zur Themenvergabe",
        "Gute Java- und Spring-Kenntnisse erforderlich"
    ),
    new Thema(
        "KI-gestützte Empfehlungssysteme",
        "Bachelorarbeit im Bereich KI",
        "KI",
        "Fortgeschritten",
        "Prof. Erika Beispiel",
        "Analyse und Entwicklung eines Empfehlungssystems",
        "Mathematische Grundlagen empfohlen"
    ),
    new Thema(
        "Datenbanken SQL mit JAVA",
        "Bachelorarbeit im Bereich DB",
        "Datenbank",
        "Grundkenntnisse",
        "Prof. Jens Bendo",
        "Erstellung von Datenbanken mit Hilfe von SQL und Spring Boot",
        "Sehr gute SQL und Java Kenntnisse empfohlen"
    )   
);

    @GetMapping("/themen")
    public String themen(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "tag", required = false) String tag,
            @RequestParam(name = "requirement", required = false) String requirement,
            Model model) {

        List<Thema> filtered = alleThemen.stream()
                .filter(t -> search == null || t.getTitle().toLowerCase().contains(search.toLowerCase()))
                .filter(t -> tag == null || tag.isEmpty() || t.getTag().equals(tag))
                .filter(t -> requirement == null || requirement.isEmpty() || t.getRequirement().equals(requirement))
                .collect(Collectors.toList());

        model.addAttribute("themenListe", filtered);
        model.addAttribute("resultCount", filtered.size());
        model.addAttribute("search", search);
        model.addAttribute("tag", tag);
        model.addAttribute("requirement", requirement);

        return "themen";
    }
}
