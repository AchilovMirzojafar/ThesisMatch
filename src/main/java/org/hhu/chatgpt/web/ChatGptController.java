package org.hhu.chatgpt.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hhu.chatgpt.application.applicationservice.BetreuerReadService;
import org.hhu.chatgpt.application.applicationservice.MatchingService;
import org.hhu.chatgpt.application.applicationservice.ThemaService;
import org.hhu.chatgpt.domain.model.Betreuer;
import org.hhu.chatgpt.domain.model.Matching;
import org.hhu.chatgpt.domain.model.MatchingResult;
import org.hhu.chatgpt.domain.model.TagCatalog;
import org.hhu.chatgpt.domain.model.Thema;
import org.hhu.chatgpt.domain.model.VeranstaltungCatalog;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

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

        List<ThemaListItem> filtered = themaService.getGefilterteThemen(search, tag, requirement);

        model.addAttribute("themenListe", filtered);
        model.addAttribute("resultCount", filtered.size());
        model.addAttribute("search", search);
        model.addAttribute("tag", tag);
        model.addAttribute("allTags", TagCatalog.ALL);
        model.addAttribute("requirement", requirement);
        model.addAttribute("allRequirements", VeranstaltungCatalog.ALL);

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
        model.addAttribute("allTags", TagCatalog.ALL);

        return "student/betreuende";
    }

    @GetMapping("/betreuende/{fachId}")
    public String publicProfil(@PathVariable Integer fachId, Model model) {

        Betreuer betreuer = betreuerReadService.findProfile(fachId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Betreuer nicht gefunden"));

        model.addAttribute("betreuer", betreuer);

        Set<Thema> themen = (betreuer.getThemenSet() == null) ? Set.of() : betreuer.getThemenSet();
        model.addAttribute("themaListe", themen);

        return "student/view/betreuerProfilView";
    }

    @GetMapping("/matching")
    public String matching(
            @RequestParam(name = "kurse", required = false) List<String> kurse,
            @RequestParam(name = "interessen", required = false) List<String> interessen,
            Model model) {

        Matching matching = new Matching(
                new ArrayList<>(VeranstaltungCatalog.ALL),
                new ArrayList<>(TagCatalog.ALL)
        );
        model.addAttribute("matching", matching);

        List<String> safeKurse = (kurse == null) ? List.of() : kurse;
        List<String> safeInteressen = (interessen == null) ? List.of() : interessen;

        if (!safeKurse.isEmpty() || !safeInteressen.isEmpty()) {
        List<MatchingResult> results = matchingService.getMatchingResults(safeKurse, safeInteressen);
        model.addAttribute("results", results);
    }

        return "student/matching";
    }

}