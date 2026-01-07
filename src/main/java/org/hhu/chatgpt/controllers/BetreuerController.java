package org.hhu.chatgpt.controllers;

import org.hhu.chatgpt.domain.model.betreuer.Betreuer;
import org.hhu.chatgpt.domain.service.BetreuerService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String profil(Model model, Principal principal) {
        String login = principal.getName();
        Betreuer betreuer = betreuerService.findByLogin(login);

        if (betreuer == null) {
           return "redirect:/error";}

        model.addAttribute("betreuer", betreuer);
        model.addAttribute("allTags", betreuer.getTags());
        return "betreuer/profil";
    }

    @GetMapping("/edit")
    public String edit(Model model, Principal principal) {
        String login = principal.getName();
        Betreuer betreuer = betreuerService.findByLogin(login);
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
    public String updateProfile(@RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam String email,
                                @RequestParam String phone,
                                @RequestParam(required = false) List<String> tags,
                                Principal principal) {
        String login = principal.getName();
        Betreuer old = betreuerService.findByLogin(login);
        if (old == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Profil ist als Betreuer nicht gefunden. Bitte wenden Sie sich an den Administrator.");
        }
        List<String> newTags = tags != null ? tags : List.of();

        Betreuer updated = new Betreuer(
                firstName,
                lastName,
                email,
                phone,
                old.getGitHubName(),
                newTags,
                old.getFiles()

        );
        betreuerService.save(updated);
        return "redirect:/profil";
    }
}
