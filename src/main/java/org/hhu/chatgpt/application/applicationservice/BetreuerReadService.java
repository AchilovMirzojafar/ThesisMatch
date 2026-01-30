package org.hhu.chatgpt.application.applicationservice;

import org.hhu.chatgpt.domain.model.Betreuer;
import org.hhu.chatgpt.application.applicationservice.repository.BetreuerRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class BetreuerReadService {

    private final BetreuerRepository repository;

    public BetreuerReadService(BetreuerRepository repository) {
        this.repository = repository;
    }

    public List<Betreuer> list() {
        return repository.findAll();
    }

    public Optional<Betreuer> findProfile(Integer id) {
        return repository.findByFachId(id);
    }

    public List<Betreuer> getGesuchteName(String name, String tag) {

        List<String> searchTokens = (name == null || name.isBlank())
                ? List.of()
                : List.of(
                name.toLowerCase()
                        .replace(".", "")
                        .trim()
                        .split("\\s+")
        );

        return repository.findAll().stream()
                .filter(b -> {
                    if (searchTokens.isEmpty()) return true;

                    String searchableText = String.join(" ",
                                    b.getTitel(),
                                    b.getFirstName(),
                                    b.getLastName()
                            )
                            .toLowerCase()
                            .replace(".", "");

                    return searchTokens.stream()
                            .allMatch(searchableText::contains);
                })
                .filter(b ->
                        tag == null || tag.isBlank()
                                || b.getTags().stream()
                                .anyMatch(t -> t.equalsIgnoreCase(tag))
                )
                .toList();
    }

}
