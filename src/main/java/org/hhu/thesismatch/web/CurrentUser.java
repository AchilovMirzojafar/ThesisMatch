package org.hhu.thesismatch.web;

import org.hhu.thesismatch.domain.model.GitHubId;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Hilfsmethoden für den eingeloggten GitHub-Nutzer.
 */
final class CurrentUser {

    private CurrentUser() {
    }

    static GitHubId gitHubId(OAuth2User user) {
        Integer id = user.getAttribute("id");
        return GitHubId.of(id);
    }
}
