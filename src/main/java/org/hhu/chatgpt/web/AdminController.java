package org.hhu.chatgpt.web;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.hhu.chatgpt.application.applicationservice.BetreuerReadService;
import org.hhu.chatgpt.application.applicationservice.BetreuerWriteService;
import org.hhu.chatgpt.config.annotations.AdminOnly;
import org.hhu.chatgpt.domain.model.Betreuer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
public class AdminController {

    private final BetreuerWriteService betreuerWriteService;
    private final BetreuerReadService betreuerReadService;

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
            BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
    model.addAttribute("betreuer", betreuer);
    model.addAttribute("betreuerListe", betreuerReadService.list());
    return "admin/admin";
}

        Betreuer b = new Betreuer(
                betreuer.getFachId(),
                betreuer.getTitel(),
                betreuer.getFirstName(),
                betreuer.getLastName(),
                betreuer.getEmail(),
                null,
                Set.of(),
                Set.of()
        );

        boolean created = betreuerWriteService.create(b);

        if (!created) {
    bindingResult.rejectValue("fachId", "duplicate", "GitHub-Login existiert bereits");
    model.addAttribute("betreuer", betreuer);
    model.addAttribute("betreuerListe", betreuerReadService.list());
    return "admin/admin";
}
        return "redirect:/admin";
    }

    @PostMapping("/admin/delete")
    @AdminOnly
    public String deleteBetreuer(@RequestParam("id") Integer id, RedirectAttributes ra) {
        boolean deleted = betreuerWriteService.deleteById(id);
        ra.addFlashAttribute("message", deleted ? "Betreuer gelöscht." : "Betreuer nicht gefunden.");
        return "redirect:/admin";
    }
}