package org.hhu.thesismatch.application.applicationservice;

import java.util.Objects;

import org.hhu.thesismatch.application.applicationservice.repository.BetreuerRepository;
import org.hhu.thesismatch.domain.model.Betreuer;
import org.hhu.thesismatch.domain.model.Email;
import org.hhu.thesismatch.domain.model.GitHubId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BetreuerWriteService {

    private final BetreuerRepository repository;

    public BetreuerWriteService(BetreuerRepository repository) {
        this.repository = repository;
    }

    /**
     * @throws BetreuerExistiertBereitsException wenn die GitHub-ID schon vergeben ist
     */
    public void create(GitHubId fachId, String titel, String firstName, String lastName, Email email) {
        if (repository.findByFachId(fachId).isPresent()) {
            throw new BetreuerExistiertBereitsException(fachId);
        }
        repository.save(Betreuer.neu(fachId, titel, firstName, lastName, email));
    }

    /**
     * Ändert das Profil eines Betreuers. Themen bleiben unverändert.
     *
     * @param erwarteteVersion Version, die der Nutzer beim Öffnen des Formulars gesehen hat.
     *                         Weicht sie ab, wurde das Profil zwischenzeitlich geändert.
     * @throws BetreuerNichtGefundenException    wenn es keinen Betreuer mit dieser ID gibt
     * @throws KonkurrierendeAenderungException  wenn das Profil zwischenzeitlich geändert wurde
     */
    public void updateProfile(GitHubId fachId, Integer erwarteteVersion, ProfilDaten daten) {
        Betreuer betreuer = repository.findByFachId(fachId)
                .orElseThrow(() -> new BetreuerNichtGefundenException(fachId));

        if (erwarteteVersion != null && !Objects.equals(erwarteteVersion, betreuer.getVersion())) {
            throw new KonkurrierendeAenderungException();
        }

        betreuer.updateProfil(daten.titel(), daten.firstName(), daten.lastName(),
                daten.email(), daten.phone(), daten.tags());
        repository.save(betreuer);
    }

    /**
     * @return true, wenn ein Betreuer gelöscht wurde
     */
    public boolean delete(GitHubId fachId) {
        return repository.findByFachId(fachId)
                .map(betreuer -> {
                    repository.delete(betreuer);
                    return true;
                })
                .orElse(false);
    }
}
