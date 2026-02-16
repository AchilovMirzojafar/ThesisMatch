package org.hhu.chatgpt.web;

import org.hhu.chatgpt.application.applicationservice.BetreuerReadService;
import org.hhu.chatgpt.application.applicationservice.BetreuerWriteService;
import org.hhu.chatgpt.application.applicationservice.repository.BetreuerRepository;
import org.hhu.chatgpt.config.AppUserService;
import org.hhu.chatgpt.config.RoleBasedSuccessHandler;
import org.hhu.chatgpt.config.SecurityConfig;
import org.hhu.chatgpt.config.MethodSecurityConfig;
import org.hhu.chatgpt.domain.model.Betreuer;
import org.hhu.chatgpt.domain.model.Thema;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BetreuerController.class)
@Import({SecurityConfig.class, MethodSecurityConfig.class})
class BetreuerControllerMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BetreuerWriteService betreuerWriteService;

    @MockBean
    BetreuerReadService betreuerReadService;

    @MockBean
    BetreuerRepository betreuerRepository;

    @MockBean
    RoleBasedSuccessHandler successHandler;

    @MockBean
    AppUserService appUserService;

    private static final int USER_ID = 999;

    private static Betreuer betreuerMitThemen(Set<Thema> themen) {
        Betreuer b = new Betreuer();
        b.setThemenSet(themen);
        b.setTags(Set.of("AI", "ML"));
        b.setFachId(123456);
        return b;
    }

    private static Thema thema(UUID id) {
        Thema t = new Thema();
        t.setId(id);
        t.setTags(Set.of());
        t.setRequirements(Set.of());
        return t;
    }

    @Test
    void profil_unauthenticated_redirects() throws Exception {
        mockMvc.perform(get("/profil"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void profil_asStudent_forbidden() throws Exception {
        mockMvc.perform(get("/profil")
                        .with(oauth2Login()
                                .authorities(new SimpleGrantedAuthority("ROLE_STUDENT"))
                                .attributes(a -> a.putAll(Map.of("id", USER_ID)))))
                .andExpect(status().isForbidden());
    }

    @Test
    void profil_asBetreuer_ok_modelAndView() throws Exception {
        Betreuer b = betreuerMitThemen(new HashSet<>());
        when(betreuerReadService.findProfile(USER_ID)).thenReturn(Optional.of(b));

        mockMvc.perform(get("/profil")
                        .with(oauth2Login()
                                .authorities(new SimpleGrantedAuthority("ROLE_BETREUER"))
                                .attributes(a -> a.putAll(Map.of("id", USER_ID)))))
                .andExpect(status().isOk())
                .andExpect(view().name("betreuer/profil"))
                .andExpect(model().attributeExists("betreuer"))
                .andExpect(model().attributeExists("allTags"))
                .andExpect(model().attributeExists("themaListe"));

        verify(betreuerReadService).findProfile(USER_ID);
        verifyNoInteractions(betreuerWriteService);
        verifyNoInteractions(betreuerRepository);
    }

    @Test
    void profil_notFound_returns404() throws Exception {
        when(betreuerReadService.findProfile(USER_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/profil")
                        .with(oauth2Login()
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                                .attributes(a -> a.putAll(Map.of("id", USER_ID)))))
                .andExpect(status().isNotFound());

        verify(betreuerReadService).findProfile(USER_ID);
    }

    @Test
    void edit_asBetreuer_ok_modelAndView() throws Exception {
        Betreuer b = betreuerMitThemen(new HashSet<>());
        when(betreuerReadService.findProfile(USER_ID)).thenReturn(Optional.of(b));

        mockMvc.perform(get("/edit")
                        .with(oauth2Login()
                                .authorities(new SimpleGrantedAuthority("ROLE_BETREUER"))
                                .attributes(a -> a.putAll(Map.of("id", USER_ID)))))
                .andExpect(status().isOk())
                .andExpect(view().name("betreuer/edit"))
                .andExpect(model().attributeExists("betreuer"))
                .andExpect(model().attributeExists("allTags"));

        verify(betreuerReadService).findProfile(USER_ID);
    }

    @Test
    void updateProfile_withValidationErrors_rendersEdit() throws Exception {
        Betreuer existing = betreuerMitThemen(new HashSet<>());
        existing.setTags(new LinkedHashSet<>());
        when(betreuerReadService.findProfile(USER_ID)).thenReturn(Optional.of(existing));

        mockMvc.perform(post("/profil")
                        .with(oauth2Login()
                                .authorities(new SimpleGrantedAuthority("ROLE_BETREUER"))
                                .attributes(a -> a.putAll(Map.of("id", USER_ID))))
                        .with(csrf())
                        .param("tags", "ML")
                        .param("email", "invalid-email"))
                .andExpect(status().isOk())
                .andExpect(view().name("betreuer/edit"))
                .andExpect(model().attributeExists("allTags"))
                .andExpect(model().attributeHasFieldErrors("betreuer", "fachId"));

        verify(betreuerReadService).findProfile(USER_ID);
        verifyNoInteractions(betreuerWriteService);
        verifyNoInteractions(betreuerRepository);
    }


    @Test
    void updateProfile_valid_redirects_andCallsService() throws Exception {
        Betreuer existing = betreuerMitThemen(new HashSet<>());
        existing.setFachId(123456);
        existing.setTags(new LinkedHashSet<>());

        when(betreuerReadService.findProfile(USER_ID)).thenReturn(Optional.of(existing));

        mockMvc.perform(post("/profil")
                        .with(oauth2Login()
                                .authorities(new SimpleGrantedAuthority("ROLE_BETREUER"))
                                .attributes(a -> a.putAll(Map.of("id", USER_ID))))
                        .with(csrf())
                        .param("titel", "Dr.")
                        .param("firstName", "Max")
                        .param("lastName", "Mustermann")
                        .param("email", "max@example.com")
                        .param("tags", "ML")
                        // WICHTIG: fachId ist @NotNull -> mitsenden!
                        .param("fachId", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profil"));

        verify(betreuerReadService).findProfile(USER_ID);
        verify(betreuerWriteService).updateProfile(any(Betreuer.class));
        verifyNoInteractions(betreuerRepository);
    }



    @Test
    void newThema_asBetreuer_ok() throws Exception {
        mockMvc.perform(get("/newThema")
                        .with(oauth2Login()
                                .authorities(new SimpleGrantedAuthority("ROLE_BETREUER"))
                                .attributes(a -> a.putAll(Map.of("id", USER_ID)))))
                .andExpect(status().isOk())
                .andExpect(view().name("thema/newThema"))
                .andExpect(model().attributeExists("thema"))
                .andExpect(model().attributeExists("allTags"))
                .andExpect(model().attributeExists("allRequirements"));

        verifyNoInteractions(betreuerReadService, betreuerWriteService, betreuerRepository);
    }

    @Test
    void createNewThema_validationErrors_rendersNewThema() throws Exception {
        Betreuer b = betreuerMitThemen(new HashSet<>());
        when(betreuerReadService.findProfile(USER_ID)).thenReturn(Optional.of(b));

        mockMvc.perform(post("/newThema")
                        .with(oauth2Login()
                                .authorities(new SimpleGrantedAuthority("ROLE_BETREUER"))
                                .attributes(a -> a.putAll(Map.of("id", USER_ID))))
                        .with(csrf())
                       .param("titel", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("thema/newThema"))
                .andExpect(model().attributeExists("allTags"))
                .andExpect(model().attributeExists("allRequirements"));

        verify(betreuerReadService).findProfile(USER_ID);
        verifyNoInteractions(betreuerRepository);
    }

    @Test
    void createNewThema_valid_redirects_andSavesBetreuer() throws Exception {
        Betreuer b = betreuerMitThemen(new HashSet<>());
        when(betreuerReadService.findProfile(USER_ID)).thenReturn(Optional.of(b));

        mockMvc.perform(post("/newThema")
                        .with(oauth2Login()
                                .authorities(new SimpleGrantedAuthority("ROLE_BETREUER"))
                                .attributes(a -> a.putAll(Map.of("id", USER_ID))))
                        .with(csrf())
                        .param("title", "Neues Thema")
                        .param("description", "Beschreibung")
                        .param("tags", "AI", "ML")
                        .param("requirements", "Data Science"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profil"));

        verify(betreuerReadService).findProfile(USER_ID);
        verify(betreuerRepository).save(any(Betreuer.class));
    }


    @Test
    void deleteThema_whenThemenSetNull_redirects_withoutSave() throws Exception {
        Betreuer b = betreuerMitThemen(null);
        when(betreuerReadService.findProfile(USER_ID)).thenReturn(Optional.of(b));

        UUID themaId = UUID.randomUUID();

        mockMvc.perform(post("/thema/" + themaId + "/delete")
                        .with(oauth2Login()
                                .authorities(new SimpleGrantedAuthority("ROLE_BETREUER"))
                                .attributes(a -> a.putAll(Map.of("id", USER_ID))))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profil"));

        verify(betreuerReadService).findProfile(USER_ID);
        verifyNoInteractions(betreuerRepository);
    }

    @Test
    void deleteThema_whenRemoved_saves_andRedirects() throws Exception {
        UUID themaId = UUID.randomUUID();
        Set<Thema> themen = new HashSet<>(Set.of(thema(themaId), thema(UUID.randomUUID())));
        Betreuer b = betreuerMitThemen(themen);

        when(betreuerReadService.findProfile(USER_ID)).thenReturn(Optional.of(b));

        mockMvc.perform(post("/thema/" + themaId + "/delete")
                        .with(oauth2Login()
                                .authorities(new SimpleGrantedAuthority("ROLE_BETREUER"))
                                .attributes(a -> a.putAll(Map.of("id", USER_ID))))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profil"));

        verify(betreuerReadService).findProfile(USER_ID);
        verify(betreuerRepository).save(any(Betreuer.class));
    }

    @Test
    void deleteThema_whenNotRemoved_doesNotSave_butRedirects() throws Exception {
        Set<Thema> themen = new HashSet<>(Set.of(thema(UUID.randomUUID())));
        Betreuer b = betreuerMitThemen(themen);

        when(betreuerReadService.findProfile(USER_ID)).thenReturn(Optional.of(b));

        UUID missingId = UUID.randomUUID();

        mockMvc.perform(post("/thema/" + missingId + "/delete")
                        .with(oauth2Login()
                                .authorities(new SimpleGrantedAuthority("ROLE_BETREUER"))
                                .attributes(a -> a.putAll(Map.of("id", USER_ID))))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profil"));

        verify(betreuerReadService).findProfile(USER_ID);
        verifyNoInteractions(betreuerRepository);
    }
}

