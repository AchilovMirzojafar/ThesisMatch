package org.hhu.thesismatch.application.applicationservice;

import org.hhu.thesismatch.domain.model.GitHubId;

public class BetreuerNichtGefundenException extends RuntimeException {

    public BetreuerNichtGefundenException(GitHubId fachId) {
        super("Kein Betreuer mit GitHub-ID " + fachId + " gefunden");
    }
}
