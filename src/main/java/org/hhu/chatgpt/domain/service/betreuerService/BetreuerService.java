package org.hhu.chatgpt.domain.service.betreuerService;

import org.hhu.chatgpt.domain.model.betreuer.Betreuer;
import org.hhu.chatgpt.domain.repository.BetreuerRepository;
import org.springframework.stereotype.Service;

@Service
public class BetreuerService {

    private final BetreuerRepository repository;

    public BetreuerService(BetreuerRepository repository) {
        this.repository = repository;
    }

    public Betreuer findOwnProfile(String login) {
        return repository.findByLogin(login);
    }

    public void updateProfile(Betreuer updated) {
        repository.save(updated);
    }
}

