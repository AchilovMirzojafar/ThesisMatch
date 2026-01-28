package org.hhu.chatgpt.controllers;

import jakarta.validation.Valid;
import org.hhu.chatgpt.config.annotations.AdminOrBetreuerOnly;
import org.hhu.chatgpt.domain.model.Thema;
import org.hhu.chatgpt.domain.model.VeranstaltungenListe;
import org.hhu.chatgpt.domain.model.betreuer.Betreuer;
import org.hhu.chatgpt.domain.repository.BetreuerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.hhu.chatgpt.application.applicationservice.BetreuerWriteService;
import org.hhu.chatgpt.application.applicationservice.BetreuerReadService;


import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@AdminOrBetreuerOnly
public class BetreuerController {

    private final BetreuerWriteService betreuerWriteService;
    private final BetreuerReadService betreuerReadService;
    private final BetreuerRepository betreuerRepository;


    public BetreuerController(BetreuerWriteService betreuerWriteService, BetreuerReadService betreuerReadService, BetreuerRepository betreuerRepository) {
        this.betreuerWriteService = betreuerWriteService;
        this.betreuerReadService = betreuerReadService;
        this.betreuerRepository = betreuerRepository;
    }
    @GetMapping("/profil")
    public String profil(Model model,
                         @AuthenticationPrincipal OAuth2User user) {
        Integer id = user.getAttribute("id");
        Betreuer betreuer = betreuerReadService.findProfile(id);

        if (betreuer == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Profil ist als Betreuer nicht gefunden. Bitte wenden Sie sich an den Administrator.");
        }
        model.addAttribute("betreuer", betreuer);
        model.addAttribute("allTags", betreuer.getTags());
        model.addAttribute("themaListe", betreuer.getThemenSet());

        return "betreuer/profil";
    }


    @GetMapping("/edit")
    public String edit(Model model, @AuthenticationPrincipal OAuth2User user) {
        Integer id = user.getAttribute("id");
        Betreuer betreuer = betreuerReadService.findProfile(id);
        if (betreuer == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Profil ist als Betreuer nicht gefunden. Bitte wenden Sie sich an den Administrator.");
        }
        model.addAttribute("betreuer", betreuer);
        model.addAttribute("allTags", betreuer.getTags());
        return "betreuer/edit";
    }

    @PostMapping("/profil")
    public String updateProfile(@Valid @ModelAttribute Betreuer betreuer,
                                BindingResult bindingResult,
                                Model model,
                                @AuthenticationPrincipal OAuth2User user) {
        Integer id = user.getAttribute("id");
        Betreuer old = betreuerReadService.findProfile(id);
        if (old == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Profil ist als Betreuer nicht gefunden. Bitte wenden Sie sich an den Administrator.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("allTags", old.getTags());
            return "betreuer/edit";
        }

        betreuer.setFachId(old.getFachId());
        betreuer.setFiles(old.getFiles());
        betreuer.setThemenSet(old.getThemenSet());

        betreuerWriteService.updateProfile(betreuer);
        return "redirect:/profil";

    }

        @GetMapping("/newThema")
        public String newThema(Model model) {
            model.addAttribute("thema", new Thema());
            model.addAttribute("allVeranstaltungen", VeranstaltungenListe.VERANSTALTUNGEN);
            return "thema/newThema";
        }

        @PostMapping("/newThema")
        public String createNewThema(@ModelAttribute("thema") Thema thema,
                @AuthenticationPrincipal OAuth2User user) {
            Integer id = user.getAttribute("id");
            if (id == null) {
                throw new IllegalStateException("GitHub id not available");
            }

            Betreuer betreuer = betreuerReadService.findProfile(id);
            if (betreuer == null) {
                throw new IllegalStateException("Betreuer not found for id: " + id);
            }

            // 1) tagInput -> tags
            thema.setTags(parseCsv(thema.getTagInput()));

            if (thema.getRequirements() == null) {
                thema.setRequirements(List.of());
            }

            betreuer.getThemenSet().add(thema);

            betreuerRepository.save(betreuer);

        //für Output
        System.out.println("=== Themen von Betreuer " + betreuer.getFachId() + " ===");
        betreuer.getThemenSet().forEach(t ->
                System.out.println("- " + t.getTitle() + "," + t.getTags() + "," + t.getDescription() + "," + t.getRequirements())
        );

            return "redirect:/profil";
        }

        private List<String> parseCsv(String input) {
            if (input == null || input.isBlank()) return List.of();
            return Arrays.stream(input.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .distinct()
                    .collect(Collectors.toList());

        }
    }

