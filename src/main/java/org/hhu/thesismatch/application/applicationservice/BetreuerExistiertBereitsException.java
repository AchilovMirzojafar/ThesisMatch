package org.hhu.thesismatch.application.applicationservice;

import org.hhu.thesismatch.domain.model.GitHubId;

public class BetreuerExistiertBereitsException extends RuntimeException {

    public BetreuerExistiertBereitsException(GitHubId fachId) {
        super("Betreuer mit GitHub-ID " + fachId + " existiert bereits");
    }
}
