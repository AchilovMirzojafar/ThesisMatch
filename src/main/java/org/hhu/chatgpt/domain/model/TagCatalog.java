package org.hhu.chatgpt.domain.model;

import java.util.Set;

public final class TagCatalog {
    public static final Set<String> ALL = Set.of(
        "KI", "Datenbanken", "Compilerbau", "Software Engineering", "Security", "Web", "ML"
    );
    private TagCatalog() {}
}