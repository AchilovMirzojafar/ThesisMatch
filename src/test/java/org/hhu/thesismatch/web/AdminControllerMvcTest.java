package org.hhu.thesismatch.web;

import static org.hhu.thesismatch.helper.TestDaten.betreuer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.hhu.thesismatch.application.applicationservice.BetreuerExistiertBereitsException;
import org.hhu.thesismatch.application.applicationservice.BetreuerReadService;
import org.hhu.thesismatch.application.applicationservice.BetreuerWriteService;
import org.hhu.thesismatch.config.MethodSecurityConfig;
import org.hhu.thesismatch.domain.model.Email;
import org.hhu.thesismatch.domain.model.GitHubId;
import org.hhu.thesismatch.helper.WithMockOAuth2User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminController.class)
@Import(MethodSecurityConfig.class)
@WithMockOAuth2User(roles = {"ADMIN"})
class AdminControllerMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    BetreuerWriteService betreuerWriteService;

    @MockitoBean
    BetreuerReadService betreuerReadService;

    @Test
    @DisplayName("GET /admin rendert Formular und Liste")
    void admin_get_rendersPage() throws Exception {
        when(betreuerReadService.list()).thenReturn(List.of(betreuer(1)));

        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin"))
                .andExpect(model().attributeExists("betreuer", "betreuerListe"));

        verifyNoInteractions(betreuerWriteService);
    }

    @Test
    @DisplayName("POST /admin mit Validierungsfehlern bleibt auf der Seite")
    void admin_post_validationErrors_staysOnPage() throws Exception {
        when(betreuerReadService.list()).thenReturn(List.of());

        mockMvc.perform(post("/admin").with(csrf()).param("fachId", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin"))
                .andExpect(model().attributeHasFieldErrors("betreuer", "titel", "firstName", "lastName", "email"));

        verifyNoInteractions(betreuerWriteService);
    }

    @Test
    @DisplayName("POST /admin mit gültigen Daten legt Betreuer an und leitet weiter")
    void admin_post_success_redirects() throws Exception {
        mockMvc.perform(post("/admin").with(csrf())
                        .param("fachId", "123")
                        .param("titel", "Dr.")
                        .param("firstName", "Max")
                        .param("lastName", "Mustermann")
                        .param("email", "max@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        verify(betreuerWriteService).create(new GitHubId(123), "Dr.", "Max", "Mustermann",
                new Email("max@example.com"));
        verifyNoInteractions(betreuerReadService);
    }

    @Test
    @DisplayName("POST /admin mit vergebener GitHub-ID zeigt Fehler am Feld fachId")
    void admin_post_duplicate_showsError() throws Exception {
        doThrow(new BetreuerExistiertBereitsException(new GitHubId(123)))
                .when(betreuerWriteService)
                .create(eq(new GitHubId(123)), anyString(), anyString(), anyString(), any(Email.class));
        when(betreuerReadService.list()).thenReturn(List.of());

        mockMvc.perform(post("/admin").with(csrf())
                        .param("fachId", "123")
                        .param("titel", "Dr.")
                        .param("firstName", "Max")
                        .param("lastName", "Mustermann")
                        .param("email", "max@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin"))
                .andExpect(model().attributeHasFieldErrors("betreuer", "fachId"));
    }

    @Test
    @DisplayName("POST /admin/delete zeigt passende Flash-Nachricht")
    void admin_delete_flashMessages() throws Exception {
        when(betreuerWriteService.delete(new GitHubId(10))).thenReturn(true);
        when(betreuerWriteService.delete(new GitHubId(11))).thenReturn(false);

        mockMvc.perform(post("/admin/delete").with(csrf()).param("id", "10"))
                .andExpect(redirectedUrl("/admin"))
                .andExpect(flash().attribute("message", "Betreuer gelöscht."));

        mockMvc.perform(post("/admin/delete").with(csrf()).param("id", "11"))
                .andExpect(redirectedUrl("/admin"))
                .andExpect(flash().attribute("message", "Betreuer nicht gefunden."));
    }

    @Test
    @DisplayName("POST /admin/delete mit ungültiger ID liefert 400")
    void admin_delete_invalidId() throws Exception {
        mockMvc.perform(post("/admin/delete").with(csrf()).param("id", "-1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(betreuerWriteService);
    }

    @Test
    @WithMockOAuth2User(roles = {"STUDENT"})
    @DisplayName("Studierende haben keinen Zugriff auf /admin")
    void admin_asStudent_forbidden() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }
}
