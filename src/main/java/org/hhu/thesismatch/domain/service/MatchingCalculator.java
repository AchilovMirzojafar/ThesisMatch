package org.hhu.thesismatch.domain.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.hhu.thesismatch.domain.model.Betreuer;
import org.hhu.thesismatch.domain.model.MatchingResult;
import org.hhu.thesismatch.domain.model.Thema;

/**
 * Domain Service: berechnet, wie gut die Themen der Betreuer zu den bestandenen
 * Veranstaltungen und Interessen eines Studierenden passen.
 *
 * <p>Score = (erfüllte Voraussetzungen + passende Tags) / (alle Voraussetzungen + alle Tags) in Prozent.
 * Als Tags zählen die Tags des Themas und die Fachgebiete des Betreuers.
 */
public class MatchingCalculator {

    public List<MatchingResult> berechne(Collection<Betreuer> betreuende,
                                         Collection<String> kurse,
                                         Collection<String> interessen) {
        Set<String> normalizedKurse = normalize(kurse);
        Set<String> normalizedInteressen = normalize(interessen);

        if (normalizedKurse.isEmpty() && normalizedInteressen.isEmpty()) {
            return List.of();
        }

        List<MatchingResult> results = new ArrayList<>();
        for (Betreuer betreuer : betreuende) {
            for (Thema thema : betreuer.getThemen()) {
                MatchingResult result = bewerte(betreuer, thema, normalizedKurse, normalizedInteressen);
                if (result.matchedRequirements() > 0 || result.matchedTags() > 0) {
                    results.add(result);
                }
            }
        }

        results.sort(Comparator.comparingInt(MatchingResult::score).reversed());
        return results;
    }

    private static MatchingResult bewerte(Betreuer betreuer, Thema thema,
                                          Set<String> kurse, Set<String> interessen) {
        Set<String> requirements = normalize(thema.getRequirements());

        Set<String> interestTags = new LinkedHashSet<>(normalize(thema.getTags()));
        interestTags.addAll(normalize(betreuer.getTags()));

        int matchedRequirements = countMatches(requirements, kurse);
        int matchedTags = countMatches(interestTags, interessen);

        int total = requirements.size() + interestTags.size();
        int score = total == 0
                ? 0
                : (int) Math.round((matchedRequirements + matchedTags) * 100.0 / total);

        return new MatchingResult(thema, betreuer,
                matchedRequirements, requirements.size(),
                matchedTags, interestTags.size(),
                score);
    }

    private static int countMatches(Set<String> values, Set<String> selected) {
        return (int) values.stream().filter(selected::contains).count();
    }

    static Set<String> normalize(Collection<String> values) {
        if (values == null) {
            return Set.of();
        }
        return values.stream()
                .filter(Objects::nonNull)
                .map(MatchingCalculator::normalizeOne)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    static String normalizeOne(String s) {
        return s.trim()
                .toLowerCase()
                .replace("-", " ")
                .replaceAll("\\s+", " ");
    }
}
