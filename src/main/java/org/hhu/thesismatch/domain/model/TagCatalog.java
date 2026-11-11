package org.hhu.thesismatch.domain.model;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Feste Liste der Fachgebiete, die Betreuern und Themen zugeordnet werden können.
 */
public final class TagCatalog {

    public static final SortedSet<String> ALL = Collections.unmodifiableSortedSet(new TreeSet<>(List.of(
            "KI", "Datenbanken", "Compilerbau", "Software Engineering", "Security", "Web", "ML",
            "Machine Learning", "Deep Learning", "Data Science",
            "Programmiersprachen", "Betriebssysteme", "Rechnernetze", "IT-Security",
            "Web-Technologien", "Algorithmik", "Datenstrukturen", "Autonome Systeme"
    )));

    private TagCatalog() {
    }
}
