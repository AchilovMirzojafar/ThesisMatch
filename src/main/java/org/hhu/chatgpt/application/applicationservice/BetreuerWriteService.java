package org.hhu.chatgpt.application.applicationservice;

import org.hhu.chatgpt.domain.model.Betreuer;
import org.hhu.chatgpt.application.applicationservice.repository.BetreuerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class BetreuerWriteService {

    private final BetreuerRepository repository;

    public BetreuerWriteService(BetreuerRepository repository) {
        this.repository = repository;
    }

    public boolean create(Betreuer b) {
        Optional<Betreuer> existing = repository.findByFachId(b.getFachId());
        if (existing.isPresent()) {
            return false;
        }
        repository.save(b);
        return true;
    }


    public void updateProfile(Betreuer updated) {
        repository.save(updated);
    }


    public boolean deleteById(Integer fachId) {
        return repository.findByFachId(fachId)
                .map(betreuer -> { repository.delete(betreuer); return true; })
                .orElse(false);
    }


}
