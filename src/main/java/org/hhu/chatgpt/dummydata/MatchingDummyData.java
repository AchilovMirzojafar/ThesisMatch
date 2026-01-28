package org.hhu.chatgpt.dummydata;

import java.util.List;

import org.hhu.chatgpt.domain.model.Matching;

public class MatchingDummyData {

    public static Matching getDummyMatching() {
        List<String> kurse = List.of("Programmierung", "Machine Learning", "Datenbanken", "Computernetzwerke");
        List<String> interessen = List.of("KI", "Netzwerk", "Datenbank", "Datenbanken",
                "Web", "Netzwerk", "Compilerbau", "Formale Methoden",
                "Theoretische Informatik", "Spring",
                "Webentwicklung", "Data Science", "Machine Learning",
                "Computernetzwerke", "Rechnerarchitektur", "Socketprogrammierung");
        return new Matching(kurse, interessen);
    }
}
