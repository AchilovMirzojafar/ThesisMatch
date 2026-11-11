package org.hhu.thesismatch.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ThemaTest {

    @Test
    @DisplayName("neu() erzeugt ein Thema mit eigener ID")
    void neu_createsThema() {
        Thema thema = Thema.neu(" Compiler ", "Beschreibung", Set.of("KI"), Set.of("Programmierung"));

        assertThat(thema.getId()).isNotNull();
        assertThat(thema.getTitle()).isEqualTo("Compiler");
        assertThat(thema.getTags()).containsExactly("KI");
    }

    @Test
    @DisplayName("neu() lehnt leeren oder zu langen Titel ab")
    void neu_rejectsInvalidTitle() {
        assertThatThrownBy(() -> Thema.neu(" ", null, Set.of("KI"), Set.of("Programmierung")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Thema.neu("x".repeat(51), null, Set.of("KI"), Set.of("Programmierung")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("neu() lehnt zu lange Beschreibung ab")
    void neu_rejectsLongDescription() {
        assertThatThrownBy(() -> Thema.neu("T", "x".repeat(301), Set.of("KI"), Set.of("Programmierung")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("neu() verlangt mindestens einen Tag und eine Veranstaltung")
    void neu_requiresTagsAndRequirements() {
        assertThatThrownBy(() -> Thema.neu("T", null, Set.of(), Set.of("Programmierung")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Thema.neu("T", null, Set.of("KI"), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Tags können von außen nicht verändert werden")
    void tags_areImmutable() {
        Set<String> tags = new HashSet<>(Set.of("KI"));
        Thema thema = Thema.neu("T", null, tags, Set.of("Programmierung"));

        tags.add("Web");

        assertThat(thema.getTags()).containsExactly("KI");
        assertThatThrownBy(() -> thema.getTags().add("Web"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Gleichheit wird nur über die ID bestimmt")
    void equality_byId() {
        UUID id = UUID.randomUUID();
        Thema a = Thema.rekonstruiere(id, "A", null, Set.of(), Set.of());
        Thema b = Thema.rekonstruiere(id, "B", null, Set.of(), Set.of());

        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
    }
}
