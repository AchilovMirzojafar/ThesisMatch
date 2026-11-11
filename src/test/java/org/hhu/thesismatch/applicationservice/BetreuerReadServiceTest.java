package org.hhu.thesismatch.applicationservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hhu.thesismatch.helper.TestDaten.betreuer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.hhu.thesismatch.application.applicationservice.BetreuerReadService;
import org.hhu.thesismatch.application.applicationservice.repository.BetreuerRepository;
import org.hhu.thesismatch.domain.model.Betreuer;
import org.hhu.thesismatch.domain.model.GitHubId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BetreuerReadServiceTest {

    private BetreuerRepository repository;
    private BetreuerReadService service;

    private Betreuer meyer;
    private Betreuer schmidt;

    @BeforeEach
    void setUp() {
        repository = mock(BetreuerRepository.class);
        service = new BetreuerReadService(repository);

        meyer = betreuer(1, "Dr.", "Anna", "Meyer", Set.of("KI", "Web"));
        schmidt = betreuer(2, "Prof. Dr.", "Max", "Schmidt", Set.of("Datenbanken"));
        when(repository.findAll()).thenReturn(List.of(meyer, schmidt));
    }

    @Test
    @DisplayName("list() gibt alle Betreuer aus dem Repository zurück")
    void list_returnsAll() {
        assertThat(service.list()).containsExactly(meyer, schmidt);
    }

    @Test
    @DisplayName("findProfile() liefert den Betreuer oder ein leeres Optional")
    void findProfile() {
        when(repository.findByFachId(new GitHubId(1))).thenReturn(Optional.of(meyer));
        when(repository.findByFachId(new GitHubId(99))).thenReturn(Optional.empty());

        assertThat(service.findProfile(new GitHubId(1))).contains(meyer);
        assertThat(service.findProfile(new GitHubId(99))).isEmpty();
    }

    @Test
    @DisplayName("Ohne Name und Tag (oder nur Leerzeichen) werden alle Betreuer gefunden")
    void search_withoutFilters_returnsAll() {
        assertThat(service.search(null, null)).containsExactly(meyer, schmidt);
        assertThat(service.search("   ", " ")).containsExactly(meyer, schmidt);
    }

    @Test
    @DisplayName("Namenssuche ignoriert Groß-/Kleinschreibung und Punkte")
    void search_byName_caseAndDotInsensitive() {
        assertThat(service.search("MEYER", null)).containsExactly(meyer);
        assertThat(service.search("prof dr schmidt", null)).containsExactly(schmidt);
    }

    @Test
    @DisplayName("Alle Suchbegriffe müssen im Namen vorkommen")
    void search_allTokensMustMatch() {
        assertThat(service.search("Anna Schmidt", null)).isEmpty();
    }

    @Test
    @DisplayName("Tag-Filter ignoriert Groß-/Kleinschreibung")
    void search_byTag() {
        assertThat(service.search(null, "datenbanken")).containsExactly(schmidt);
    }

    @Test
    @DisplayName("Name und Tag müssen beide passen")
    void search_nameAndTag() {
        assertThat(service.search("Meyer", "KI")).containsExactly(meyer);
        assertThat(service.search("Meyer", "Datenbanken")).isEmpty();
    }
}
