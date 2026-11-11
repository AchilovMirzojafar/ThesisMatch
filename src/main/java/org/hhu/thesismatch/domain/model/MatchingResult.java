package org.hhu.thesismatch.domain.model;

/**
 * Ergebnis des Matchings für ein einzelnes Thema.
 */
public record MatchingResult(
        Thema thema,
        Betreuer betreuer,
        int matchedRequirements,
        int totalRequirements,
        int matchedTags,
        int totalTags,
        int score) {
}
