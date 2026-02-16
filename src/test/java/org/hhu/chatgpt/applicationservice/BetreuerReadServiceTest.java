package org.hhu.chatgpt.applicationservice;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.hhu.chatgpt.application.applicationservice.BetreuerReadService;
import org.hhu.chatgpt.application.applicationservice.repository.BetreuerRepository;
import org.hhu.chatgpt.domain.model.Betreuer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class BetreuerReadServiceTest {

    private BetreuerRepository repository;
    private BetreuerReadService service;

    @BeforeEach
    void setUp() {
        repository = mock(BetreuerRepository.class);
        service = new BetreuerReadService(repository);
    }

    private static Betreuer b(int fachId, String titel, String first, String last, Set<String> tags) {
        return new Betreuer(
                fachId,
                titel,
                first,
                last,
                "x" + fachId + "@example.com",
                "+49123456",
                tags,
                Set.of()
        );
    }

    @Test
    @DisplayName("Die Methode list() gibt genau die Liste zurück, die das Repository liefert.")
    void test1() {
        Betreuer b1 = b(1, "Dr.", "Anna", "Meyer", Set.of("AI"));
        Betreuer b2 = b(2, "Prof.", "Max", "Mustermann", Set.of("DB"));
        List<Betreuer> all = List.of(b1, b2);

        when(repository.findAll()).thenReturn(all);

        List<Betreuer> result = service.list();

        assertThat(result).isSameAs(all);
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Die Methode findProfile() gibt das Profil zurück, wenn es im Repository existiert.")
    void test2() {
        Betreuer b = b(42, "Dr.", "Anna", "Meyer", Set.of("AI"));
        when(repository.findByFachId(42)).thenReturn(Optional.of(b));

        Optional<Betreuer> result = service.findProfile(42);

        assertThat(result).isPresent();
        assertThat(result.get()).isSameAs(b);
        verify(repository).findByFachId(42);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Die Methode findProfile() liefert ein leeres Optional, wenn das Profil nicht existiert.")
    void test3() {
        when(repository.findByFachId(7)).thenReturn(Optional.empty());

        Optional<Betreuer> result = service.findProfile(7);

        assertThat(result).isEmpty();
        verify(repository).findByFachId(7);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Die Suche gibt alle Betreuer zurück, wenn Name und Tag nicht angegeben sind.")
    void test4() {
        Betreuer b1 = b(1, "Dr.", "Anna", "Meyer", Set.of("AI"));
        Betreuer b2 = b(2, "Prof.", "Max", "Mustermann", Set.of("DB"));
        when(repository.findAll()).thenReturn(List.of(b1, b2));

        List<Betreuer> result = service.getGesuchteName(null, null);

        assertThat(result).containsExactly(b1, b2);
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Die Suche gibt alle Betreuer zurück, wenn Name und Tag nur aus Leerzeichen bestehen.")
    void test5() {
        Betreuer b1 = b(1, "Dr.", "Anna", "Meyer", Set.of("AI"));
        Betreuer b2 = b(2, "Prof.", "Max", "Mustermann", Set.of("DB"));
        when(repository.findAll()).thenReturn(List.of(b1, b2));

        List<Betreuer> result = service.getGesuchteName("   ", "   ");

        assertThat(result).containsExactly(b1, b2);
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Die Suche nach Name ist nicht case-sensitive und findet passende Betreuer unabhängig von Groß- und Kleinschreibung.")
    void test6() {
        Betreuer match = b(2, "Prof.", "Max", "Mustermann", Set.of("DB"));
        Betreuer nonMatch = b(1, "Dr.", "Anna", "Meyer", Set.of("AI"));
        when(repository.findAll()).thenReturn(List.of(match, nonMatch));

        List<Betreuer> result = service.getGesuchteName("PROF max", null);

        assertThat(result).containsExactly(match);
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Die Suche ignoriert Punkte sowohl im Suchtext als auch in den Namen der Betreuer.")
    void test7() {
        Betreuer profWithDot = b(2, "Prof.", "Max", "Mustermann", Set.of("DB"));
        when(repository.findAll()).thenReturn(List.of(profWithDot));

        List<Betreuer> result = service.getGesuchteName("Prof. Max", null);

        assertThat(result).containsExactly(profWithDot);
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Die Suche verlangt, dass alle Tokens aus dem Namen im Betreuer-Text enthalten sein müssen.")
    void test8() {
        Betreuer fullMatch = b(1, "Dr.", "Anna", "Maria", Set.of("AI"));
        Betreuer partialMatch = b(2, "Dr.", "Anna", "Schmidt", Set.of("AI"));
        when(repository.findAll()).thenReturn(List.of(fullMatch, partialMatch));

        List<Betreuer> result = service.getGesuchteName("  Anna   Maria  ", null);

        assertThat(result).containsExactly(fullMatch);
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Wenn kein Tag angegeben ist, wird nur nach dem Namen gefiltert.")
    void test9() {
        Betreuer b1 = b(1, "Dr.", "Anna", "Meyer", Set.of("AI"));
        Betreuer b2 = b(2, "Prof.", "Max", "Mustermann", Set.of("DB"));
        when(repository.findAll()).thenReturn(List.of(b1, b2));

        List<Betreuer> r1 = service.getGesuchteName("anna", null);
        List<Betreuer> r2 = service.getGesuchteName("anna", "   ");

        assertThat(r1).containsExactly(b1);
        assertThat(r2).containsExactly(b1);

        verify(repository, times(2)).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Die Suche filtert Betreuer nach Tag unabhängig von Groß- und Kleinschreibung.")
    void test10() {
        Betreuer ai = b(1, "Dr.", "Anna", "Meyer", Set.of("AI", "ML"));
        Betreuer db = b(2, "Prof.", "Max", "Mustermann", Set.of("DB"));
        when(repository.findAll()).thenReturn(List.of(ai, db));

        List<Betreuer> result = service.getGesuchteName(null, "ml");

        assertThat(result).containsExactly(ai);
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Die Suche kombiniert Namenssuche und Tag-Filterung, sodass beide Bedingungen erfüllt sein müssen.")
    void test11() {
        Betreuer match = b(1, "Dr.", "Anna", "Meyer", Set.of("AI"));
        Betreuer wrongTag = b(2, "Dr.", "Anna", "Meyer", Set.of("DB"));
        Betreuer wrongName = b(3, "Dr.", "Sara", "Meyer", Set.of("AI"));

        when(repository.findAll()).thenReturn(List.of(match, wrongTag, wrongName));

        List<Betreuer> result = service.getGesuchteName("anna", "ai");

        assertThat(result).containsExactly(match);
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Die Suche liefert eine leere Liste zurück, wenn kein Betreuer die Kriterien erfüllt.")
    void test12() {
        Betreuer b1 = b(1, "Dr.", "Anna", "Meyer", Set.of("AI"));
        Betreuer b2 = b(2, "Prof.", "Max", "Mustermann", Set.of("DB"));
        when(repository.findAll()).thenReturn(List.of(b1, b2));

        List<Betreuer> result = service.getGesuchteName("doesnotexist", "whatever");

        assertThat(result).isEmpty();
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }
}
