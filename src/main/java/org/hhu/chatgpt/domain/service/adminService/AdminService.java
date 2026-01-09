package org.hhu.chatgpt.domain.service.adminService;

import org.hhu.chatgpt.domain.model.betreuer.Betreuer;
import org.hhu.chatgpt.dummydata.BetreuerDummyData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AdminService {

    // Key: GitHub Benutzername
    private final Map<String, Betreuer> storage = new ConcurrentHashMap<>();

    public AdminService() {
        // Dummy-Daten beim Start laden
        for (Betreuer betreuer : BetreuerDummyData.getBetreuerListe()) {
            storage.put(betreuer.getGitHubName(), betreuer);
        }
    }

    public List<Betreuer> list() {
        return new ArrayList<>(storage.values());
    }

    public boolean create(Betreuer b) {
        if (b == null || b.getGitHubName() == null || b.getGitHubName().isBlank()) {
            return false;
        }
        return storage.putIfAbsent(b.getGitHubName(), b) == null;
    }

    public boolean deleteByLogin(String login) {
        if (login == null || login.isBlank()) {
            return false;
        }
        return storage.remove(login) != null;
    }
}

