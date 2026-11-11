package org.hhu.thesismatch.helper;

import java.util.List;
import java.util.Set;

import org.hhu.thesismatch.domain.model.Betreuer;
import org.hhu.thesismatch.domain.model.Email;
import org.hhu.thesismatch.domain.model.GitHubId;
import org.hhu.thesismatch.domain.model.Thema;

/**
 * Kleine Fabrikmethoden für Testdaten.
 */
public final class TestDaten {

    private TestDaten() {
    }

    public static Betreuer betreuer(int fachId) {
        return betreuer(fachId, "Dr.", "Anna", "Meyer", Set.of());
    }

    public static Betreuer betreuer(int fachId, String titel, String firstName, String lastName,
                                    Set<String> tags, Thema... themen) {
        return Betreuer.rekonstruiere(new GitHubId(fachId), 0, titel, firstName, lastName,
                new Email("x" + fachId + "@example.com"), null, tags, List.of(themen));
    }

    public static Betreuer betreuerMitThemen(int fachId, Set<String> tags, Thema... themen) {
        return betreuer(fachId, "Dr.", "Anna", "Meyer", tags, themen);
    }

    public static Thema thema(String title, Set<String> tags, Set<String> requirements) {
        return Thema.neu(title, "Beschreibung", tags, requirements);
    }
}
