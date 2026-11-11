package org.hhu.thesismatch.application.applicationservice;

import java.util.List;
import java.util.Optional;

import org.hhu.thesismatch.application.applicationservice.repository.BetreuerRepository;
import org.hhu.thesismatch.domain.model.Betreuer;
import org.hhu.thesismatch.domain.model.GitHubId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BetreuerReadService {

    private final BetreuerRepository repository;

    public BetreuerReadService(BetreuerRepository repository) {
        this.repository = repository;
    }

    public List<Betreuer> list() {
        return repository.findAll();
    }

    public Optional<Betreuer> findProfile(GitHubId fachId) {
        return repository.findByFachId(fachId);
    }

    /**
     * Sucht Betreuer, deren Titel und Name alle Suchbegriffe enthalten und die optional
     * das angegebene Fachgebiet haben.
     */
    public List<Betreuer> search(String name, String tag) {
        List<String> searchTokens = tokenize(name);

        return repository.findAll().stream()
                .filter(b -> matchesName(b, searchTokens))
                .filter(b -> tag == null || tag.isBlank()
                        || b.getTags().stream().anyMatch(t -> t.equalsIgnoreCase(tag)))
                .toList();
    }

    private static List<String> tokenize(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return List.of(name.toLowerCase().replace(".", "").trim().split("\\s+"));
    }

    private static boolean matchesName(Betreuer b, List<String> searchTokens) {
        if (searchTokens.isEmpty()) {
            return true;
        }
        String searchableText = String.join(" ", b.getTitel(), b.getFirstName(), b.getLastName())
                .toLowerCase()
                .replace(".", "");
        return searchTokens.stream().allMatch(searchableText::contains);
    }
}
