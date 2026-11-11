package org.hhu.thesismatch.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BetreuerTest {

    private static Betreuer neuerBetreuer() {
        return Betreuer.neu(new GitHubId(1), "Dr.", "Anna", "Meyer", new Email("anna@example.com"));
    }

    private static Thema thema(String title) {
        return Thema.neu(title, null, Set.of("KI"), Set.of("Programmierung"));
    }

    @Test
    @DisplayName("neu() legt einen Betreuer ohne Version, Tags und Themen an")
    void neu_createsEmptyBetreuer() {
        Betreuer b = neuerBetreuer();

        assertThat(b.getVersion()).isNull();
        assertThat(b.getTags()).isEmpty();
        assertThat(b.getThemen()).isEmpty();
        assertThat(b.getPhone()).isNull();
    }

    @Test
    @DisplayName("neu() lehnt leere Namen ab")
    void neu_rejectsBlankNames() {
        assertThatThrownBy(() -> Betreuer.neu(new GitHubId(1), "Dr.", " ", "Meyer", new Email("a@example.com")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("addThema() fügt ein Thema hinzu, removeThema() entfernt es wieder")
    void addAndRemoveThema() {
        Betreuer b = neuerBetreuer();
        Thema thema = thema("Compiler");

        b.addThema(thema);
        assertThat(b.getThemen()).containsExactly(thema);

        assertThat(b.removeThema(thema.getId())).isTrue();
        assertThat(b.getThemen()).isEmpty();
    }

    @Test
    @DisplayName("removeThema() mit unbekannter ID ändert nichts")
    void removeUnknownThema() {
        Betreuer b = neuerBetreuer();
        b.addThema(thema("Compiler"));

        assertThat(b.removeThema(UUID.randomUUID())).isFalse();
        assertThat(b.getThemen()).hasSize(1);
    }

    @Test
    @DisplayName("Dasselbe Thema kann nicht zweimal hinzugefügt werden")
    void addThema_rejectsDuplicate() {
        Betreuer b = neuerBetreuer();
        Thema thema = thema("Compiler");
        b.addThema(thema);

        assertThatThrownBy(() -> b.addThema(thema)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Themen und Tags können von außen nicht verändert werden")
    void collections_areNotModifiableFromOutside() {
        Betreuer b = neuerBetreuer();

        assertThatThrownBy(() -> b.getThemen().add(thema("X")))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> b.getTags().add("KI"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("updateProfil() übernimmt Daten, leere Telefonnummer wird zu null")
    void updateProfil_updatesData() {
        Betreuer b = neuerBetreuer();

        b.updateProfil("Prof.", "Max", "Muster", new Email("max@example.com"), "  ", Set.of("KI"));

        assertThat(b.getTitel()).isEqualTo("Prof.");
        assertThat(b.getFullName()).isEqualTo("Max Muster");
        assertThat(b.getEmail()).isEqualTo(new Email("max@example.com"));
        assertThat(b.getPhone()).isNull();
        assertThat(b.getTags()).containsExactly("KI");
    }

    @Test
    @DisplayName("updateProfil() lehnt ungültige Telefonnummer ab")
    void updateProfil_rejectsInvalidPhone() {
        Betreuer b = neuerBetreuer();

        assertThatThrownBy(() -> b.updateProfil("Dr.", "Anna", "Meyer", new Email("a@example.com"), "abc", Set.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("updateProfil() lässt die Themen unverändert")
    void updateProfil_keepsThemen() {
        Betreuer b = neuerBetreuer();
        Thema thema = thema("Compiler");
        b.addThema(thema);

        b.updateProfil("Dr.", "Anna", "Meyer", new Email("a@example.com"), null, Set.of());

        assertThat(b.getThemen()).containsExactly(thema);
    }
}
