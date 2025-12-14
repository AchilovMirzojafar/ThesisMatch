package org.hhu.chatgpt.controllers;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ChatGptController {

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(OAuth2AuthenticationToken auth) {
        OAuth2User user = auth.getPrincipal();
        System.out.println(user);
        System.out.println(auth.getName());
        return "home";
    }


}
