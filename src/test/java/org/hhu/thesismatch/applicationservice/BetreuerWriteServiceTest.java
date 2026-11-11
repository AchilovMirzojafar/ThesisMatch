package org.hhu.thesismatch.applicationservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hhu.thesismatch.helper.TestDaten.betreuer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.hhu.thesismatch.application.applicationservice.BetreuerExistiertBereitsException;
import org.hhu.thesismatch.application.applicationservice.BetreuerNichtGefundenException;
import org.hhu.thesismatch.application.applicationservice.BetreuerWriteService;
import org.hhu.thesismatch.application.applicationservice.KonkurrierendeAenderungException;
import org.hhu.thesismatch.application.applicationservice.ProfilDaten;
import org.hhu.thesismatch.application.applicationservice.repository.BetreuerRepository;
import org.hhu.thesismatch.domain.model.Betreuer;
import org.hhu.thesismatch.domain.model.Email;
import org.hhu.thesismatch.domain.model.GitHubId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class BetreuerWriteServiceTest {

    private static final GitHubId ID = new GitHubId(10);

    private BetreuerRepository repository;
    private BetreuerWriteService service;

    @BeforeEach
    void setUp() {
        repository = mock(BetreuerRepository.class);
        service = new BetreuerWriteService(repository);
    }

    private static ProfilDaten profil() {
        return new ProfilDaten("Prof.", "Max", "Muster", new Email("max@example.com"), "+49 211", Set.of("KI"));
    }

    @Test
    @DisplayName("create() speichert einen neuen Betreuer")
    void create_savesNewBetreuer() {
        when(repository.findByFachId(ID)).thenReturn(Optional.empty());

        service.create(ID, "Dr.", "Anna", "Meyer", new Email("anna@example.com"));

        ArgumentCaptor<Betreuer> captor = ArgumentCaptor.forClass(Betreuer.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getFachId()).isEqualTo(ID);
        assertThat(captor.getValue().getVersion()).isNull();
    }

    @Test
    @DisplayName("create() wirft eine Exception, wenn die GitHub-ID schon vergeben ist")
    void create_duplicate_throws() {
        when(repository.findByFachId(ID)).thenReturn(Optional.of(betreuer(10)));

        assertThatThrownBy(() -> service.create(ID, "Dr.", "Anna", "Meyer", new Email("anna@example.com")))
                .isInstanceOf(BetreuerExistiertBereitsException.class);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("updateProfile() ändert das geladene Aggregat und speichert es")
    void updateProfile_savesChanges() {
        Betreuer existing = betreuer(10);
        when(repository.findByFachId(ID)).thenReturn(Optional.of(existing));

        service.updateProfile(ID, 0, profil());

        verify(repository).save(existing);
        assertThat(existing.getTitel()).isEqualTo("Prof.");
        assertThat(existing.getTags()).containsExactly("KI");
    }

    @Test
    @DisplayName("updateProfile() erkennt eine zwischenzeitliche Änderung (Lost Update)")
    void updateProfile_staleVersion_throws() {
        when(repository.findByFachId(ID)).thenReturn(Optional.of(betreuer(10)));

        assertThatThrownBy(() -> service.updateProfile(ID, 5, profil()))
                .isInstanceOf(KonkurrierendeAenderungException.class);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("updateProfile() für unbekannten Betreuer wirft BetreuerNichtGefundenException")
    void updateProfile_unknown_throws() {
        when(repository.findByFachId(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateProfile(ID, 0, profil()))
                .isInstanceOf(BetreuerNichtGefundenException.class);
    }

    @Test
    @DisplayName("delete() löscht vorhandene Betreuer und meldet, ob etwas gelöscht wurde")
    void delete() {
        Betreuer existing = betreuer(10);
        when(repository.findByFachId(ID)).thenReturn(Optional.of(existing));
        when(repository.findByFachId(new GitHubId(11))).thenReturn(Optional.empty());

        assertThat(service.delete(ID)).isTrue();
        assertThat(service.delete(new GitHubId(11))).isFalse();
        verify(repository).delete(existing);
    }
}
