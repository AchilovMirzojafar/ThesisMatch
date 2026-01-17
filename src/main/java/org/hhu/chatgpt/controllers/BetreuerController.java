package org.hhu.chatgpt.controllers;

import jakarta.validation.Valid;
import org.hhu.chatgpt.domain.model.betreuer.Betreuer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.hhu.chatgpt.application.applicationservice.BetreuerWriteService;
import org.hhu.chatgpt.application.applicationservice.BetreuerReadService;


import java.security.Principal;

@Controller
public class BetreuerController {

    private final BetreuerWriteService betreuerWriteService;
    private final BetreuerReadService betreuerReadService;


    public BetreuerController(BetreuerWriteService betreuerWriteService, BetreuerReadService betreuerReadService) {
        this.betreuerWriteService = betreuerWriteService;
        this.betreuerReadService = betreuerReadService;
    }

    @GetMapping("/profil")
    public String profil(Model model,
                         Principal principal) {
        String login = principal.getName();
        Betreuer betreuer = betreuerReadService.findProfile(login);

        if (betreuer == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Profil ist als Betreuer nicht gefunden. Bitte wenden Sie sich an den Administrator.");
        }
        model.addAttribute("betreuer", betreuer);
        model.addAttribute("allTags", betreuer.getTags());
        return "betreuer/profil";
    }

    @GetMapping("/edit")
    public String edit(Model model, Principal principal) {
        String login = principal.getName();
        Betreuer betreuer = betreuerReadService.findProfile(login);
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
                                Principal principal) {
        String login = principal.getName();
        Betreuer old = betreuerReadService.findProfile(login);
        if (old == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Profil ist als Betreuer nicht gefunden. Bitte wenden Sie sich an den Administrator.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("allTags", old.getTags());
            return "betreuer/edit";
        }

        betreuer.setFiles(old.getFiles());

        betreuerWriteService.updateProfile(betreuer);
        return "redirect:/profil";
    }
}
