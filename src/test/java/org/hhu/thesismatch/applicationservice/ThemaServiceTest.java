package org.hhu.thesismatch.applicationservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hhu.thesismatch.helper.TestDaten.betreuer;
import static org.hhu.thesismatch.helper.TestDaten.betreuerMitThemen;
import static org.hhu.thesismatch.helper.TestDaten.thema;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.hhu.thesismatch.application.applicationservice.BetreuerNichtGefundenException;
import org.hhu.thesismatch.application.applicationservice.ThemaListItem;
import org.hhu.thesismatch.application.applicationservice.ThemaService;
import org.hhu.thesismatch.application.applicationservice.repository.BetreuerRepository;
import org.hhu.thesismatch.domain.model.Betreuer;
import org.hhu.thesismatch.domain.model.GitHubId;
import org.hhu.thesismatch.domain.model.Thema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ThemaServiceTest {

    private static final GitHubId ID = new GitHubId(1);

    private BetreuerRepository repository;
    private ThemaService service;

    @BeforeEach
    void setUp() {
        repository = mock(BetreuerRepository.class);
        service = new ThemaService(repository);
    }

    @Test
    @DisplayName("Themen aller Betreuer werden mit Betreuername als ThemaListItem geliefert")
    void collectsThemenOfAllBetreuer() {
        Betreuer a = betreuerMitThemen(1, Set.of(), thema("Compiler", Set.of("Compilerbau"), Set.of("Programmierung")));
        Betreuer b = betreuerMitThemen(2, Set.of(), thema("KI", Set.of("KI"), Set.of("Data Science")));
        when(repository.findAll()).thenReturn(List.of(a, b));

        List<ThemaListItem> result = service.getGefilterteThemen(null, null, null);

        assertThat(result).extracting(ThemaListItem::title).containsExactly("Compiler", "KI");
        assertThat(result.get(0).betreuerName()).isEqualTo("Anna Meyer");
    }

    @Test
    @DisplayName("Titelsuche, Tag- und Veranstaltungsfilter werden kombiniert")
    void filters() {
        Betreuer a = betreuerMitThemen(1, Set.of(),
                thema("Compiler bauen", Set.of("Compilerbau"), Set.of("Programmierung")),
                thema("Neuronale Netze", Set.of("KI"), Set.of("Data Science")));
        when(repository.findAll()).thenReturn(List.of(a));

        assertThat(service.getGefilterteThemen("compiler", null, null))
                .extracting(ThemaListItem::title).containsExactly("Compiler bauen");
        assertThat(service.getGefilterteThemen(null, "KI", null))
                .extracting(ThemaListItem::title).containsExactly("Neuronale Netze");
        assertThat(service.getGefilterteThemen(null, null, "Programmierung"))
                .extracting(ThemaListItem::title).containsExactly("Compiler bauen");
        assertThat(service.getGefilterteThemen("compiler", "KI", null)).isEmpty();
    }

    @Test
    @DisplayName("createThema() fügt das Thema über den Aggregate Root hinzu und speichert")
    void createThema() {
        Betreuer b = betreuer(1);
        when(repository.findByFachId(ID)).thenReturn(Optional.of(b));

        Thema thema = service.createThema(ID, "Neu", "Text", Set.of("KI"), Set.of("Programmierung"));

        assertThat(b.getThemen()).containsExactly(thema);
        verify(repository).save(b);
    }

    @Test
    @DisplayName("createThema() mit ungültigen Daten speichert nichts")
    void createThema_invalid() {
        when(repository.findByFachId(ID)).thenReturn(Optional.of(betreuer(1)));

        assertThatThrownBy(() -> service.createThema(ID, "", null, Set.of(), Set.of()))
                .isInstanceOf(IllegalArgumentException.class);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("createThema() für unbekannten Betreuer wirft BetreuerNichtGefundenException")
    void createThema_unknownBetreuer() {
        when(repository.findByFachId(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createThema(ID, "Neu", null, Set.of("KI"), Set.of("Programmierung")))
                .isInstanceOf(BetreuerNichtGefundenException.class);
    }

    @Test
    @DisplayName("deleteThema() entfernt eigenes Thema und speichert")
    void deleteThema_own() {
        Thema thema = thema("T", Set.of("KI"), Set.of("Programmierung"));
        Betreuer b = betreuerMitThemen(1, Set.of(), thema);
        when(repository.findByFachId(ID)).thenReturn(Optional.of(b));

        assertThat(service.deleteThema(ID, thema.getId())).isTrue();
        assertThat(b.getThemen()).isEmpty();
        verify(repository).save(b);
    }

    @Test
    @DisplayName("deleteThema() mit fremder oder unbekannter ID speichert nichts")
    void deleteThema_unknown() {
        Betreuer b = betreuerMitThemen(1, Set.of(), thema("T", Set.of("KI"), Set.of("Programmierung")));
        when(repository.findByFachId(ID)).thenReturn(Optional.of(b));

        assertThat(service.deleteThema(ID, UUID.randomUUID())).isFalse();
        assertThat(b.getThemen()).hasSize(1);
        verify(repository, never()).save(any());
    }
}
