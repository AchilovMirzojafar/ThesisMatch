package org.hhu.thesismatch.domain.model;

/**
 * Value Object: numerische GitHub-ID einer Person.
 * Wird im System als fachliche Kennung eines Betreuers verwendet.
 */
public record GitHubId(int value) {

    public GitHubId {
        if (value <= 0) {
            throw new IllegalArgumentException("GitHub-ID muss positiv sein");
        }
    }

    public static GitHubId of(Integer value) {
        if (value == null) {
            throw new IllegalArgumentException("GitHub-ID darf nicht null sein");
        }
        return new GitHubId(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
