package org.hhu.chatgpt.controllers;

import org.hhu.chatgpt.config.annotations.AdminOnly;
import org.hhu.chatgpt.config.annotations.BetreuerOnly;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ChatGptController {

    @GetMapping("/")
    public String root() {
        return "home";
    }

    @GetMapping("/home")
    public String home(OAuth2AuthenticationToken auth) {
        OAuth2User user = auth.getPrincipal();
        String login = user.getAttribute("login");
        System.out.println(user);
        System.out.println(login);
        return "home";
    }

    @GetMapping("/admin")
    @AdminOnly
    public String admin(){
        return "admin";
    }

    @GetMapping("/betreuer")
    @BetreuerOnly
    public String betreuer(){
        return "betreuer";
    }

    @GetMapping("/betreunde")
    public String betreuende() { return "betreuende"; }


}
