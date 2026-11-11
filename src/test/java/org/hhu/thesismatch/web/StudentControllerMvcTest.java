package org.hhu.thesismatch.web;

import static org.hhu.thesismatch.helper.TestDaten.betreuerMitThemen;
import static org.hhu.thesismatch.helper.TestDaten.thema;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.hhu.thesismatch.application.applicationservice.BetreuerReadService;
import org.hhu.thesismatch.application.applicationservice.MatchingService;
import org.hhu.thesismatch.application.applicationservice.ThemaListItem;
import org.hhu.thesismatch.application.applicationservice.ThemaService;
import org.hhu.thesismatch.domain.model.Betreuer;
import org.hhu.thesismatch.domain.model.GitHubId;
import org.hhu.thesismatch.domain.model.MatchingResult;
import org.hhu.thesismatch.domain.model.Thema;
import org.hhu.thesismatch.helper.WithMockOAuth2User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StudentController.class)
@WithMockOAuth2User(id = 999, roles = {"STUDENT"})
class StudentControllerMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ThemaService themaService;

    @MockitoBean
    BetreuerReadService betreuerReadService;

    @MockitoBean
    MatchingService matchingService;

    @Test
    @DisplayName("GET / und GET /home rendern student/home")
    void home() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("student/home"));
        mockMvc.perform(get("/home")).andExpect(status().isOk()).andExpect(view().name("student/home"));
    }

    @Test
    @DisplayName("GET /themen setzt Liste, Anzahl und Kataloge")
    void themen_withoutFilters() throws Exception {
        List<ThemaListItem> items = List.of(
                new ThemaListItem("A", "", Set.of("KI"), Set.of("Programmierung"), "Anna Meyer"));
        when(themaService.getGefilterteThemen(null, null, null)).thenReturn(items);

        mockMvc.perform(get("/themen"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/themen"))
                .andExpect(model().attribute("themenListe", items))
                .andExpect(model().attribute("resultCount", 1))
                .andExpect(model().attributeExists("allTags", "allRequirements"));

        verifyNoInteractions(betreuerReadService, matchingService);
    }

    @Test
    @DisplayName("GET /betreuende ruft die Suche auf")
    void betreuende() throws Exception {
        List<Betreuer> list = List.of(betreuerMitThemen(123, Set.of()));
        when(betreuerReadService.search("Meyer", "KI")).thenReturn(list);

        mockMvc.perform(get("/betreuende").param("searchname", "Meyer").param("tag", "KI"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/betreuende"))
                .andExpect(model().attribute("betreuendeListe", list));
    }

    @Test
    @DisplayName("GET /betreuende/{fachId} zeigt Profil mit Themen")
    void publicProfil_found() throws Exception {
        Thema t = thema("T", Set.of("KI"), Set.of("Programmierung"));
        Betreuer b = betreuerMitThemen(777, Set.of("ML"), t);
        when(betreuerReadService.findProfile(new GitHubId(777))).thenReturn(Optional.of(b));

        mockMvc.perform(get("/betreuende/{fachId}", 777))
                .andExpect(status().isOk())
                .andExpect(view().name("student/view/betreuerProfilView"))
                .andExpect(model().attribute("betreuer", b))
                .andExpect(model().attribute("themaListe", List.of(t)));
    }

    @Test
    @DisplayName("GET /betreuende/{fachId} für unbekannten Betreuer liefert 404")
    void publicProfil_notFound() throws Exception {
        when(betreuerReadService.findProfile(new GitHubId(404))).thenReturn(Optional.empty());

        mockMvc.perform(get("/betreuende/{fachId}", 404))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /matching ohne Auswahl zeigt keine Ergebnisse")
    void matching_withoutParams() throws Exception {
        when(matchingService.getMatchingResults(null, null)).thenReturn(List.of());

        mockMvc.perform(get("/matching"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/matching"))
                .andExpect(model().attributeExists("matching"))
                .andExpect(model().attributeDoesNotExist("results"));
    }

    @Test
    @DisplayName("GET /matching mit Auswahl übergibt Parameter und zeigt Ergebnisse")
    void matching_withParams() throws Exception {
        Thema t = thema("Thema A", Set.of("KI"), Set.of("Programmierung"));
        Betreuer b = betreuerMitThemen(1, Set.of(), t);
        List<MatchingResult> results = List.of(new MatchingResult(t, b, 1, 1, 1, 1, 100));
        when(matchingService.getMatchingResults(any(), any())).thenReturn(results);

        mockMvc.perform(get("/matching")
                        .param("kurse", "Programmierung", "Datenbanken")
                        .param("interessen", "KI", "ML"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("results", results));

        verify(matchingService).getMatchingResults(
                List.of("Programmierung", "Datenbanken"), List.of("KI", "ML"));
    }
}
