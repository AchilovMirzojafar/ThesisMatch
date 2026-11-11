package org.hhu.thesismatch.application.applicationservice;

import java.util.List;

import org.hhu.thesismatch.application.applicationservice.repository.BetreuerRepository;
import org.hhu.thesismatch.domain.model.MatchingResult;
import org.hhu.thesismatch.domain.service.MatchingCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lädt die Daten und delegiert die eigentliche Berechnung an den Domain Service
 * {@link MatchingCalculator}.
 */
@Service
@Transactional(readOnly = true)
public class MatchingService {

    private final BetreuerRepository betreuerRepository;
    private final MatchingCalculator calculator = new MatchingCalculator();

    public MatchingService(BetreuerRepository betreuerRepository) {
        this.betreuerRepository = betreuerRepository;
    }

    public List<MatchingResult> getMatchingResults(List<String> kurse, List<String> interessen) {
        boolean nichtsAusgewaehlt = (kurse == null || kurse.isEmpty())
                && (interessen == null || interessen.isEmpty());
        if (nichtsAusgewaehlt) {
            return List.of();
        }
        return calculator.berechne(betreuerRepository.findAll(), kurse, interessen);
    }
}
