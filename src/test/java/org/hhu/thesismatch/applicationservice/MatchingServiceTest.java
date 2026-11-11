package org.hhu.thesismatch.applicationservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hhu.thesismatch.helper.TestDaten.betreuerMitThemen;
import static org.hhu.thesismatch.helper.TestDaten.thema;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.hhu.thesismatch.application.applicationservice.MatchingService;
import org.hhu.thesismatch.application.applicationservice.repository.BetreuerRepository;
import org.hhu.thesismatch.domain.model.MatchingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Die Berechnung selbst wird in MatchingCalculatorTest geprüft. Hier geht es nur um das Laden.
 */
class MatchingServiceTest {

    private BetreuerRepository repository;
    private MatchingService service;

    @BeforeEach
    void setUp() {
        repository = mock(BetreuerRepository.class);
        service = new MatchingService(repository);
    }

    @Test
    @DisplayName("Ohne Auswahl wird das Repository gar nicht gefragt")
    void noSelection_noRepositoryCall() {
        assertThat(service.getMatchingResults(null, List.of())).isEmpty();
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Mit Auswahl werden alle Betreuer geladen und bewertet")
    void withSelection_delegatesToCalculator() {
        when(repository.findAll()).thenReturn(List.of(
                betreuerMitThemen(1, Set.of(), thema("T", Set.of("KI"), Set.of("Programmierung")))));

        List<MatchingResult> result = service.getMatchingResults(List.of("Programmierung"), null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).score()).isEqualTo(50);
    }
}
