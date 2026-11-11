package org.hhu.thesismatch.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hhu.thesismatch.helper.TestDaten.betreuerMitThemen;
import static org.hhu.thesismatch.helper.TestDaten.thema;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.hhu.thesismatch.domain.model.Betreuer;
import org.hhu.thesismatch.domain.model.MatchingResult;
import org.hhu.thesismatch.domain.model.Thema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MatchingCalculatorTest {

    private final MatchingCalculator calculator = new MatchingCalculator();

    @Test
    @DisplayName("Ohne Kurse und Interessen gibt es keine Ergebnisse")
    void emptySelection_returnsEmpty() {
        Betreuer b = betreuerMitThemen(1, Set.of(), thema("KI", Set.of("KI"), Set.of("Programmierung")));

        assertThat(calculator.berechne(List.of(b), List.of(), List.of())).isEmpty();
    }

    @Test
    @DisplayName("Betreuer ohne Themen liefern keine Ergebnisse")
    void betreuerOhneThemen_ignored() {
        Betreuer b = betreuerMitThemen(1, Set.of("KI"));

        assertThat(calculator.berechne(List.of(b), List.of("Programmierung"), List.of("KI"))).isEmpty();
    }

    @Test
    @DisplayName("Ein Thema wird nur zurückgegeben, wenn mindestens etwas passt")
    void onlyMatchingThemen() {
        Thema passt = thema("Passt", Set.of("KI"), Set.of("Programmierung"));
        Thema passtNicht = thema("Passt nicht", Set.of("Web"), Set.of("Datenbanken"));
        Betreuer b = betreuerMitThemen(1, Set.of(), passt, passtNicht);

        List<MatchingResult> result = calculator.berechne(List.of(b), List.of("Programmierung"), List.of());

        assertThat(result).extracting(r -> r.thema().getTitle()).containsExactly("Passt");
    }

    @Test
    @DisplayName("Groß-/Kleinschreibung, Bindestriche und Leerzeichen werden normalisiert")
    void normalization() {
        Thema t = thema("T", Set.of("Machine-Learning"), Set.of("Data  Science"));
        Betreuer b = betreuerMitThemen(1, Set.of(), t);

        List<MatchingResult> result = calculator.berechne(List.of(b),
                List.of("data science"), List.of("machine learning"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).matchedRequirements()).isEqualTo(1);
        assertThat(result.get(0).matchedTags()).isEqualTo(1);
        assertThat(result.get(0).score()).isEqualTo(100);
    }

    @Test
    @DisplayName("Tags des Themas und des Betreuers zählen gemeinsam, doppelte Tags nur einmal")
    void tagsOfThemaAndBetreuer() {
        Thema t = thema("T", Set.of("KI", "ML"), Set.of("Programmierung"));
        Betreuer b = betreuerMitThemen(1, Set.of("ml", "Web"), t);

        MatchingResult r = calculator.berechne(List.of(b), List.of(), List.of("Web")).get(0);

        assertThat(r.totalTags()).isEqualTo(3);
        assertThat(r.matchedTags()).isEqualTo(1);
        assertThat(r.betreuer()).isSameAs(b);
    }

    @Test
    @DisplayName("Score wird gerundet und Ergebnisse absteigend sortiert")
    void scoreAndSorting() {
        Thema low = thema("Low", Set.of("KI"), Set.of("Betriebssysteme", "Datenbanken"));
        Thema high = thema("High", Set.of("KI", "ML"), Set.of("Betriebssysteme"));
        Betreuer b = betreuerMitThemen(1, Set.of(), low, high);

        List<MatchingResult> result = calculator.berechne(List.of(b),
                List.of("Betriebssysteme"), List.of("KI", "ML"));

        assertThat(result).extracting(r -> r.thema().getTitle()).containsExactly("High", "Low");
        assertThat(result.get(0).score()).isEqualTo(100);
        assertThat(result.get(1).score()).isEqualTo(67);
    }

    @Test
    @DisplayName("null und leere Strings in der Auswahl werden ignoriert")
    void nullAndBlankValuesIgnored() {
        Thema t = thema("T", Set.of("KI"), Set.of("Betriebssysteme"));
        Betreuer b = betreuerMitThemen(1, Set.of(), t);

        List<MatchingResult> result = calculator.berechne(List.of(b),
                Arrays.asList(null, "  ", "Betriebssysteme"),
                Arrays.asList("", "  ki  ", null));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).matchedRequirements()).isEqualTo(1);
        assertThat(result.get(0).matchedTags()).isEqualTo(1);
    }
}
