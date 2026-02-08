package org.hhu.chatgpt.domain.model;

import java.util.Set;
import java.util.TreeSet;

public final class TagCatalog {

    public static final Set<String> ALL = new TreeSet<>(Set.of(
            "KI", "Datenbanken", "Compilerbau", "Software Engineering", "Security", "Web", "ML",
            "Machine Learning", "Deep Learning", "Data Science",
            "Programmiersprachen", "Betriebssysteme", "Rechnernetze", "IT-Security",
            "Web-Technologien", "Algorithmik", "Datenstrukturen", "Autonome Systeme"
    ));

    private TagCatalog() {}
}