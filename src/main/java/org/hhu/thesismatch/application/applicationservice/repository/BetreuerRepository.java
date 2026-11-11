package org.hhu.thesismatch.application.applicationservice.repository;

import java.util.List;
import java.util.Optional;

import org.hhu.thesismatch.domain.model.Betreuer;
import org.hhu.thesismatch.domain.model.GitHubId;

/**
 * Repository für das Aggregat {@link Betreuer}. Es wird immer das ganze Aggregat
 * (inklusive Themen) geladen und gespeichert.
 */
public interface BetreuerRepository {

    /**
     * @throws org.hhu.thesismatch.application.applicationservice.BetreuerExistiertBereitsException
     *         wenn ein neuer Betreuer mit bereits vergebener GitHub-ID gespeichert wird
     * @throws org.hhu.thesismatch.application.applicationservice.KonkurrierendeAenderungException
     *         wenn der Betreuer seit dem Laden von jemand anderem geändert wurde
     */
    void save(Betreuer betreuer);

    void delete(Betreuer betreuer);

    List<Betreuer> findAll();

    Optional<Betreuer> findByFachId(GitHubId fachId);
}
