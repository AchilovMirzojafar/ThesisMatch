package org.hhu.chatgpt.web;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Set;

import org.hhu.chatgpt.application.applicationservice.BetreuerReadService;
import org.hhu.chatgpt.application.applicationservice.BetreuerWriteService;
import org.hhu.chatgpt.domain.model.Betreuer;
import org.hhu.chatgpt.helper.WithMockOAuth2User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminController.class)
@WithMockOAuth2User(roles = {"ADMIN"})
class AdminControllerMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    BetreuerWriteService betreuerWriteService;

    @MockitoBean
    BetreuerReadService betreuerReadService;

    private static Betreuer sampleBetreuer() {
        return new Betreuer(
                1,
                "Dr.",
                "Max",
                "Mustermann",
                "max@example.com",
                null,
                Set.of(),
                Set.of()
        );
    }

    @Test
    @DisplayName("GET /admin: rendert admin/admin und setzt betreuer + betreuerListe")
    void admin_get_rendersPage() throws Exception {
        when(betreuerReadService.list()).thenReturn(List.of(sampleBetreuer()));

        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin"))
                .andExpect(model().attributeExists("betreuer"))
                .andExpect(model().attributeExists("betreuerListe"));

        verify(betreuerReadService).list();
        verifyNoInteractions(betreuerWriteService);
    }

    @Test
    @DisplayName("POST /admin: bei Validation-Fehlern bleibt auf admin/admin und ruft create nicht auf")
    void admin_post_validationErrors_staysOnPage() throws Exception {
        when(betreuerReadService.list()).thenReturn(List.of(sampleBetreuer()));

        mockMvc.perform(post("/admin")
                        .with(csrf())
                        .param("fachId", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin"))
                .andExpect(model().attributeExists("betreuer"))
                .andExpect(model().attributeExists("betreuerListe"))
                .andExpect(model().hasErrors());

        verify(betreuerReadService).list();
        verifyNoInteractions(betreuerWriteService);
    }

    @Test
    @DisplayName("POST /admin: valid + create=true -> redirect:/admin")
    void admin_post_success_redirects() throws Exception {
        when(betreuerWriteService.create(any(Betreuer.class))).thenReturn(true);

        mockMvc.perform(post("/admin")
                        .with(csrf())
                        .param("fachId", "123")
                        .param("titel", "Dr.")
                        .param("firstName", "Max")
                        .param("lastName", "Mustermann")
                        .param("email", "max@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        verify(betreuerWriteService).create(any(Betreuer.class));
        verifyNoInteractions(betreuerReadService);
    }

    @Test
    @DisplayName("POST /admin: valid + create=false -> admin/admin + FieldError fachId (duplicate)")
    void admin_post_duplicate_showsError() throws Exception {
        when(betreuerWriteService.create(any(Betreuer.class))).thenReturn(false);
        when(betreuerReadService.list()).thenReturn(List.of(sampleBetreuer()));

        mockMvc.perform(post("/admin")
                        .with(csrf())
                        .param("fachId", "123")
                        .param("titel", "Dr.")
                        .param("firstName", "Max")
                        .param("lastName", "Mustermann")
                        .param("email", "max@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin"))
                .andExpect(model().attributeExists("betreuerListe"))
                .andExpect(model().attributeHasFieldErrors("betreuer", "fachId"));

        verify(betreuerWriteService).create(any(Betreuer.class));
        verify(betreuerReadService).list();
    }

    @Test
    @DisplayName("POST /admin/delete: deleted=true -> redirect + Flash 'Betreuer gelöscht.'")
    void admin_delete_success_flashMessage() throws Exception {
        when(betreuerWriteService.deleteById(10)).thenReturn(true);

        mockMvc.perform(post("/admin/delete")
                        .with(csrf())
                        .param("id", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"))
                .andExpect(flash().attribute("message", "Betreuer gelöscht."));

        verify(betreuerWriteService).deleteById(10);
    }

    @Test
    @DisplayName("POST /admin/delete: deleted=false -> redirect + Flash 'Betreuer nicht gefunden.'")
    void admin_delete_notFound_flashMessage() throws Exception {
        when(betreuerWriteService.deleteById(10)).thenReturn(false);

        mockMvc.perform(post("/admin/delete")
                        .with(csrf())
                        .param("id", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"))
                .andExpect(flash().attribute("message", "Betreuer nicht gefunden."));

        verify(betreuerWriteService).deleteById(10);
    }
}
