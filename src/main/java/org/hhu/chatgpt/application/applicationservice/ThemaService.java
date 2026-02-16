package org.hhu.chatgpt.application.applicationservice;

import org.hhu.chatgpt.domain.model.ThemaListItem;
import org.hhu.chatgpt.application.applicationservice.repository.BetreuerRepository;
import org.hhu.chatgpt.domain.model.Betreuer;
import org.hhu.chatgpt.domain.model.Thema;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;

@Service
public class ThemaService {

    private final BetreuerRepository betreuerRepository;

    public ThemaService(BetreuerRepository betreuerRepository) {
        this.betreuerRepository = betreuerRepository;
    }

    public List<ThemaListItem> getGefilterteThemen(String search, String tag, String requirement) {

        return betreuerRepository.findAll().stream()
                .flatMap(b -> {
                    Set<Thema> themen = b.getThemenSet() == null ? Set.of() : b.getThemenSet();
                    String name = buildName(b);
                    return themen.stream().map(t -> toItem(t, name));
                })

                // Title search
                .filter(x ->
                        search == null || search.isBlank()
                                || x.title().toLowerCase().contains(search.toLowerCase())
                )

                // Tag filter
                .filter(x ->
                        tag == null || tag.isBlank()
                                || (x.tags() != null && x.tags().contains(tag))
                )

                // Requirement filter
                .filter(x ->
                        requirement == null || requirement.isBlank()
                                || (x.requirements() != null && x.requirements().contains(requirement))
                )

                .toList();
    }

    private ThemaListItem toItem(Thema t, String betreuerName) {
        Set<String> tags = t.getTags() == null ? Set.of() : t.getTags();
        Set<String> reqs = t.getRequirements() == null ? Set.of() : t.getRequirements();

        return new ThemaListItem(
                t.getTitle(),
                t.getDescription(),
                tags,
                reqs,
                betreuerName
        );
    }

    private String buildName(Betreuer b) {
        String first = b.getFirstName() == null ? "" : b.getFirstName().trim();
        String last = b.getLastName() == null ? "" : b.getLastName().trim();
        String full = (first + " " + last).trim();
        return full.isBlank() ? "Unbekannt" : full;
    }
}
