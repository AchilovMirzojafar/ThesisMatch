package org.hhu.thesismatch.web;

import static org.hhu.thesismatch.helper.TestDaten.betreuer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.hhu.thesismatch.application.applicationservice.BetreuerNichtGefundenException;
import org.hhu.thesismatch.application.applicationservice.BetreuerReadService;
import org.hhu.thesismatch.application.applicationservice.BetreuerWriteService;
import org.hhu.thesismatch.application.applicationservice.KonkurrierendeAenderungException;
import org.hhu.thesismatch.application.applicationservice.ProfilDaten;
import org.hhu.thesismatch.application.applicationservice.ThemaService;
import org.hhu.thesismatch.config.AppUserService;
import org.hhu.thesismatch.config.MethodSecurityConfig;
import org.hhu.thesismatch.config.RoleBasedSuccessHandler;
import org.hhu.thesismatch.config.SecurityConfig;
import org.hhu.thesismatch.domain.model.GitHubId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@WebMvcTest(BetreuerController.class)
@Import({SecurityConfig.class, MethodSecurityConfig.class})
class BetreuerControllerMvcTest {

    private static final int USER_ID = 999;
    private static final GitHubId ID = new GitHubId(USER_ID);

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    BetreuerWriteService betreuerWriteService;

    @MockitoBean
    BetreuerReadService betreuerReadService;

    @MockitoBean
    ThemaService themaService;

    @MockitoBean
    RoleBasedSuccessHandler successHandler;

    @MockitoBean
    AppUserService appUserService;

    private static RequestPostProcessor login(String role) {
        return oauth2Login()
                .authorities(new SimpleGrantedAuthority(role))
                .attributes(a -> a.putAll(Map.of("id", USER_ID)));
    }

    @Test
    void profil_unauthenticated_redirects() throws Exception {
        mockMvc.perform(get("/profil"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void profil_asStudent_forbidden() throws Exception {
        mockMvc.perform(get("/profil").with(login("ROLE_STUDENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void profil_asBetreuer_rendersProfil() throws Exception {
        when(betreuerReadService.findProfile(ID)).thenReturn(Optional.of(betreuer(USER_ID)));

        mockMvc.perform(get("/profil").with(login("ROLE_BETREUER")))
                .andExpect(status().isOk())
                .andExpect(view().name("betreuer/profil"))
                .andExpect(model().attributeExists("betreuer", "allTags", "themaListe"));

        verifyNoInteractions(betreuerWriteService, themaService);
    }

    @Test
    void profil_withoutBetreuerProfile_returns404() throws Exception {
        when(betreuerReadService.findProfile(ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/profil").with(login("ROLE_ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void edit_rendersFormWithCurrentData() throws Exception {
        when(betreuerReadService.findProfile(ID)).thenReturn(Optional.of(betreuer(USER_ID)));

        mockMvc.perform(get("/edit").with(login("ROLE_BETREUER")))
                .andExpect(status().isOk())
                .andExpect(view().name("betreuer/edit"))
                .andExpect(model().attribute("betreuer",
                        org.hamcrest.Matchers.hasProperty("lastName", org.hamcrest.Matchers.is("Meyer"))))
                .andExpect(model().attributeExists("allTags"));
    }

    @Test
    void updateProfile_invalid_rendersEditWithErrors() throws Exception {
        mockMvc.perform(post("/profil").with(login("ROLE_BETREUER")).with(csrf())
                        .param("titel", "Dr.")
                        .param("firstName", "Max")
                        .param("lastName", "Mustermann")
                        .param("email", "invalid-email"))
                .andExpect(status().isOk())
                .andExpect(view().name("betreuer/edit"))
                .andExpect(model().attributeHasFieldErrors("betreuer", "email"));

        verifyNoInteractions(betreuerWriteService);
    }

    @Test
    void updateProfile_valid_callsServiceWithLoggedInUser() throws Exception {
        mockMvc.perform(post("/profil").with(login("ROLE_BETREUER")).with(csrf())
                        .param("version", "3")
                        .param("fachId", "1")
                        .param("titel", "Dr.")
                        .param("firstName", "Max")
                        .param("lastName", "Mustermann")
                        .param("email", "max@example.com")
                        .param("phone", "")
                        .param("tags", "ML"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profil"));

        // fachId aus dem Request wird ignoriert, es zählt nur der eingeloggte Nutzer
        verify(betreuerWriteService).updateProfile(eq(ID), eq(3), any(ProfilDaten.class));
    }

    @Test
    void updateProfile_concurrentChange_showsGlobalError() throws Exception {
        doThrow(new KonkurrierendeAenderungException())
                .when(betreuerWriteService).updateProfile(eq(ID), eq(1), any(ProfilDaten.class));

        mockMvc.perform(post("/profil").with(login("ROLE_BETREUER")).with(csrf())
                        .param("version", "1")
                        .param("titel", "Dr.")
                        .param("firstName", "Max")
                        .param("lastName", "Mustermann")
                        .param("email", "max@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("betreuer/edit"))
                .andExpect(model().hasErrors());
    }

    @Test
    void newThema_rendersForm() throws Exception {
        mockMvc.perform(get("/newThema").with(login("ROLE_BETREUER")))
                .andExpect(status().isOk())
                .andExpect(view().name("thema/newThema"))
                .andExpect(model().attributeExists("thema", "allTags", "allRequirements"));

        verifyNoInteractions(betreuerReadService, betreuerWriteService, themaService);
    }

    @Test
    void createNewThema_invalid_rendersFormAgain() throws Exception {
        mockMvc.perform(post("/newThema").with(login("ROLE_BETREUER")).with(csrf())
                        .param("title", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("thema/newThema"))
                .andExpect(model().attributeHasFieldErrors("thema", "title", "tags", "requirements"));

        verifyNoInteractions(themaService);
    }

    @Test
    void createNewThema_valid_callsService() throws Exception {
        mockMvc.perform(post("/newThema").with(login("ROLE_BETREUER")).with(csrf())
                        .param("title", "Neues Thema")
                        .param("description", "Beschreibung")
                        .param("tags", "KI", "ML")
                        .param("requirements", "Data Science"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profil"));

        verify(themaService).createThema(eq(ID), eq("Neues Thema"), eq("Beschreibung"),
                eq(Set.of("KI", "ML")), eq(Set.of("Data Science")));
    }

    @Test
    void createNewThema_withoutBetreuerProfile_returns404() throws Exception {
        when(themaService.createThema(eq(ID), any(), any(), anySet(), anySet()))
                .thenThrow(new BetreuerNichtGefundenException(ID));

        mockMvc.perform(post("/newThema").with(login("ROLE_ADMIN")).with(csrf())
                        .param("title", "Neues Thema")
                        .param("tags", "KI")
                        .param("requirements", "Data Science"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteThema_callsServiceWithLoggedInUser() throws Exception {
        UUID themaId = UUID.randomUUID();

        mockMvc.perform(post("/thema/" + themaId + "/delete").with(login("ROLE_BETREUER")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profil"));

        verify(themaService).deleteThema(ID, themaId);
    }
}
