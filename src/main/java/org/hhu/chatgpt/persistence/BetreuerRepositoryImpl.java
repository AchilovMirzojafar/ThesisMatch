package org.hhu.chatgpt.persistence;

import org.hhu.chatgpt.application.applicationservice.repository.BetreuerRepository;
import org.hhu.chatgpt.domain.model.Betreuer;
import org.hhu.chatgpt.domain.model.Thema;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class BetreuerRepositoryImpl implements BetreuerRepository {

    private final BetreuerDaoRepository betreuerDaoRepository;

    public BetreuerRepositoryImpl(BetreuerDaoRepository betreuerDaoRepository) {
        this.betreuerDaoRepository = betreuerDaoRepository;
    }

    @Override
    public void save(Betreuer betreuer) {
        Integer existingId = betreuerDaoRepository.findByFachId(betreuer.getFachId())
                .map(BetreuerDTO::getId)
                .orElse(null);

        BetreuerDTO dto = toBetreuerDTO(existingId, betreuer);
        betreuerDaoRepository.save(dto);
    }

    @Override
    public void delete(Betreuer betreuer) {
        Optional<BetreuerDTO> existing = betreuerDaoRepository.findByFachId(betreuer.getFachId());
        if (existing.isEmpty()) {
            return;
        }
        betreuerDaoRepository.delete(existing.get());
    }

    @Override
    public List<Betreuer> findAll() {
        return betreuerDaoRepository.findAll().stream()
                .map(this::toDomainBetreuer)
                .toList();
    }

    @Override
    public Optional<Betreuer> findByFachId(Integer fachId) {
        return betreuerDaoRepository.findByFachId(fachId)
                .map(this::toDomainBetreuer);
    }

    // ---------------- Domain -> Persistence DTO ----------------

    private BetreuerDTO toBetreuerDTO(Integer existingId, Betreuer b) {
        BetreuerDTO dto = new BetreuerDTO();
        dto.setId(existingId);

        dto.setFachId(b.getFachId());
        dto.setTitel(b.getTitel());
        dto.setFirstName(b.getFirstName());
        dto.setLastName(b.getLastName());
        dto.setEmail(b.getEmail());
        dto.setPhone(b.getPhone());

        // tags: Set<String> -> Set<BetreuerTagDTO>
        Set<String> domainTags = (b.getTags() == null) ? Set.of() : b.getTags();
        dto.setTags(domainTags.stream().map(BetreuerTagDTO::new).collect(Collectors.toSet()));

        // themen: Set<Thema> -> Set<ThemaDTO>
        Set<Thema> domainThemen = (b.getThemenSet() == null) ? Set.of() : b.getThemenSet();
        dto.setThemenSet(domainThemen.stream().map(this::toThemaDTO).collect(Collectors.toSet()));

        return dto;
    }

    private ThemaDTO toThemaDTO(Thema t) {
        ThemaDTO dto = new ThemaDTO();
        dto.setId(t.getId());

        dto.setTitle(t.getTitle());
        dto.setDescription(t.getDescription());

        Set<String> tags = (t.getTags() == null) ? Set.of() : t.getTags();
        dto.setTags(tags.stream().map(ThemaTagDTO::new).collect(Collectors.toSet()));

        Set<String> reqs = (t.getRequirements() == null) ? Set.of() : t.getRequirements();
        dto.setRequirements(reqs.stream().map(ThemaRequirementDTO::new).collect(Collectors.toSet()));

        return dto;
    }

    // ---------------- Persistence DTO -> Domain ----------------

    private Betreuer toDomainBetreuer(BetreuerDTO dto) {
        Set<String> tags = safe(dto.getTags()).stream()
                .map(BetreuerTagDTO::tag)
                .collect(Collectors.toSet());

        Set<Thema> themen = safe(dto.getThemenSet()).stream()
                .map(this::toDomainThema)
                .collect(Collectors.toSet());

        return new Betreuer(
                dto.getFachId(),
                dto.getTitel(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getPhone(),
                tags,
                themen
        );
    }

    private Thema toDomainThema(ThemaDTO dto) {
        Set<String> tags = safe(dto.getTags()).stream()
                .map(ThemaTagDTO::tag)
                .collect(Collectors.toSet());

        Set<String> reqs = safe(dto.getRequirements()).stream()
                .map(ThemaRequirementDTO::requirement)
                .collect(Collectors.toSet());

        Thema t = new Thema(dto.getTitle(), dto.getDescription(), tags, reqs);

        t.setId(dto.getId()); // <-- UUID

        return t;
    }

    private static <T> Set<T> safe(Set<T> set) {
        return (set == null) ? new HashSet<>() : set;
    }
}

