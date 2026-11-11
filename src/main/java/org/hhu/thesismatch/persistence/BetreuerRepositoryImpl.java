package org.hhu.thesismatch.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.hhu.thesismatch.application.applicationservice.BetreuerExistiertBereitsException;
import org.hhu.thesismatch.application.applicationservice.KonkurrierendeAenderungException;
import org.hhu.thesismatch.application.applicationservice.repository.BetreuerRepository;
import org.hhu.thesismatch.domain.model.Betreuer;
import org.hhu.thesismatch.domain.model.Email;
import org.hhu.thesismatch.domain.model.GitHubId;
import org.hhu.thesismatch.domain.model.Thema;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

/**
 * Adapter zwischen dem Domänen-Repository und Spring Data JDBC.
 * Übersetzt das Aggregat {@link Betreuer} in Persistenz-DTOs und zurück.
 */
@Repository
public class BetreuerRepositoryImpl implements BetreuerRepository {

    private final BetreuerDaoRepository dao;

    public BetreuerRepositoryImpl(BetreuerDaoRepository dao) {
        this.dao = dao;
    }

    @Override
    public void save(Betreuer betreuer) {
        Integer existingId = dao.findByFachId(betreuer.getFachId().value())
                .map(BetreuerDTO::getId)
                .orElse(null);

        try {
            dao.save(toDto(existingId, betreuer));
        } catch (OptimisticLockingFailureException e) {
            throw new KonkurrierendeAenderungException();
        } catch (DataAccessException e) {
            if (hasCause(e, DuplicateKeyException.class)) {
                throw new BetreuerExistiertBereitsException(betreuer.getFachId());
            }
            throw e;
        }
    }

    @Override
    public void delete(Betreuer betreuer) {
        dao.findByFachId(betreuer.getFachId().value()).ifPresent(dao::delete);
    }

    @Override
    public List<Betreuer> findAll() {
        return dao.findAll().stream()
                .map(BetreuerRepositoryImpl::toDomain)
                .toList();
    }

    @Override
    public Optional<Betreuer> findByFachId(GitHubId fachId) {
        return dao.findByFachId(fachId.value())
                .map(BetreuerRepositoryImpl::toDomain);
    }

    private static boolean hasCause(Throwable e, Class<? extends Throwable> type) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (type.isInstance(t)) {
                return true;
            }
        }
        return false;
    }

    // ---------------- Domain -> Persistence DTO ----------------

    private static BetreuerDTO toDto(Integer existingId, Betreuer b) {
        BetreuerDTO dto = new BetreuerDTO();
        dto.setId(existingId);
        dto.setVersion(b.getVersion());
        dto.setFachId(b.getFachId().value());
        dto.setTitel(b.getTitel());
        dto.setFirstName(b.getFirstName());
        dto.setLastName(b.getLastName());
        dto.setEmail(b.getEmail().value());
        dto.setPhone(b.getPhone());
        dto.setTags(b.getTags().stream().map(BetreuerTagDTO::new).collect(Collectors.toSet()));
        dto.setThemenSet(b.getThemen().stream().map(BetreuerRepositoryImpl::toDto).collect(Collectors.toSet()));
        return dto;
    }

    private static ThemaDTO toDto(Thema t) {
        ThemaDTO dto = new ThemaDTO();
        dto.setId(t.getId());
        dto.setTitle(t.getTitle());
        dto.setDescription(t.getDescription());
        dto.setTags(t.getTags().stream().map(ThemaTagDTO::new).collect(Collectors.toSet()));
        dto.setRequirements(t.getRequirements().stream()
                .map(ThemaRequirementDTO::new)
                .collect(Collectors.toSet()));
        return dto;
    }

    // ---------------- Persistence DTO -> Domain ----------------

    private static Betreuer toDomain(BetreuerDTO dto) {
        Set<String> tags = safe(dto.getTags()).stream()
                .map(BetreuerTagDTO::tag)
                .collect(Collectors.toSet());

        List<Thema> themen = safe(dto.getThemenSet()).stream()
                .map(BetreuerRepositoryImpl::toDomain)
                .toList();

        return Betreuer.rekonstruiere(
                GitHubId.of(dto.getFachId()),
                dto.getVersion(),
                dto.getTitel(),
                dto.getFirstName(),
                dto.getLastName(),
                new Email(dto.getEmail()),
                dto.getPhone(),
                tags,
                themen);
    }

    private static Thema toDomain(ThemaDTO dto) {
        Set<String> tags = safe(dto.getTags()).stream()
                .map(ThemaTagDTO::tag)
                .collect(Collectors.toSet());

        Set<String> requirements = safe(dto.getRequirements()).stream()
                .map(ThemaRequirementDTO::requirement)
                .collect(Collectors.toSet());

        return Thema.rekonstruiere(dto.getId(), dto.getTitle(), dto.getDescription(), tags, requirements);
    }

    private static <T> Set<T> safe(Set<T> set) {
        return set == null ? Set.of() : set;
    }
}
