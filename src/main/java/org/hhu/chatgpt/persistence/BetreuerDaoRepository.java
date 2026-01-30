package org.hhu.chatgpt.persistence;

import org.hhu.chatgpt.domain.model.Betreuer;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BetreuerDaoRepository extends CrudRepository<BetreuerDTO, Integer> {

    Optional<BetreuerDTO> findByFachId(Integer fachId);
    BetreuerDTO save(BetreuerDTO betreuer);
    void delete(BetreuerDTO betreuer);
    List<BetreuerDTO> findAll();


}
