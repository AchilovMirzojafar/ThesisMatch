package org.hhu.chatgpt.controllers;

import jakarta.validation.Valid;
import org.hhu.chatgpt.config.annotations.AdminOnly;
import org.hhu.chatgpt.domain.model.betreuer.Betreuer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.hhu.chatgpt.application.applicationservice.BetreuerWriteService;
import org.hhu.chatgpt.application.applicationservice.BetreuerReadService;

import java.util.*;

@Controller
public class AdminController {

    private final BetreuerWriteService betreuerWriteService;
    private final BetreuerReadService betreuerReadService;



    //Konstruktor für Controller
    public AdminController(BetreuerWriteService betreuerWriteService, BetreuerReadService betreuerReadService) {
        this.betreuerWriteService = betreuerWriteService;
        this.betreuerReadService = betreuerReadService;
    }

    @GetMapping("/admin")
    @AdminOnly
    public String admin(Model model) {
        model.addAttribute("betreuer", new Betreuer());
        model.addAttribute("betreuerListe", betreuerReadService.list());
        return "admin/admin";
    }

    @PostMapping("/admin")
    @AdminOnly
    public String createBetreuer(
            @Valid @ModelAttribute("betreuer") Betreuer betreuer,
            BindingResult bindingResult,
            @RequestParam(value = "tags", required = false) String tags,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("betreuerListe", betreuerReadService.list());
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

        boolean created = betreuerWriteService.create(b);

        if (!created) {
            bindingResult.rejectValue(
                    "gitHubName",
                    "duplicate",
                    "GitHub-Login existiert bereits"
            );
            model.addAttribute("betreuerListe", betreuerReadService.list());
            return "admin/admin";
        }

        //   ra.addFlashAttribute("message", created ? "Betreuer erstellt." : "Betreuer  bereits vorhanden.");
        return "redirect:/admin";
    }

    @GetMapping("/admin/edit")
    @AdminOnly
    public String updateBetreuer(@RequestParam("login") String login, Model model) {
        Betreuer betreuer = betreuerReadService.findProfile(login);
        if (betreuer == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Profil ist als Betreuer nicht gefunden. Bitte wenden Sie sich an den Administrator.");
        }
        model.addAttribute("betreuer", betreuer);  //
        model.addAttribute("allTags", betreuer.getTags());
        return "admin/edit";
    }

    @PostMapping("/admin/edit")
    @AdminOnly
    public String saveUpdates(
            @ModelAttribute Betreuer betreuer,
            @RequestParam(required = false) List<String> existingTags,
            @RequestParam(required = false) String newTags
    ) {
        Set<String> result = new LinkedHashSet<>();

        // 1. bestehende Checkbox-Tags
        if (existingTags != null) {
            result.addAll(existingTags);
        }

        // 2. neue Tags aus Textfeld
        if (newTags != null && !newTags.isBlank()) {
            Arrays.stream(newTags.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(result::add);
        }

        betreuer.setTags(new ArrayList<>(result));

        betreuerWriteService.updateProfile(betreuer);
        return "redirect:/admin";
    }

    @PostMapping("/admin/delete")
    @AdminOnly
    public String deleteBetreuer(@RequestParam("login") String login, RedirectAttributes ra) {
        boolean deleted = betreuerWriteService.deleteByLogin(login);
        ra.addFlashAttribute("message", deleted ? "Betreuer gelöscht." : "Betreuer nicht gefunden.");
        return "redirect:/admin";
    }
}