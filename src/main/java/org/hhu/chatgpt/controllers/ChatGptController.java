package org.hhu.chatgpt.controllers;

import java.util.List;

import org.hhu.chatgpt.config.annotations.AdminOnly;
import org.hhu.chatgpt.config.annotations.AdminOrBetreuerOnly;
import org.hhu.chatgpt.config.annotations.BetreuerOnly;
import org.hhu.chatgpt.domain.model.Thema;
import org.hhu.chatgpt.domain.model.betreuer.Betreuer;
import org.hhu.chatgpt.domain.service.ThemaService;
import org.hhu.chatgpt.domain.service.BetreuerService;
import org.hhu.chatgpt.dummydata.BetreuerDummyData;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class ChatGptController {

    private final ThemaService themaService;
    private final BetreuerService betreuerService;

    public ChatGptController(ThemaService themaService, BetreuerService betreuerService) {
        this.themaService = themaService;
        this.betreuerService = betreuerService;
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
    public String admin(Model model){
        model.addAttribute("betreuerListe", betreuerService.list());
        return "admin";
    }

    @PostMapping("/admin/betreuer")
    @AdminOnly
    public String createBetreuer(@RequestParam("name") String name,
                                 @RequestParam("nachname") String nachname,
                                 @RequestParam("email") String email,
                                 @RequestParam(value = "phone", required = false) String phone,
                                 @RequestParam(value = "tags", required = false) String tags,
                                 RedirectAttributes ra) {
        java.util.List<String> tagList = (tags == null || tags.isBlank())
                ? java.util.List.of()
                : java.util.Arrays.stream(tags.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .toList();

        Betreuer b = new Betreuer(name, nachname, email, phone, tagList, java.util.List.of());
        boolean created = betreuerService.create(b);
        ra.addFlashAttribute("message", created ? "Betreuer erstellt." : "E-Mail bereits vorhanden.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/betreuer/delete")
    @AdminOnly
    public String deleteBetreuer(@RequestParam("email") String email, RedirectAttributes ra) {
        boolean deleted = betreuerService.deleteByEmail(email);
        ra.addFlashAttribute("message", deleted ? "Betreuer gelöscht." : "Betreuer nicht gefunden.");
        return "redirect:/admin";
    }

    @GetMapping("/betreuende")
    public String betreuende() {
        return "betreuende";
    }

    @GetMapping("/matching")
    public String matching() {
        return "matching";
    }


    @GetMapping("/betreuer")
    @AdminOrBetreuerOnly
    public String betreuer(Model model){
        model.addAttribute("betreuer", BetreuerDummyData.getBetreuerListe());
        model.addAttribute("allTags", BetreuerDummyData.getBetreuerListe());
        return "betreuer/betreuer";
    }


    @GetMapping("/profil")
    @BetreuerOnly
    public String profil(Model model) {
            Betreuer betreuer = new Betreuer(
                    "Max",
                    "Musterman",
                    "max.mustermann@uni.de",
                    "0211 123456",
                    List.of("Spring", "Formale Methoden", "Funktionale Programmierung"),
                    List.of()
            );
        model.addAttribute("betreuer", betreuer);
        model.addAttribute("allTags",
                List.of("Compilerbau", "Formale Methoden", "Spring", "KI"));
            return "betreuer/profil";
    }

    @GetMapping("/edit")
    @AdminOrBetreuerOnly
    public String edit(Model model){
        Betreuer betreuer = new Betreuer(
                "Max",
                "Musterman",
                "max.mustermann@uni.de",
                "0211 123456",
                List.of("Spring", "Formale Methoden", "Funktionale Programmierung"),
                List.of()

        );

        model.addAttribute("betreuer", betreuer);
        model.addAttribute("allTags",
                List.of("Compilerbau", "Formale Methoden", "Spring", "KI"));
        return "betreuer/edit";
    }


}
