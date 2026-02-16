package org.hhu.chatgpt.applicationservice;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.hhu.chatgpt.application.applicationservice.MatchingService;
import org.hhu.chatgpt.application.applicationservice.repository.BetreuerRepository;
import org.hhu.chatgpt.domain.model.Betreuer;
import org.hhu.chatgpt.domain.model.MatchingResult;
import org.hhu.chatgpt.domain.model.Thema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class MatchingServiceTest {

    private BetreuerRepository repository;
    private MatchingService service;

    @BeforeEach
    void setUp() {
        repository = mock(BetreuerRepository.class);
        service = new MatchingService(repository);
    }

    private static Betreuer betreuer(int fachId, Set<String> profTags, Set<Thema> themen) {
        Betreuer b = new Betreuer(
                fachId,
                "Dr.",
                "Anna",
                "Meyer",
                "x" + fachId + "@example.com",
                "+49123456",
                profTags,
                themen
        );
        b.setTags(profTags);
        b.setThemenSet(themen);
        return b;
    }

    private static Thema thema(String title, Set<String> tags, Set<String> reqs) {
        return new Thema(title, "Desc", tags, reqs);
    }

    @Test
    @DisplayName("Wenn keine Kurse und keine Interessen ausgewählt sind, wird eine leere Ergebnisliste zurückgegeben.")
    void test1() {
        List<MatchingResult> result = service.getMatchingResults(List.of(), List.of());

        assertThat(result).isEmpty();
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Betreuer ohne Themen werden ignoriert und liefern keine Ergebnisse.")
    void test2() {
        Betreuer b1 = betreuer(1, Set.of("AI"), Set.of());
        when(repository.findAll()).thenReturn(List.of(b1));

        List<MatchingResult> result = service.getMatchingResults(List.of("OS"), List.of("AI"));

        assertThat(result).isEmpty();
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Ein Thema wird nur dann zurückgegeben, wenn mindestens ein Requirement oder Tag matcht.")
    void test3() {
        Thema t1 = thema("KI", Set.of("ML"), Set.of("DB"));
        Betreuer b1 = betreuer(1, Set.of("SE"), Set.of(t1));
        when(repository.findAll()).thenReturn(List.of(b1));

        List<MatchingResult> result = service.getMatchingResults(List.of("OS"), List.of("AI"));

        assertThat(result).isEmpty();
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Requirements und Tags werden normalisiert und führen auch bei Bindestrichen oder Leerzeichen zu Matches.")
    void test4() {
        Thema t1 = thema("KI", Set.of("Machine-Learning"), Set.of("Operating-Systems"));
        Betreuer b1 = betreuer(1, Set.of("Deep  Learning"), Set.of(t1));
        when(repository.findAll()).thenReturn(List.of(b1));

        List<MatchingResult> result = service.getMatchingResults(
                List.of("  operating systems "),
                List.of("machine learning", "deep-learning")
        );

        assertThat(result).hasSize(1);
        MatchingResult r = result.get(0);
        assertThat(r.getMatchedRequirements()).isEqualTo(1);
        assertThat(r.getTotalRequirements()).isEqualTo(1);
        assertThat(r.getMatchedTags()).isEqualTo(2);
        assertThat(r.getTotalTags()).isEqualTo(2);
        assertThat(r.getScore()).isEqualTo(100);

        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Die Tag-Matches berücksichtigen sowohl die Tags des Themas als auch die Tags des Betreuers.")
    void test5() {
        Thema t1 = thema("KI", Set.of("AI"), Set.of("OS"));
        Betreuer b1 = betreuer(1, Set.of("ML"), Set.of(t1));
        when(repository.findAll()).thenReturn(List.of(b1));

        List<MatchingResult> result = service.getMatchingResults(List.of(), List.of("ai", "ml"));

        assertThat(result).hasSize(1);
        MatchingResult r = result.get(0);
        assertThat(r.getMatchedTags()).isEqualTo(2);
        assertThat(r.getTotalTags()).isEqualTo(2);

        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Der Score wird als Prozentwert gerundet und nach Score absteigend sortiert zurückgegeben.")
    void test6() {
        Thema low = thema("Low", Set.of("AI"), Set.of("OS", "DB"));
        Thema high = thema("High", Set.of("AI", "ML"), Set.of("OS"));
        Betreuer b1 = betreuer(1, Set.of(), Set.of(low, high));
        when(repository.findAll()).thenReturn(List.of(b1));

        List<MatchingResult> result = service.getMatchingResults(
                List.of("OS"),
                List.of("AI", "ML")
        );

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getThema().getTitle()).isEqualTo("High");
        assertThat(result.get(0).getScore()).isGreaterThanOrEqualTo(result.get(1).getScore());
        assertThat(result.get(1).getThema().getTitle()).isEqualTo("Low");

        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
@DisplayName("Nullwerte und leere Strings in den Auswahl-Listen werden beim Normalisieren herausgefiltert.")
void test7() {
    Thema t1 = thema("KI", Set.of("AI"), Set.of("OS"));
    Betreuer b1 = betreuer(1, Set.of(), Set.of(t1));
    when(repository.findAll()).thenReturn(List.of(b1));

    List<MatchingResult> result = service.getMatchingResults(
            Arrays.asList(null, "  ", "OS"),
            Arrays.asList("", "  ai  ", null)
    );

    assertThat(result).hasSize(1);
    MatchingResult r = result.get(0);
    assertThat(r.getMatchedRequirements()).isEqualTo(1);
    assertThat(r.getMatchedTags()).isEqualTo(1);

    verify(repository).findAll();
    verifyNoMoreInteractions(repository);
}

}
