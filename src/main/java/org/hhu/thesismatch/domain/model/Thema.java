package org.hhu.thesismatch.domain.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Entity innerhalb des Aggregats {@link Betreuer}.
 * Ein Thema ist unveränderlich. Gleichheit wird über die ID bestimmt.
 */
public final class Thema {

    public static final int MAX_TITLE_LENGTH = 50;
    public static final int MAX_DESCRIPTION_LENGTH = 300;

    private final UUID id;
    private final String title;
    private final String description;
    private final Set<String> tags;
    private final Set<String> requirements;

    private Thema(UUID id, String title, String description, Set<String> tags, Set<String> requirements) {
        this.id = Objects.requireNonNull(id, "id");
        this.title = title;
        this.description = description == null ? "" : description;
        this.tags = copy(tags);
        this.requirements = copy(requirements);
    }

    /**
     * Legt ein neues Thema an und prüft dabei alle fachlichen Regeln.
     */
    public static Thema neu(String title, String description, Set<String> tags, Set<String> requirements) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Titel darf nicht leer sein");
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException("Titel darf maximal " + MAX_TITLE_LENGTH + " Zeichen haben");
        }
        if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                    "Beschreibung darf maximal " + MAX_DESCRIPTION_LENGTH + " Zeichen haben");
        }
        if (tags == null || tags.isEmpty()) {
            throw new IllegalArgumentException("Mindestens ein Tag ist erforderlich");
        }
        if (requirements == null || requirements.isEmpty()) {
            throw new IllegalArgumentException("Mindestens eine Veranstaltung ist erforderlich");
        }
        return new Thema(UUID.randomUUID(), title.trim(), description, tags, requirements);
    }

    /**
     * Stellt ein bereits gespeichertes Thema wieder her (z. B. aus der Datenbank).
     * Fachliche Regeln wurden beim Anlegen geprüft und werden hier nicht erneut erzwungen,
     * damit ältere Datensätze weiterhin geladen werden können.
     */
    public static Thema rekonstruiere(UUID id, String title, String description,
                                      Set<String> tags, Set<String> requirements) {
        return new Thema(id, title, description, tags, requirements);
    }

    private static Set<String> copy(Set<String> values) {
        return values == null ? Set.of() : Collections.unmodifiableSet(new LinkedHashSet<>(values));
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getTags() {
        return tags;
    }

    public Set<String> getRequirements() {
        return requirements;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Thema other && id.equals(other.id));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
