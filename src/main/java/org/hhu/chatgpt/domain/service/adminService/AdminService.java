package org.hhu.chatgpt.domain.service.adminService;

import org.hhu.chatgpt.domain.model.betreuer.Betreuer;
import org.hhu.chatgpt.domain.repository.BetreuerRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminService {

    private final BetreuerRepository repository;

    public AdminService(BetreuerRepository repository) {
        this.repository = repository;
    }

    public List<Betreuer> list() {
        return repository.findAll();
    }

    public boolean create(Betreuer b) {
        return repository.findByLogin(b.getGitHubName()) == null
                && repository.save(b);
    }

    public boolean deleteByLogin(String login) {
        return repository.deleteByLogin(login);
    }
}
