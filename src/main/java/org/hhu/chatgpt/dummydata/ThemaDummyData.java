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
                       List.of( "Web"),
                       List.of( "Programmierung"),
                        "Prof. Max Mustermann"),
                        
                        
                new Thema(
                        "KI-gestützte Empfehlungssysteme",
                        "Bachelorarbeit im Bereich KI",
                        List.of( "KI"),
                        List.of( "Machine Learning"),
                        "Prof. Erika Beispiel"),
                        
                new Thema(
                        "Datenbanken SQL mit JAVA",
                        "Bachelorarbeit im Bereich DB",
                        List.of("Datenbank"),
                        List.of("Datenbanken", "Programmierung"),
                        "Prof. Michael Conrad"),
                        

                new Thema(
                        "Datenbanken MariaDB und PostgeSQL",
                        "Bachelorarbeit im Bereich DB",
                        List.of("Datenbank"),
                        List.of("Datenbanken"),
                        "Prof. Mero Kh")
                        
                        
        );
    }
}
