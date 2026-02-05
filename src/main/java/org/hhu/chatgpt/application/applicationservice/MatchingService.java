package org.hhu.chatgpt.application.applicationservice;

import org.hhu.chatgpt.domain.model.Betreuer;
import org.hhu.chatgpt.domain.model.MatchingResult;
import org.hhu.chatgpt.domain.model.Thema;
import org.hhu.chatgpt.dummydata.BetreuerDummyData;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchingService {

        public List<MatchingResult> getMatchingResults(
                List<String> selectedKurse,
                List<String> selectedInteressen) {

                Set<String> normalizedKurse = normalize(selectedKurse);
                Set<String> normalizedInteressen = normalize(selectedInteressen);

                if (normalizedKurse.isEmpty() && normalizedInteressen.isEmpty()) {
                        return List.of();
                }

                List<MatchingResult> results = new ArrayList<>();

                // Источник правды: агрегат Betreuer (каждый Betreuer владеет своими Themen)
                List<Betreuer> betreuendeListe = BetreuerDummyData.getBetreuerListe();

                for (Betreuer prof : betreuendeListe) {

                        Set<Thema> themen = prof.getThemenSet() == null ? Set.of() : prof.getThemenSet();
                        if (themen.isEmpty()) continue;

                        for (Thema thema : themen) {

                                Set<String> themaTags = thema.getTags() == null ? Set.of() : thema.getTags();
                                Set<String> profTags = prof.getTags() == null ? Set.of() : prof.getTags();

                                Set<String> allInterestTags = new HashSet<>();
                                allInterestTags.addAll(themaTags);
                                allInterestTags.addAll(profTags);

                                Set<String> reqs = thema.getRequirements() == null ? Set.of() : thema.getRequirements();
                                int totalRequirements = reqs.size();

                                int matchedRequirements = (int) reqs.stream()
                                        .map(MatchingService::normalizeOne)
                                        .filter(normalizedKurse::contains)
                                        .count();

                                int totalTags = allInterestTags.size();

                                int matchedTags = (int) allInterestTags.stream()
                                        .map(MatchingService::normalizeOne)
                                        .filter(normalizedInteressen::contains)
                                        .count();

                                int total = totalRequirements + totalTags;
                                int score = (total == 0)
                                        ? 0
                                        : (int) Math.round((matchedRequirements + matchedTags) * 100.0 / total);

                                if (matchedRequirements > 0 || matchedTags > 0) {
                                        results.add(new MatchingResult(
                                                thema,
                                                prof,
                                                matchedRequirements,
                                                totalRequirements,
                                                matchedTags,
                                                totalTags,
                                                score
                                        ));
                                }
                        }
                }

                results.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
                return results;
        }

        private static Set<String> normalize(List<String> values) {
                if (values == null) return Set.of();
                return values.stream()
                        .filter(Objects::nonNull)
                        .map(MatchingService::normalizeOne)
                        .filter(s -> !s.isBlank())
                        .collect(Collectors.toSet());
        }

        private static String normalizeOne(String s) {
                if (s == null) return "";
                return s.trim()
                        .toLowerCase()
                        .replace("-", " ")
                        .replaceAll("\\s+", " ");
        }
}
