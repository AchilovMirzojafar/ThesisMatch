package org.hhu.chatgpt.domain.service.betreuerService;

import org.hhu.chatgpt.domain.model.betreuer.Betreuer;
import org.hhu.chatgpt.dummydata.BetreuerDummyData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BetreuerService {

    // Key: GitHub Benutzername
    private final Map<String, Betreuer> storage = new ConcurrentHashMap<>();

    public BetreuerService() {
        // Dummy-Daten beim Start laden
        for (Betreuer betreuer : BetreuerDummyData.getBetreuerListe()) {
            storage.put(betreuer.getGitHubName(), betreuer);
        }
    }

    public List<Betreuer> list() {
        return new ArrayList<>(storage.values());
    }

    public Betreuer findByLogin(String login) {
        return storage.get(login);
    }

    public void save(Betreuer betreuer) {
        if (betreuer == null || betreuer.getGitHubName() == null) {
            throw new IllegalArgumentException("Betreuer oder GitHub-Login ist null");
        }
        storage.put(betreuer.getGitHubName(), betreuer);
    }

    }

