package org.hhu.chatgpt.controllers;

import jakarta.validation.Valid;
import org.hhu.chatgpt.domain.model.betreuer.Betreuer;
import org.hhu.chatgpt.domain.service.betreuerService.BetreuerService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.security.Principal;
import java.util.List;

@Controller
public class BetreuerController {

    private final BetreuerService betreuerService;


    public BetreuerController( BetreuerService betreuerService) {
        this.betreuerService = betreuerService;

    }

    @GetMapping("/profil")
    public String profil(Model model,
                         Principal principal) {
        String login = principal.getName();
        Betreuer betreuer = betreuerService.findOwnProfile(login);

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
        Betreuer betreuer = betreuerService.findOwnProfile(login);
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
        Betreuer old = betreuerService.findOwnProfile(login);
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

        betreuerService.updateProfile(betreuer);
        return "redirect:/profil";
    }
}
