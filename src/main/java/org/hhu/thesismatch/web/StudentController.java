package org.hhu.thesismatch.web;

import java.util.List;

import org.hhu.thesismatch.application.applicationservice.BetreuerReadService;
import org.hhu.thesismatch.application.applicationservice.MatchingService;
import org.hhu.thesismatch.application.applicationservice.ThemaListItem;
import org.hhu.thesismatch.application.applicationservice.ThemaService;
import org.hhu.thesismatch.domain.model.Betreuer;
import org.hhu.thesismatch.domain.model.GitHubId;
import org.hhu.thesismatch.domain.model.MatchingResult;
import org.hhu.thesismatch.domain.model.TagCatalog;
import org.hhu.thesismatch.domain.model.VeranstaltungCatalog;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class StudentController {

    private final ThemaService themaService;
    private final BetreuerReadService betreuerReadService;
    private final MatchingService matchingService;

    public StudentController(ThemaService themaService, BetreuerReadService betreuerReadService,
                             MatchingService matchingService) {
        this.themaService = themaService;
        this.betreuerReadService = betreuerReadService;
        this.matchingService = matchingService;
    }

    @GetMapping({"/", "/home"})
    public String home() {
        return "student/home";
    }

    @GetMapping("/themen")
    public String themen(@RequestParam(name = "search", required = false) String search,
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
    public String betreuende(@RequestParam(name = "searchname", required = false) String searchname,
                             @RequestParam(name = "tag", required = false) String tag,
                             Model model) {
        model.addAttribute("betreuendeListe", betreuerReadService.search(searchname, tag));
        model.addAttribute("searchname", searchname);
        model.addAttribute("tag", tag);
        model.addAttribute("allTags", TagCatalog.ALL);
        return "student/betreuende";
    }

    @GetMapping("/betreuende/{fachId}")
    public String publicProfil(@PathVariable Integer fachId, Model model) {
        Betreuer betreuer = betreuerReadService.findProfile(GitHubId.of(fachId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Betreuer nicht gefunden"));

        model.addAttribute("betreuer", betreuer);
        model.addAttribute("themaListe", betreuer.getThemen());
        return "student/view/betreuerProfilView";
    }

    @GetMapping("/matching")
    public String matching(@RequestParam(name = "kurse", required = false) List<String> kurse,
                           @RequestParam(name = "interessen", required = false) List<String> interessen,
                           Model model) {
        model.addAttribute("matching", new MatchingAuswahl(
                List.copyOf(VeranstaltungCatalog.ALL),
                List.copyOf(TagCatalog.ALL)));

        List<MatchingResult> results = matchingService.getMatchingResults(kurse, interessen);
        if (!results.isEmpty()) {
            model.addAttribute("results", results);
        }
        return "student/matching";
    }
}
