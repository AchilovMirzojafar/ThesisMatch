package org.hhu.chatgpt.domain.service.studentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hhu.chatgpt.domain.model.MatchingResult;
import org.hhu.chatgpt.domain.model.Thema;
import org.hhu.chatgpt.domain.model.betreuer.Betreuer;
import org.hhu.chatgpt.dummydata.BetreuerDummyData;
import org.hhu.chatgpt.dummydata.ThemaDummyData;
import org.springframework.stereotype.Service;

@Service
public class MatchingService {

        public List<MatchingResult> getMatchingResults(
                        List<String> selectedKurse,
                        List<String> selectedInteressen) {

                List<MatchingResult> results = new ArrayList<>();

                Set<String> normalizedKurse = selectedKurse == null
                                ? Set.of()
                                : selectedKurse.stream()
                                                .map(String::trim)
                                                .map(s -> s.toLowerCase().replace("-", " "))
                                                .collect(Collectors.toSet());

                Set<String> normalizedInteressen = selectedInteressen == null
                                ? Set.of()
                                : selectedInteressen.stream()
                                                .map(String::trim)
                                                .map(s -> s.toLowerCase().replace("-", " "))
                                                .collect(Collectors.toSet());

                if (normalizedKurse.isEmpty() && normalizedInteressen.isEmpty()) {
                        return results;
                }

                List<Thema> themen = ThemaDummyData.getAlleThemen();
                List<Betreuer> betreuendeListe = BetreuerDummyData.getBetreuerListe();

                for (Thema thema : themen) {

                        // find matching professor
                        String betreuerName = thema.getBetreuerName();
                        Betreuer prof = null;

                        if (betreuerName != null) {
                                String t = betreuerName.toLowerCase()
                                                .replace(".", " ")
                                                .replace(",", " ")
                                                .replace("-", " ")
                                                .replaceAll("\\s+", " ")
                                                .trim();

                                prof = betreuendeListe.stream()
                                                .filter(b -> {
                                                        String first = b.getFirstName() == null ? ""
                                                                        : b.getFirstName().toLowerCase().trim();
                                                        String last = b.getLastName() == null ? ""
                                                                        : b.getLastName().toLowerCase().trim();

                                                        boolean lastMatch = !last.isBlank() && t.contains(last);
                                                        boolean firstMatch = !first.isBlank() && t.contains(first);

                                                        return lastMatch || firstMatch;
                                                })
                                                .findFirst()
                                                .orElse(null);
                        }

                        if (prof == null)
                                continue;

                        Set<String> allInterestTags = new java.util.HashSet<>();

                        if (thema.getTags() != null)
                                allInterestTags.addAll(thema.getTags());
                        if (prof.getTags() != null)
                                allInterestTags.addAll(prof.getTags());

                        List<String> reqs = thema.getRequirements() == null ? List.of() : thema.getRequirements();
                        int totalRequirements = reqs.size();

                        int matchedRequirements = (int) reqs.stream()
                                        .map(String::trim)
                                        .map(s -> s.toLowerCase().replace("-", " "))
                                        .filter(normalizedKurse::contains)
                                        .count();

                        int totalTags = allInterestTags.size();

                        int matchedTags = (int) allInterestTags.stream()
                                        .map(String::trim)
                                        .map(s -> s.toLowerCase().replace("-", " "))
                                        .filter(normalizedInteressen::contains)
                                        .count();
                                        
                        int score = 0;
                        int total = totalRequirements + totalTags;

                        if (total > 0) {
                                score = (int) Math.round((matchedRequirements + matchedTags) * 100.0 / total);
                        }

                        if (matchedRequirements > 0 || matchedTags > 0) {
                                results.add(new MatchingResult(
                                                thema,
                                                prof,
                                                matchedRequirements,
                                                totalRequirements,
                                                matchedTags,
                                                totalTags,
                                                score));
                        }
                }
                
                results.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
                return results;
        }
}
