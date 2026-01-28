package org.hhu.chatgpt.controllers;

import java.util.List;

import org.hhu.chatgpt.application.applicationservice.BetreuerReadService;
import org.hhu.chatgpt.domain.model.Thema;
import org.hhu.chatgpt.domain.model.betreuer.Betreuer;
import org.hhu.chatgpt.domain.service.studentService.ThemaService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class ChatGptController {


    private final ThemaService themaService;
    private final BetreuerReadService betruerReadService;


    public ChatGptController(ThemaService themaService, BetreuerReadService betruerReadService  ) {
        this.themaService = themaService;
        this.betruerReadService =betruerReadService;

    }

    @GetMapping("/")
    public String root() {
        return "student/home";
    }

    @GetMapping("/home")
    public String home(OAuth2AuthenticationToken auth) {
        OAuth2User user = auth.getPrincipal();
        Integer id = user.getAttribute("id");
        System.out.println(user);
        System.out.println(id);
        return "student/home";
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

        return "student/themen";
    }

    @GetMapping("/betreuende")
    public String betreuende(
            @RequestParam(name = "searchname", required = false) String searchname,
            @RequestParam(name = "tag", required = false) String tag,
            Model model) {

        List<Betreuer> searched =
                betruerReadService.getGesuchteName(searchname, tag);

        model.addAttribute("betreuendeListe", searched);
        model.addAttribute("searchname", searchname);
        model.addAttribute("tag", tag);

        return "student/betreuende";
    }
    @GetMapping("/matching")
    public String matching() {
        return "student/matching";
    }

}
