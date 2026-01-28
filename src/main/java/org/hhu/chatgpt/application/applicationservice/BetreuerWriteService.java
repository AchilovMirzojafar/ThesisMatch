package org.hhu.chatgpt.application.applicationservice;

import org.hhu.chatgpt.domain.model.betreuer.Betreuer;
import org.hhu.chatgpt.domain.repository.BetreuerRepository;
import org.springframework.stereotype.Service;


@Service
public class BetreuerWriteService {

    private final BetreuerRepository repository;

    public BetreuerWriteService(BetreuerRepository repository) {
        this.repository = repository;
    }

    public boolean create(Betreuer b) {
        return repository.findById(b.getFachId()) == null
                && repository.save(b);
    }

    public void updateProfile(Betreuer updated) {
        repository.save(updated);
    }

    public boolean deleteById(Integer id) {
        return repository.deleteById(id);
    }


}
