package org.hhu.chatgpt.web;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.hhu.chatgpt.application.applicationservice.BetreuerReadService;
import org.hhu.chatgpt.application.applicationservice.MatchingService;
import org.hhu.chatgpt.application.applicationservice.ThemaService;
import org.hhu.chatgpt.domain.model.Betreuer;
import org.hhu.chatgpt.domain.model.MatchingResult;
import org.hhu.chatgpt.domain.model.Thema;
import org.hhu.chatgpt.domain.model.ThemaListItem;
import org.hhu.chatgpt.helper.WithMockOAuth2User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChatGptController.class)
@WithMockOAuth2User(id = 999, roles = {"STUDENT"})
class ChatGptControllerMvcTest {

    @Autowired MockMvc mockMvc;

    @MockBean ThemaService themaService;
    @MockBean BetreuerReadService betreuerReadService;
    @MockBean
    MatchingService matchingService;

    @Test
    @DisplayName("Startseite: GET / rendert student/home")
    void root_rendersHome() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/home"));
    }

    @Test
    @DisplayName("Home: GET /home mit OAuth2-User rendert student/home")
    void home_rendersHome() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/home"));
    }

    @Test
    @DisplayName("Themen: GET /themen ohne Filter setzt Model-Attribute und rendert student/themen")
    void themen_withoutFilters() throws Exception {
        List<ThemaListItem> items = List.of(mock(ThemaListItem.class), mock(ThemaListItem.class));
        when(themaService.getGefilterteThemen(null, null, null)).thenReturn(items);

        mockMvc.perform(get("/themen"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/themen"))
                .andExpect(model().attribute("themenListe", items))
                .andExpect(model().attribute("resultCount", 2))
                .andExpect(model().attributeExists("allTags"))
                .andExpect(model().attributeExists("allRequirements"));

        verify(themaService).getGefilterteThemen(null, null, null);
        verifyNoInteractions(betreuerReadService, matchingService);
    }

    @Test
    @DisplayName("Betreuende: GET /betreuende ohne Filter rendert Liste")
    void betreuende_withoutFilters() throws Exception {
        List<Betreuer> list = List.of(
                new Betreuer(123, "Dr.", "Max", "Mustermann", "max@example.com", null, Set.of(), Set.of())
        );
        when(betreuerReadService.getGesuchteName(null, null)).thenReturn(list);

        mockMvc.perform(get("/betreuende"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/betreuende"))
                .andExpect(model().attribute("betreuendeListe", list))
                .andExpect(model().attributeExists("allTags"));

        verify(betreuerReadService).getGesuchteName(null, null);
        verifyNoInteractions(themaService, matchingService);
    }

    @Test
    @DisplayName("Profil: GET /betreuende/{fachId} wenn gefunden -> rendert Profil-View und ThemaListe")
    void publicProfil_found() throws Exception {
        int fachId = 777;

        Betreuer b = new Betreuer(fachId, "Prof.", "Anna", "Muster", "anna@example.com", null, Set.of("ML"), Set.of());
        b.setThemenSet(Set.of(mock(Thema.class)));

        when(betreuerReadService.findProfile(fachId)).thenReturn(Optional.of(b));

        mockMvc.perform(get("/betreuende/{fachId}", fachId))
                .andExpect(status().isOk())
                .andExpect(view().name("student/view/betreuerProfilView"))
                .andExpect(model().attribute("betreuer", b))
                .andExpect(model().attributeExists("themaListe"));

        verify(betreuerReadService).findProfile(fachId);
        verifyNoInteractions(themaService, matchingService);
    }

    @Test
    @DisplayName("Profil: GET /betreuende/{fachId} wenn nicht gefunden -> 404")
    void publicProfil_notFound() throws Exception {
        int fachId = 404;
        when(betreuerReadService.findProfile(fachId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/betreuende/{fachId}", fachId))
                .andExpect(status().isNotFound());

        verify(betreuerReadService).findProfile(fachId);
        verifyNoInteractions(themaService, matchingService);
    }

    @Test
    @DisplayName("Matching: GET /matching ohne Parameter rendert Seite ohne Service-Call")
    void matching_withoutParams() throws Exception {
        mockMvc.perform(get("/matching"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/matching"))
                .andExpect(model().attributeExists("matching"))
                .andExpect(model().attributeDoesNotExist("results"));

        verifyNoInteractions(matchingService);
    }

    @Test
    @DisplayName("Matching: GET /matching mit Parametern ruft Service auf und setzt results (template-safe)")
    void matching_withParams() throws Exception {
       Thema thema = mock(Thema.class);
        when(thema.getTitle()).thenReturn("Thema A");

        Betreuer betreuer = mock(Betreuer.class);
        when(betreuer.getTitel()).thenReturn("Dr.");
        when(betreuer.getFirstName()).thenReturn("Max");
        when(betreuer.getLastName()).thenReturn("Mustermann");

        MatchingResult r = mock(MatchingResult.class);
        when(r.getThema()).thenReturn(thema);
        when(r.getBetreuer()).thenReturn(betreuer);

        List<MatchingResult> results = List.of(r);
        when(matchingService.getMatchingResults(anyList(), anyList())).thenReturn(results);

        mockMvc.perform(get("/matching")
                        .param("kurse", "Programmierung", "Datenbanken")
                        .param("interessen", "KI", "ML"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/matching"))
                .andExpect(model().attributeExists("matching"))
                .andExpect(model().attribute("results", results));

        verify(matchingService).getMatchingResults(
                eq(List.of("Programmierung", "Datenbanken")),
                eq(List.of("KI", "ML"))
        );
    }

}
