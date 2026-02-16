package org.hhu.chatgpt.applicationservice;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.hhu.chatgpt.application.applicationservice.BetreuerWriteService;
import org.hhu.chatgpt.application.applicationservice.repository.BetreuerRepository;
import org.hhu.chatgpt.domain.model.Betreuer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class BetreuerWriteServiceTest {

    private BetreuerRepository repository;
    private BetreuerWriteService service;

    @BeforeEach
    void setUp() {
        repository = mock(BetreuerRepository.class);
        service = new BetreuerWriteService(repository);
    }

    private static Betreuer b(int fachId) {
        return new Betreuer(
                fachId,
                "Dr.",
                "Anna",
                "Meyer",
                "x" + fachId + "@example.com",
                "+49123456",
                Set.of("AI"),
                Set.of()
        );
    }

    @Test
    @DisplayName("Die Methode create() gibt false zurück, wenn bereits ein Betreuer mit der FachId existiert.")
    void test1() {
        Betreuer betreuer = b(10);
        when(repository.findByFachId(10)).thenReturn(Optional.of(betreuer));

        boolean result = service.create(betreuer);

        assertThat(result).isFalse();
        verify(repository).findByFachId(10);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Die Methode create() speichert den Betreuer und gibt true zurück, wenn noch kein Eintrag existiert.")
    void test2() {
        Betreuer betreuer = b(11);
        when(repository.findByFachId(11)).thenReturn(Optional.empty());

        boolean result = service.create(betreuer);

        assertThat(result).isTrue();
        verify(repository).findByFachId(11);
        verify(repository).save(betreuer);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Die Methode updateProfile() speichert den übergebenen Betreuer im Repository.")
    void test3() {
        Betreuer updated = b(20);

        service.updateProfile(updated);

        verify(repository).save(updated);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Die Methode deleteById() löscht den Betreuer und gibt true zurück, wenn er existiert.")
    void test4() {
        Betreuer existing = b(30);
        when(repository.findByFachId(30)).thenReturn(Optional.of(existing));

        boolean result = service.deleteById(30);

        assertThat(result).isTrue();
        verify(repository).findByFachId(30);
        verify(repository).delete(existing);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Die Methode deleteById() gibt false zurück, wenn kein Betreuer mit dieser FachId existiert.")
    void test5() {
        when(repository.findByFachId(40)).thenReturn(Optional.empty());

        boolean result = service.deleteById(40);

        assertThat(result).isFalse();
        verify(repository).findByFachId(40);
        verifyNoMoreInteractions(repository);
    }
}
