package org.hhu.thesismatch.application.applicationservice;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hhu.thesismatch.application.applicationservice.repository.BetreuerRepository;
import org.hhu.thesismatch.domain.model.Betreuer;
import org.hhu.thesismatch.domain.model.GitHubId;
import org.hhu.thesismatch.domain.model.Thema;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ThemaService {

    private final BetreuerRepository betreuerRepository;

    public ThemaService(BetreuerRepository betreuerRepository) {
        this.betreuerRepository = betreuerRepository;
    }

    @Transactional(readOnly = true)
    public List<ThemaListItem> getGefilterteThemen(String search, String tag, String requirement) {
        return betreuerRepository.findAll().stream()
                .flatMap(b -> b.getThemen().stream().map(t -> toItem(t, b)))
                .filter(x -> search == null || search.isBlank()
                        || x.title().toLowerCase().contains(search.toLowerCase()))
                .filter(x -> tag == null || tag.isBlank() || x.tags().contains(tag))
                .filter(x -> requirement == null || requirement.isBlank()
                        || x.requirements().contains(requirement))
                .toList();
    }

    /**
     * Legt ein neues Thema für den angegebenen Betreuer an.
     *
     * @throws BetreuerNichtGefundenException wenn es keinen Betreuer mit dieser ID gibt
     */
    @Transactional
    public Thema createThema(GitHubId fachId, String title, String description,
                             Set<String> tags, Set<String> requirements) {
        Betreuer betreuer = load(fachId);
        Thema thema = Thema.neu(title, description, tags, requirements);
        betreuer.addThema(thema);
        betreuerRepository.save(betreuer);
        return thema;
    }

    /**
     * Löscht ein Thema. Ein Betreuer kann nur seine eigenen Themen löschen.
     *
     * @return true, wenn das Thema gelöscht wurde
     */
    @Transactional
    public boolean deleteThema(GitHubId fachId, UUID themaId) {
        Betreuer betreuer = load(fachId);
        boolean removed = betreuer.removeThema(themaId);
        if (removed) {
            betreuerRepository.save(betreuer);
        }
        return removed;
    }

    private Betreuer load(GitHubId fachId) {
        return betreuerRepository.findByFachId(fachId)
                .orElseThrow(() -> new BetreuerNichtGefundenException(fachId));
    }

    private static ThemaListItem toItem(Thema t, Betreuer b) {
        return new ThemaListItem(t.getTitle(), t.getDescription(), t.getTags(), t.getRequirements(),
                buildName(b));
    }

    private static String buildName(Betreuer b) {
        String full = (nullToEmpty(b.getFirstName()) + " " + nullToEmpty(b.getLastName())).trim();
        return full.isBlank() ? "Unbekannt" : full;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s.trim();
    }
}
