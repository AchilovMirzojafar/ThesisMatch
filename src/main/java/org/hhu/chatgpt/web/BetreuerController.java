package org.hhu.chatgpt.web;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.hhu.chatgpt.application.applicationservice.BetreuerReadService;
import org.hhu.chatgpt.application.applicationservice.BetreuerWriteService;
import org.hhu.chatgpt.application.applicationservice.repository.BetreuerRepository;
import org.hhu.chatgpt.config.annotations.AdminOrBetreuerOnly;
import org.hhu.chatgpt.domain.model.Betreuer;
import org.hhu.chatgpt.domain.model.TagCatalog;
import org.hhu.chatgpt.domain.model.Thema;
import org.hhu.chatgpt.domain.model.VeranstaltungCatalog;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

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
        model.addAttribute("allTags", TagCatalog.ALL);
        return "betreuer/edit";
    }

    @PostMapping("/profil")
    public String updateProfile(@Valid @ModelAttribute Betreuer betreuer,
            BindingResult bindingResult,
            Model model,
            @AuthenticationPrincipal OAuth2User user) {

        Integer id = user.getAttribute("id");

        Betreuer oldBetreuer = betreuerReadService.findProfile(id)
                .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Profil ist als Betreuer nicht gefunden. Bitte wenden Sie sich an den Administrator."
        ));

        if (bindingResult.hasErrors()) {
            model.addAttribute("allTags", TagCatalog.ALL);
            return "betreuer/edit";
        }

        // lock fields that must not be changed via form
        betreuer.setFachId(oldBetreuer.getFachId());
        betreuer.setThemenSet(oldBetreuer.getThemenSet());

        betreuerWriteService.updateProfile(betreuer);
        return "redirect:/profil";
    }

    @GetMapping("/newThema")
    public String newThema(Model model) {
        model.addAttribute("thema", new Thema());
        model.addAttribute("allTags", TagCatalog.ALL);
        model.addAttribute("allRequirements", VeranstaltungCatalog.ALL);
        return "thema/newThema";
    }

    @PostMapping("/newThema")
    public String createNewThema(
            @Valid @ModelAttribute("thema") Thema thema,
            BindingResult bindingResult,
            @RequestParam(name = "tags", required = false) List<String> tags,
            @AuthenticationPrincipal OAuth2User user,
            Model model
    ) {
        Integer id = user.getAttribute("id");

        Betreuer betreuer = betreuerReadService.findProfile(id)
                .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Profil ist als Betreuer nicht gefunden. Bitte wenden Sie sich an den Administrator."
        ));

        // convert checkbox list -> Set
        thema.setTags(tags == null ? Set.of() : new LinkedHashSet<>(tags));

        // requirements safety
        if (thema.getRequirements() == null) {
            thema.setRequirements(Set.of());
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("allTags", TagCatalog.ALL);
            model.addAttribute("allRequirements", VeranstaltungCatalog.ALL);
            return "thema/newThema";
        }

        if (betreuer.getThemenSet() == null) {
            betreuer.setThemenSet(new HashSet<>());
        }

        betreuer.getThemenSet().add(thema);
        betreuerRepository.save(betreuer);

        return "redirect:/profil";
    }

    @PostMapping("/thema/{themaId}/delete")
    public String deleteThema(@PathVariable UUID themaId,
            @AuthenticationPrincipal OAuth2User user) {

        Integer fachId = user.getAttribute("id");

        Betreuer betreuer = betreuerReadService.findProfile(fachId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (betreuer.getThemenSet() == null) {
            return "redirect:/profil";
        }

        boolean removed = betreuer.getThemenSet().removeIf(t -> themaId.equals(t.getId()));

        if (removed) {
            betreuerRepository.save(betreuer);
        }

        return "redirect:/profil";
    }

}
