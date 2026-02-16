package org.hhu.chatgpt.applicationservice;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.hhu.chatgpt.application.applicationservice.ThemaService;
import org.hhu.chatgpt.application.applicationservice.repository.BetreuerRepository;
import org.hhu.chatgpt.domain.model.Betreuer;
import org.hhu.chatgpt.domain.model.Thema;
import org.hhu.chatgpt.domain.model.ThemaListItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class ThemaServiceTest {

    private BetreuerRepository repository;
    private ThemaService service;

    @BeforeEach
    void setUp() {
        repository = mock(BetreuerRepository.class);
        service = new ThemaService(repository);
    }

    private static Betreuer betreuer(int fachId, String firstName, String lastName, Set<Thema> themen) {
        Betreuer b = new Betreuer(
                fachId,
                "Dr.",
                firstName,
                lastName,
                "x" + fachId + "@example.com",
                "+49123456",
                Set.of("AI"),
                themen
        );
        b.setThemenSet(themen);
        return b;
    }

    private static Thema thema(String title, String desc, Set<String> tags, Set<String> reqs) {
        return new Thema(title, desc, tags, reqs);
    }

    @Test
    @DisplayName("Die Methode sammelt Themen aller Betreuer und gibt sie als ThemaListItem-Liste zurück.")
    void test1() {
        Thema t1 = thema("KI Projekt", "Desc1", Set.of("AI"), Set.of("OS"));
        Thema t2 = thema("DB Projekt", "Desc2", Set.of("DB"), Set.of("DB1"));
        Betreuer b1 = betreuer(1, "Anna", "Meyer", Set.of(t1));
        Betreuer b2 = betreuer(2, "Max", "Mustermann", Set.of(t2));

        when(repository.findAll()).thenReturn(List.of(b1, b2));

        List<ThemaListItem> result = service.getGefilterteThemen(null, null, null);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ThemaListItem::title)
                .containsExactlyInAnyOrder("KI Projekt", "DB Projekt");
        assertThat(result).extracting(ThemaListItem::betreuerName)
                .containsExactlyInAnyOrder("Anna Meyer", "Max Mustermann");

        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Die Titelsuche ist case-insensitive und filtert nach enthaltenem Suchtext.")
    void test2() {
        Thema t1 = thema("Machine Learning", "Desc", Set.of("AI"), Set.of("OS"));
        Thema t2 = thema("Datenbanken", "Desc", Set.of("DB"), Set.of("DB1"));
        Betreuer b1 = betreuer(1, "Anna", "Meyer", Set.of(t1, t2));

        when(repository.findAll()).thenReturn(List.of(b1));

        List<ThemaListItem> result = service.getGefilterteThemen("LEARN", null, null);

        assertThat(result).extracting(ThemaListItem::title).containsExactly("Machine Learning");
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Der Tag-Filter liefert nur Themen zurück, deren Tags den angegebenen Tag enthalten.")
    void test3() {
        Thema t1 = thema("KI", "Desc", Set.of("AI", "ML"), Set.of("OS"));
        Thema t2 = thema("DB", "Desc", Set.of("DB"), Set.of("DB1"));
        Betreuer b1 = betreuer(1, "Anna", "Meyer", Set.of(t1, t2));

        when(repository.findAll()).thenReturn(List.of(b1));

        List<ThemaListItem> result = service.getGefilterteThemen(null, "AI", null);

        assertThat(result).extracting(ThemaListItem::title).containsExactly("KI");
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Der Requirement-Filter liefert nur Themen zurück, deren Requirements die Veranstaltung enthalten.")
    void test4() {
        Thema t1 = thema("KI", "Desc", Set.of("AI"), Set.of("OS", "LA"));
        Thema t2 = thema("DB", "Desc", Set.of("DB"), Set.of("DB1"));
        Betreuer b1 = betreuer(1, "Anna", "Meyer", Set.of(t1, t2));

        when(repository.findAll()).thenReturn(List.of(b1));

        List<ThemaListItem> result = service.getGefilterteThemen(null, null, "LA");

        assertThat(result).extracting(ThemaListItem::title).containsExactly("KI");
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Alle Filter werden gemeinsam angewendet, sodass nur Themen übrig bleiben, die alles erfüllen.")
    void test5() {
        Thema ok = thema("Machine Learning", "Desc", Set.of("AI"), Set.of("OS"));
        Thema wrongTitle = thema("Datenbanken", "Desc", Set.of("AI"), Set.of("OS"));
        Thema wrongTag = thema("Machine Learning", "Desc", Set.of("DB"), Set.of("OS"));
        Thema wrongReq = thema("Machine Learning", "Desc", Set.of("AI"), Set.of("DB1"));
        Betreuer b1 = betreuer(1, "Anna", "Meyer", Set.of(ok, wrongTitle, wrongTag, wrongReq));

        when(repository.findAll()).thenReturn(List.of(b1));

        List<ThemaListItem> result = service.getGefilterteThemen("learn", "AI", "OS");

        assertThat(result).extracting(ThemaListItem::title).containsExactly("Machine Learning");
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Wenn Vor- und Nachname fehlen, wird der Betreuername als Unbekannt gesetzt.")
    void test6() {
        Thema t1 = thema("KI", "Desc", Set.of("AI"), Set.of("OS"));
        Betreuer b1 = betreuer(1, null, null, Set.of(t1));

        when(repository.findAll()).thenReturn(List.of(b1));

        List<ThemaListItem> result = service.getGefilterteThemen(null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).betreuerName()).isEqualTo("Unbekannt");
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Vor- und Nachname werden getrimmt, sodass überflüssige Leerzeichen entfernt werden.")
    void test7() {
        Thema t1 = thema("KI", "Desc", Set.of("AI"), Set.of("OS"));
        Betreuer b1 = betreuer(1, "  Anna  ", "  Meyer  ", Set.of(t1));

        when(repository.findAll()).thenReturn(List.of(b1));

        List<ThemaListItem> result = service.getGefilterteThemen(null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).betreuerName()).isEqualTo("Anna Meyer");
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }
}
