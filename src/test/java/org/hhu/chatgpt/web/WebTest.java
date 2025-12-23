package org.hhu.chatgpt.web;

import org.hhu.chatgpt.config.MethodSecurityConfig;
import org.hhu.chatgpt.config.RoleBasedSuccessHandler;
import org.hhu.chatgpt.config.SecurityConfig;
import org.hhu.chatgpt.helper.WithMockOAuth2User;
import org.hhu.chatgpt.domain.service.ThemaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest //lädt die Controller und macht die Nutzung von MockMVC möglich, SecurityConfig wird nicht geladen
@Import({SecurityConfig.class, MethodSecurityConfig.class}) // so wird SecurityConfig angewendet
public class WebTest {

    @Autowired // Field Injection, (keine Konstruktor Injection) weil Test
    MockMvc mvc; // Schnittstelle zur Anwendung

    @MockBean // zum mocken von Controller Abhängigkeiten
    ThemaService themaService;

    @MockBean
    RoleBasedSuccessHandler successHandler;

    @Test
    @DisplayName("Startseite ist unter / erreichbar")
    void test_1() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())// überprüft erreichbarkeit der Seite /
                .andExpect(view().name("home"));
    }

    @Test
    @DisplayName("/admin ist für die Rolle Admin erreichbar")
    @WithMockOAuth2User(login = "TestUser", roles={"ADMIN"})
    void test_2() throws Exception {
        mvc.perform(get("/admin"))
                .andExpect(status().isOk())// überprüft erreichbarkeit der Seite /
                .andExpect(view().name("admin"));
    }

    @Test
    @DisplayName("/admin ist nicht erreichbar für Rolle != Admin ")
    @WithMockOAuth2User(login = "something", roles = "USER")
    void AdminWithoutRole() throws Exception {
        mvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("themen ist unter /themen erreichbar")
    @WithMockOAuth2User(login = "MohamedChallou")
    void test_3() throws Exception {
        mvc.perform(get("/themen"))
                .andExpect(status().isOk())
                .andExpect(view().name("themen"));
    }

    @Test
    @DisplayName("betreuende ist unter /betreuende erreichbar")
    @WithMockOAuth2User(login = "MohamedChallou")
    void test_4() throws Exception {
        mvc.perform(get("/betreuende"))
                .andExpect(status().isOk())
                .andExpect(view().name("betreuende"));
    }

    @Test
    @DisplayName("betreuer ist unter /betreuer erreichbar, wenn Role = Betreuer")
    @WithMockOAuth2User(login = "MohamedChallou", roles = {"BETREUER"})
    void test_5() throws Exception {
        mvc.perform(get("/betreuer"))
                .andExpect(status().isOk())
                .andExpect(view().name("betreuer"));
    }

    @Test
    @DisplayName("betreuer ist unter /betreuer erreichbar, wenn Role = Admin")
    @WithMockOAuth2User(login = "MohamedChallou", roles = {"ADMIN"})
    void test_6() throws Exception {
        mvc.perform(get("/betreuer"))
                .andExpect(status().isOk())
                .andExpect(view().name("betreuer"));
    }

    @Test
    @DisplayName("/betreuer ist nicht erreichbar für Role != Betreuer and != Admin")
    @WithMockOAuth2User(login = "MohamedChallou")
    void BetreuerWithoutRole() throws Exception {
        mvc.perform(get("/betreuer"))
                .andExpect(status().isForbidden());
    }

}
