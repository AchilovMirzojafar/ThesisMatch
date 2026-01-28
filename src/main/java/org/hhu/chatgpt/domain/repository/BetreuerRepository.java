package org.hhu.chatgpt.domain.repository;

import org.hhu.chatgpt.domain.model.betreuer.Betreuer;
import org.hhu.chatgpt.dummydata.BetreuerDummyData;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class BetreuerRepository {

    // später: DB, jetzt: In-Memory
    private final Map<Integer, Betreuer> storage = new ConcurrentHashMap<>();

    public BetreuerRepository() {
        for (Betreuer b : BetreuerDummyData.getBetreuerListe()) {
            storage.put(b.getFachId(), b);
        }
    }

    public List<Betreuer> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Betreuer findById(Integer id) {
        return storage.get(id);
    }

    public boolean save(Betreuer betreuer) {
        storage.put(betreuer.getFachId(), betreuer);
        return true;
    }

    public boolean deleteById(Integer id){
        return storage.remove(id) != null;
    }
}
