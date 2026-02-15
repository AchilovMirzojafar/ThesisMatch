package org.hhu.chatgpt.web;

import org.hhu.chatgpt.application.applicationservice.BetreuerReadService;
import org.hhu.chatgpt.application.applicationservice.BetreuerWriteService;
import org.hhu.chatgpt.config.AppUserService;
import org.hhu.chatgpt.config.MethodSecurityConfig;
import org.hhu.chatgpt.config.RoleBasedSuccessHandler;
import org.hhu.chatgpt.config.SecurityConfig;
import org.hhu.chatgpt.domain.model.Betreuer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@Import({SecurityConfig.class, MethodSecurityConfig.class})
class AdminControllerMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BetreuerWriteService betreuerWriteService;

    @MockBean
    BetreuerReadService betreuerReadService;

    @MockBean
    RoleBasedSuccessHandler successHandler;

    @MockBean
    AppUserService appUserService;

    @Test
    void getAdmin_unauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection());
        // je nach Setup: Redirect auf /oauth2/authorization/github oder /login
    }

    @Test
    void getAdmin_authenticatedButNotAdmin_forbidden() throws Exception {
        mockMvc.perform(get("/admin")
                        .with(oauth2Login()
                                .authorities(new SimpleGrantedAuthority("ROLE_STUDENT"))
                                .attributes(a -> a.putAll(Map.of("id", 12345)))))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAdmin_asAdmin_ok_andModel() throws Exception {
        when(betreuerReadService.list()).thenReturn(List.of(new Betreuer(), new Betreuer()));

        mockMvc.perform(get("/admin")
                        .with(oauth2Login()
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                                .attributes(a -> a.putAll(Map.of("id", 999)))))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin"))
                .andExpect(model().attributeExists("betreuer"))
                .andExpect(model().attributeExists("betreuerListe"));

        verify(betreuerReadService).list();
        verifyNoInteractions(betreuerWriteService);
    }

    @Test
    void postAdmin_asAdmin_created_redirects() throws Exception {
        when(betreuerWriteService.create(any(Betreuer.class))).thenReturn(true);

        mockMvc.perform(post("/admin")
                        .with(oauth2Login()
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                                .attributes(a -> a.putAll(Map.of("id", 999))))
                        .with(csrf())
                        .param("fachId", "123456")   // muss Integer sein!
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
    void postDelete_asAdmin_setsFlashMessage() throws Exception {
        when(betreuerWriteService.deleteById(1)).thenReturn(true);

        mockMvc.perform(post("/admin/delete")
                        .with(oauth2Login()
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                                .attributes(a -> a.putAll(Map.of("id", 999))))
                        .with(csrf())
                        .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"))
                .andExpect(flash().attribute("message", "Betreuer gelöscht."));

        verify(betreuerWriteService).deleteById(1);
    }
}
