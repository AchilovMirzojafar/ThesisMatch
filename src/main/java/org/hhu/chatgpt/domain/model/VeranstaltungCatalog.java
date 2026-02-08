package org.hhu.chatgpt.domain.model;

import java.util.Set;
import java.util.TreeSet;

public class VeranstaltungCatalog {
    
     public static final Set<String> ALL = new TreeSet<>(Set.of(
           "Programmierung", "Rechnerarchitektur", "Wissenschaftliches Arbeiten", "Grundlagen der Computernetzwerke",
            "Datenbanken", "Programmierpraktikum 1 ", "Programmierpraktikum 2",
            "Algorithmen und Datenstrukturen", "Data Science", "Theoretische Informatik", "Data Science 2",
            "Datenbanken: Weiterführende Konzepte", "Introduction to NLP", "Embedded Systems", "Compilerbau",
            "Betriebssysteme und Systemprogrammierung", "Datenvisualisierung", "Einführung in Python",
            "Competitive Programming A", "Competitive Programming B", "Algorithmen in der Bioinformatik"
    ));
}
