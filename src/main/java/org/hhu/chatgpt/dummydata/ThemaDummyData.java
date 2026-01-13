package org.hhu.chatgpt.dummydata;

import java.util.List;

import org.hhu.chatgpt.domain.model.Thema;

public class ThemaDummyData {

    private ThemaDummyData() {
        // utility class
    }

    public static List<Thema> getAlleThemen() {
        return List.of(
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
            ),

            new Thema(
                "Datenbanken MariaDB und PostgeSQL",
                "Bachelorarbeit im Bereich DB",
                "Datenbank",
                "Fortgeschritten",
                "Prof. Mero Kh",
                "Entwicklung einer Datenbanken und Neuigkeiten von MariaDB",
                "Bestimmte Relationen Arten und Typen von Datenbanken erforderlich"
            )
        );
    }
}
