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
            List.of("Web"),
            List.of("Programmierung"),
            "Prof. Marwan Alkhoury"
        ),

        new Thema(
            "KI-gestützte Empfehlungssysteme",
            "Bachelorarbeit im Bereich KI",
            List.of("KI"),
            List.of("Machine Learning"),
            "Prof. Muhammad Challou"
        ),

        new Thema(
            "Datenbanken SQL mit JAVA",
            "Bachelorarbeit im Bereich DB",
            List.of("Datenbank"),
            List.of("Datenbanken", "Programmierung"),
            "Prof. Mirzojafar Achilov"
        ),

        new Thema(
            "Computer Netwerk Entwicklung",
            "HTTP mit HTML und Netwerk",
            List.of("Netzwerk"),
            List.of("Computernetzwerke"),
            "Prof. John Müller"
        )
    );
}

}
