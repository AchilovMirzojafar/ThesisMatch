package org.hhu.chatgpt.controllers;

import java.util.List;

import org.hhu.chatgpt.application.applicationservice.BetreuerReadService;
import org.hhu.chatgpt.domain.model.Matching;
import org.hhu.chatgpt.domain.model.MatchingResult;
import org.hhu.chatgpt.domain.model.Thema;
import org.hhu.chatgpt.domain.model.betreuer.Betreuer;
import org.hhu.chatgpt.domain.service.studentService.MatchingService;
import org.hhu.chatgpt.domain.service.studentService.ThemaService;
import org.hhu.chatgpt.dummydata.MatchingDummyData;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChatGptController {

    private final ThemaService themaService;
    private final BetreuerReadService betreuerReadService;
    private final MatchingService matchingService;

    public ChatGptController(ThemaService themaService, BetreuerReadService betreuerReadService,
            MatchingService matchingService) {
        this.themaService = themaService;
        this.betreuerReadService = betreuerReadService;
        this.matchingService = matchingService;

    }

    @GetMapping("/")
    public String root() {
        return "student/home";
    }

    @GetMapping("/home")
    public String home(OAuth2AuthenticationToken auth) {
        OAuth2User user = auth.getPrincipal();
        String login = user.getAttribute("login");
        System.out.println(user);
        System.out.println(login);
        return "student/home";
    }

    @GetMapping("/themen")
    public String themen(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "tag", required = false) String tag,
            @RequestParam(name = "requirement", required = false) String requirement,
            Model model) {

        List<Thema> filtered = themaService.getGefilterteThemen(search, tag, requirement);

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

        List<Betreuer> searched = betreuerReadService.getGesuchteName(searchname, tag);

        model.addAttribute("betreuendeListe", searched);
        model.addAttribute("searchname", searchname);
        model.addAttribute("tag", tag);

        return "student/betreuende";
    }

    @GetMapping("/matching")
    public String matching(
            @RequestParam(name = "kurse", required = false) List<String> kurse,
            @RequestParam(name = "interessen", required = false) List<String> interessen,
            Model model) {

        Matching matching = MatchingDummyData.getDummyMatching();
        model.addAttribute("matching", matching);

        if ((kurse != null && !kurse.isEmpty()) || (interessen != null && !interessen.isEmpty())) {
            List<MatchingResult> results = matchingService.getMatchingResults(kurse, interessen);
            System.out.println("results size = " + results.size());
            model.addAttribute("results", results);
        }

        return "student/matching";
    }

}
