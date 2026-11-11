package org.hhu.thesismatch.domain.model;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Feste Liste der Lehrveranstaltungen, die als Voraussetzung für ein Thema dienen können.
 */
public final class VeranstaltungCatalog {

    public static final SortedSet<String> ALL = Collections.unmodifiableSortedSet(new TreeSet<>(List.of(
            "Programmierung", "Rechnerarchitektur", "Wissenschaftliches Arbeiten",
            "Grundlagen der Computernetzwerke", "Datenbanken", "Programmierpraktikum 1",
            "Programmierpraktikum 2", "Algorithmen und Datenstrukturen", "Data Science",
            "Theoretische Informatik", "Data Science 2", "Datenbanken: Weiterführende Konzepte",
            "Introduction to NLP", "Embedded Systems", "Compilerbau",
            "Betriebssysteme und Systemprogrammierung", "Datenvisualisierung", "Einführung in Python",
            "Competitive Programming A", "Competitive Programming B", "Algorithmen in der Bioinformatik"
    )));

    private VeranstaltungCatalog() {
    }
}
