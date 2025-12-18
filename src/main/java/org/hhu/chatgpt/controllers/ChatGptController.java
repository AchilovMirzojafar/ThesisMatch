package org.hhu.chatgpt.controllers;

import java.util.List;

import org.hhu.chatgpt.config.annotations.AdminOnly;
import org.hhu.chatgpt.config.annotations.BetreuerOnly;
import org.hhu.chatgpt.model.Thema;
import org.hhu.chatgpt.service.ThemaService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ChatGptController {

    private final ThemaService themaService;

    public ChatGptController(ThemaService themaService) {
        this.themaService = themaService;
    }


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

    @GetMapping("/themen")
    public String themen(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "tag", required = false) String tag,
            @RequestParam(name = "requirement", required = false) String requirement,
            Model model) {

        List<Thema> filtered =
                themaService.getGefilterteThemen(search, tag, requirement);

        model.addAttribute("themenListe", filtered);
        model.addAttribute("resultCount", filtered.size());
        model.addAttribute("search", search);
        model.addAttribute("tag", tag);
        model.addAttribute("requirement", requirement);

        return "themen";
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

    @GetMapping("/betreuende")
    public String betreuende() { 
        return "betreuende";
    }


}
