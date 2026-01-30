package org.hhu.chatgpt.application.applicationservice.repository;

import org.hhu.chatgpt.domain.model.Betreuer;

import java.util.List;
import java.util.Optional;

public interface BetreuerRepository {

    void save(Betreuer betreuer);


    void delete(Betreuer betreuer);

     List<Betreuer> findAll();

     Optional<Betreuer> findByFachId(Integer id);

}
