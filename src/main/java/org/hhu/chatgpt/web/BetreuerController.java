package org.hhu.chatgpt.web;

import jakarta.validation.Valid;
import org.hhu.chatgpt.config.annotations.AdminOrBetreuerOnly;
import org.hhu.chatgpt.domain.model.Thema;
import org.hhu.chatgpt.domain.model.VeranstaltungenListe;
import org.hhu.chatgpt.domain.model.Betreuer;
import org.hhu.chatgpt.application.applicationservice.repository.BetreuerRepository;
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


import java.util.List;
import java.util.Optional;

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
        Optional<Betreuer> optionalBetreuer = Optional.ofNullable(betreuerReadService.findProfile(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profil ist als Betreuer nicht gefunden. Bitte wenden Sie sich an den Administrator."
                )));

        Betreuer betreuer = optionalBetreuer.get();
        model.addAttribute("betreuer", betreuer);
        model.addAttribute("allTags", betreuer.getTags());
        model.addAttribute("themaListe", betreuer.getThemenSet());

        return "betreuer/profil";
    }


    @GetMapping("/edit")
    public String edit(Model model, @AuthenticationPrincipal OAuth2User user) {
        Integer id = user.getAttribute("id");
        Optional<Betreuer> optionalBetreuer = Optional.ofNullable(betreuerReadService.findProfile(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profil ist als Betreuer nicht gefunden. Bitte wenden Sie sich an den Administrator."
                )));

        Betreuer betreuer = optionalBetreuer.get();
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
        Optional<Betreuer> optionalBetreuer = Optional.ofNullable(betreuerReadService.findProfile(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profil ist als Betreuer nicht gefunden. Bitte wenden Sie sich an den Administrator."
                )));

        Betreuer oldBetreuer = optionalBetreuer.get();

        if (bindingResult.hasErrors()) {
            model.addAttribute("allTags", oldBetreuer.getTags());
            return "betreuer/edit";
        }

        betreuer.setFachId(oldBetreuer.getFachId());
        //betreuer.setFiles(oldBetreuer.getFiles());
        betreuer.setThemenSet(oldBetreuer.getThemenSet());

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

            Optional<Betreuer> optionalBetreuer = Optional.ofNullable(betreuerReadService.findProfile(id)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Profil ist als Betreuer nicht gefunden. Bitte wenden Sie sich an den Administrator."
                    )));

            Betreuer betreuer = optionalBetreuer.get();

            if (thema.getRequirements() == null) {
                thema.setRequirements(List.of());
            }

            betreuer.getThemenSet().add(thema);

            betreuerRepository.save(betreuer);

            return "redirect:/profil";
        }

    }

