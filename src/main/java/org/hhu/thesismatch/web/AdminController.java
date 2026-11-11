package org.hhu.thesismatch.web;

import org.hhu.thesismatch.application.applicationservice.BetreuerExistiertBereitsException;
import org.hhu.thesismatch.application.applicationservice.BetreuerReadService;
import org.hhu.thesismatch.application.applicationservice.BetreuerWriteService;
import org.hhu.thesismatch.web.security.AdminOnly;
import org.hhu.thesismatch.domain.model.Email;
import org.hhu.thesismatch.domain.model.GitHubId;
import org.hhu.thesismatch.web.form.BetreuerAnlegenForm;
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
@AdminOnly
public class AdminController {

    private static final String VIEW = "admin/admin";

    private final BetreuerWriteService betreuerWriteService;
    private final BetreuerReadService betreuerReadService;

    public AdminController(BetreuerWriteService betreuerWriteService, BetreuerReadService betreuerReadService) {
        this.betreuerWriteService = betreuerWriteService;
        this.betreuerReadService = betreuerReadService;
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("betreuer", new BetreuerAnlegenForm());
        model.addAttribute("betreuerListe", betreuerReadService.list());
        return VIEW;
    }

    @PostMapping("/admin")
    public String createBetreuer(@Valid @ModelAttribute("betreuer") BetreuerAnlegenForm form,
                                 BindingResult bindingResult,
                                 Model model) {
        if (!bindingResult.hasErrors()) {
            try {
                betreuerWriteService.create(GitHubId.of(form.getFachId()), form.getTitel(),
                        form.getFirstName(), form.getLastName(), new Email(form.getEmail()));
                return "redirect:/admin";
            } catch (BetreuerExistiertBereitsException e) {
                bindingResult.rejectValue("fachId", "duplicate", "GitHub-Login existiert bereits");
            }
        }

        model.addAttribute("betreuerListe", betreuerReadService.list());
        return VIEW;
    }

    @PostMapping("/admin/delete")
    public String deleteBetreuer(@RequestParam("id") Integer id, RedirectAttributes ra) {
        boolean deleted = betreuerWriteService.delete(GitHubId.of(id));
        ra.addFlashAttribute("message", deleted ? "Betreuer gelöscht." : "Betreuer nicht gefunden.");
        return "redirect:/admin";
    }
}
