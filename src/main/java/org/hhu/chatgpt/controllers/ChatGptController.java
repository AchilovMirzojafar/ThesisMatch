package org.hhu.chatgpt.controllers;

import java.util.Arrays;
import java.util.List;

import jakarta.validation.Valid;
import org.hhu.chatgpt.config.annotations.AdminOnly;
import org.hhu.chatgpt.config.annotations.AdminOrBetreuerOnly;
import org.hhu.chatgpt.config.annotations.BetreuerOnly;
import org.hhu.chatgpt.domain.model.Betreuende;
import org.hhu.chatgpt.domain.model.Thema;
import org.hhu.chatgpt.domain.model.betreuer.Betreuer;
import org.hhu.chatgpt.domain.service.BetreuendeService;
import org.hhu.chatgpt.domain.service.BetreuerService;
import org.hhu.chatgpt.domain.service.ThemaService;
import org.hhu.chatgpt.dummydata.BetreuerDummyData;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class ChatGptController {


    private final ThemaService themaService;
    private final BetreuerService betreuerService;
    private final BetreuendeService betreuendeService;

    public ChatGptController(ThemaService themaService, BetreuerService betreuerService, BetreuendeService betreuendeService) {
        this.themaService = themaService;
        this.betreuerService = betreuerService;
        this.betreuendeService = betreuendeService;
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

        List<Thema> filtered =
                themaService.getGefilterteThemen(search, tag, requirement);

        model.addAttribute("themenListe", filtered);
        model.addAttribute("resultCount", filtered.size());
        model.addAttribute("search", search);
        model.addAttribute("tag", tag);
        model.addAttribute("requirement", requirement);

        return "student/themen";
    }

    @GetMapping("/admin")
    @AdminOnly
    public String admin(Model model){
        model.addAttribute("betreuer", new Betreuer());
        model.addAttribute("betreuerListe", adminService.list());
        return "admin/admin";
    }

    @PostMapping("/admin")
    @AdminOnly
    public String createBetreuer(
            @Valid @ModelAttribute("betreuer") Betreuer betreuer,
            BindingResult bindingResult,
            @RequestParam(value = "tags", required = false) String tags,
            Model model,
            RedirectAttributes ra) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("betreuerListe", adminService.list());
            return "admin/admin";
        }

        List<String> tagList = (tags == null || tags.isBlank())
                ? List.of()
                : Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        Betreuer b = new Betreuer(
                betreuer.getTitel(),
                betreuer.getFirstName(),
                betreuer.getLastName(),
                betreuer.getEmail(),
                betreuer.getPhone(),
                betreuer.getGitHubName(),
                tagList,
                List.of()
        );

        boolean created = adminService.create(b);

        if (!created) {
            bindingResult.rejectValue(
                    "gitHubName",
                    "duplicate",
                    "GitHub-Login existiert bereits"
            );
            model.addAttribute("betreuerListe", adminService.list());
            return "admin/admin";
        }

     //   ra.addFlashAttribute("message", created ? "Betreuer erstellt." : "Betreuer  bereits vorhanden.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/delete")
    @AdminOnly
    public String deleteBetreuer(@RequestParam("login") String login, RedirectAttributes ra) {
        boolean deleted = adminService.deleteByLogin(login);
        ra.addFlashAttribute("message", deleted ? "Betreuer gelöscht." : "Betreuer nicht gefunden.");
        return "redirect:/admin";
    }

    @GetMapping("/betreuende")
    public String betreuende(
            @RequestParam(name = "searchname", required = false) String searchname,
            @RequestParam(name = "tag", required = false) String tag,
            Model model) {

        List<Betreuende> searched =
                betreuendeService.getGesuchteName(searchname, tag);

        model.addAttribute("betreuendeListe", searched);
        model.addAttribute("searchname", searchname);
        model.addAttribute("tag", tag);

        return "betreuende";
    }

    @GetMapping("/matching")
    public String matching() {
        return "student/matching";
    }

}
