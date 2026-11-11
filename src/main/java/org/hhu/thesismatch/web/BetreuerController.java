package org.hhu.thesismatch.web;

import java.util.UUID;

import org.hhu.thesismatch.application.applicationservice.BetreuerReadService;
import org.hhu.thesismatch.application.applicationservice.BetreuerWriteService;
import org.hhu.thesismatch.application.applicationservice.KonkurrierendeAenderungException;
import org.hhu.thesismatch.application.applicationservice.ThemaService;
import org.hhu.thesismatch.web.security.AdminOrBetreuerOnly;
import org.hhu.thesismatch.domain.model.Betreuer;
import org.hhu.thesismatch.domain.model.GitHubId;
import org.hhu.thesismatch.domain.model.TagCatalog;
import org.hhu.thesismatch.domain.model.VeranstaltungCatalog;
import org.hhu.thesismatch.web.form.ProfilForm;
import org.hhu.thesismatch.web.form.ThemaForm;
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
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@Controller
@AdminOrBetreuerOnly
public class BetreuerController {

    private static final String PROFIL_NICHT_GEFUNDEN =
            "Profil ist als Betreuer nicht gefunden. Bitte wenden Sie sich an den Administrator.";

    private final BetreuerWriteService betreuerWriteService;
    private final BetreuerReadService betreuerReadService;
    private final ThemaService themaService;

    public BetreuerController(BetreuerWriteService betreuerWriteService,
                              BetreuerReadService betreuerReadService,
                              ThemaService themaService) {
        this.betreuerWriteService = betreuerWriteService;
        this.betreuerReadService = betreuerReadService;
        this.themaService = themaService;
    }

    @GetMapping("/profil")
    public String profil(Model model, @AuthenticationPrincipal OAuth2User user) {
        Betreuer betreuer = loadBetreuer(user);
        model.addAttribute("betreuer", betreuer);
        model.addAttribute("allTags", betreuer.getTags());
        model.addAttribute("themaListe", betreuer.getThemen());
        return "betreuer/profil";
    }

    @GetMapping("/edit")
    public String edit(Model model, @AuthenticationPrincipal OAuth2User user) {
        Betreuer betreuer = loadBetreuer(user);
        model.addAttribute("betreuer", ProfilForm.from(betreuer));
        model.addAttribute("allTags", TagCatalog.ALL);
        return "betreuer/edit";
    }

    @PostMapping("/profil")
    public String updateProfile(@Valid @ModelAttribute("betreuer") ProfilForm form,
                                BindingResult bindingResult,
                                Model model,
                                @AuthenticationPrincipal OAuth2User user) {
        GitHubId fachId = CurrentUser.gitHubId(user);
        form.setFachId(fachId.value());

        if (bindingResult.hasErrors()) {
            model.addAttribute("allTags", TagCatalog.ALL);
            return "betreuer/edit";
        }

        try {
            betreuerWriteService.updateProfile(fachId, form.getVersion(), form.toProfilDaten());
        } catch (KonkurrierendeAenderungException e) {
            bindingResult.reject("konflikt", e.getMessage());
            model.addAttribute("allTags", TagCatalog.ALL);
            return "betreuer/edit";
        }
        return "redirect:/profil";
    }

    @GetMapping("/newThema")
    public String newThema(Model model) {
        model.addAttribute("thema", new ThemaForm());
        addKataloge(model);
        return "thema/newThema";
    }

    @PostMapping("/newThema")
    public String createNewThema(@Valid @ModelAttribute("thema") ThemaForm form,
                                 BindingResult bindingResult,
                                 @AuthenticationPrincipal OAuth2User user,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            addKataloge(model);
            return "thema/newThema";
        }

        themaService.createThema(CurrentUser.gitHubId(user),
                form.getTitle(), form.getDescription(), form.getTags(), form.getRequirements());
        return "redirect:/profil";
    }

    @PostMapping("/thema/{themaId}/delete")
    public String deleteThema(@PathVariable UUID themaId, @AuthenticationPrincipal OAuth2User user) {
        themaService.deleteThema(CurrentUser.gitHubId(user), themaId);
        return "redirect:/profil";
    }

    private Betreuer loadBetreuer(OAuth2User user) {
        return betreuerReadService.findProfile(CurrentUser.gitHubId(user))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, PROFIL_NICHT_GEFUNDEN));
    }

    private static void addKataloge(Model model) {
        model.addAttribute("allTags", TagCatalog.ALL);
        model.addAttribute("allRequirements", VeranstaltungCatalog.ALL);
    }
}
