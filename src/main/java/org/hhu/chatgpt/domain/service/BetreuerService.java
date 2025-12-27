package org.hhu.chatgpt.domain.service;

import org.hhu.chatgpt.domain.model.betreuer.Betreuer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BetreuerService {

    // Key: E-Mail
    private final Map<String, Betreuer> storage = new ConcurrentHashMap<>();

    public List<Betreuer> list() {
        return new ArrayList<>(storage.values());
    }

    public boolean create(Betreuer b) {
        if (b == null || b.getEmail() == null || b.getEmail().isBlank()) {
            return false;
        }
        return storage.putIfAbsent(b.getEmail(), b) == null;
    }

    public boolean deleteByEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return storage.remove(email) != null;
    }
}

