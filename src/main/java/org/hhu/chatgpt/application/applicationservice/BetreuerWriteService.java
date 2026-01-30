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
        Optional<Betreuer> optionalBetreuer = repository.findByFachId(b.getFachId());
        if (optionalBetreuer.isEmpty()){
            Betreuer betreuer = optionalBetreuer.get();
            repository.save(betreuer);
            return true;
        }
        return false;
    }

    public void updateProfile(Betreuer updated) {
        repository.save(updated);
    }

    public boolean deleteById(Integer id) {
        Optional<Betreuer> optionalBetreuer = repository.findByFachId(id);
        if (optionalBetreuer.isEmpty()){
            Betreuer betreuer = optionalBetreuer.get();
            repository.delete(betreuer);
            return true;
        }
        return false;
    }


}
