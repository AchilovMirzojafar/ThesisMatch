package org.hhu.thesismatch.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface BetreuerDaoRepository extends CrudRepository<BetreuerDTO, Integer> {

    Optional<BetreuerDTO> findByFachId(Integer fachId);
    List<BetreuerDTO> findAll();


}
