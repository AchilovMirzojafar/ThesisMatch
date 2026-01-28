package org.hhu.chatgpt.domain.model;

import java.util.Set;

public class VeranstaltungenListe {
    private VeranstaltungenListe() {
    }

    public static final Set<String> VERANSTALTUNGEN =
            Set.of(
                    "Algorithmen und Datenstrukturen",
                    "Compilerbau",
                    "Datenbanken Weiterführende Konzepte ",
                    "ProPra2",
                    "ProPra1",
                    "Programmierung",
                    "Mathe für Informatik",
                    "Theoretische Informatik"
            );
}
