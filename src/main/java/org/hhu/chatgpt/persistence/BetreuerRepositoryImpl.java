package org.hhu.chatgpt.persistence;

import org.hhu.chatgpt.application.applicationservice.repository.BetreuerRepository;
import org.hhu.chatgpt.domain.model.Betreuer;
import org.hhu.chatgpt.domain.model.Thema;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class BetreuerRepositoryImpl implements BetreuerRepository {

    BetreuerDaoRepository betreuerDaoRepository;

    public BetreuerRepositoryImpl(BetreuerDaoRepository betreuerDaoRepository) {
        this.betreuerDaoRepository = betreuerDaoRepository;
    }

    @Override
    public void save(Betreuer betreuer) {
        Integer existingId = betreuerDaoRepository.findByFachId(betreuer.getFachId()).map(BetreuerDTO::id).orElse(null); // Suche den Betreuer mit seiner FachId in der Datenbank, zurück gegeben wird ein DTO. Prüfe, ob das BetreuerDTO eine DatenbankId hat.
        betreuerDaoRepository.save(toBetreuerDTO(existingId, betreuer));
    }

    @Override
    public void delete(Betreuer betreuer) {
        Integer existingId = betreuerDaoRepository.findByFachId(betreuer.getFachId()).map(BetreuerDTO::id).orElse(null); // Suche den Betreuer mit seiner FachId in der Datenbank, zurück gegeben wird ein DTO. Prüfe, ob das BetreuerDTO eine DatenbankId hat.
        betreuerDaoRepository.delete(toBetreuerDTO(existingId, betreuer));
    }

    @Override
    public List<Betreuer> findAll(){
        return betreuerDaoRepository.findAll().stream().map(this::toBetreuer).toList();
    }

    @Override
    public Optional<Betreuer> findByFachId(Integer fachId){
        return betreuerDaoRepository.findByFachId(fachId).map(this::toBetreuer);
    }


   //<---------------- Betreuer -> BetreuerDTO ---------------->

    private Set<ThemaDTO> toThemenDTO(Set<Thema> themaSet){
        return themaSet.stream().map(this::toThemaDTO).collect(Collectors.toSet());
    }

    private ThemaDTO toThemaDTO(Thema thema){
        return new ThemaDTO(thema.getTitle(), thema.getDescription(), thema.getTags(), thema.getRequirements(), thema.getBetreuerName());
    }

    private BetreuerDTO toBetreuerDTO(Integer existingId, Betreuer betreuer){
        Set<ThemaDTO> themaDtoSet = toThemenDTO(betreuer.getThemenSet());
        return new BetreuerDTO(existingId, betreuer.getFachId(), betreuer.getTitel(), betreuer.getFirstName(), betreuer.getLastName(), betreuer.getEmail(), betreuer.getPhone(), betreuer.getTags(), themaDtoSet);

                //String titel, String firstName, String lastName, String email, String phone, List<String> tags, Set<ThemaDTO> themaSet
    }

    //<---------------- BetreuerDTO -> Betreuer ---------------->

    private Set<Thema> translateThemen(Set<ThemaDTO> setThemaDto){
        return setThemaDto.stream().map(this::toThema).collect(Collectors.toSet());
    }

    private Thema toThema(ThemaDTO themaDTO){
        return new Thema(themaDTO.titel(), themaDTO.description(), themaDTO.tags(), themaDTO.requirements(), themaDTO.betreuerName() );
    }

    private Betreuer toBetreuer(BetreuerDTO betreuerDTO){
        Set<Thema> themaSet = translateThemen(betreuerDTO.themaSet());
        return new Betreuer(betreuerDTO.fachId(), betreuerDTO.titel(), betreuerDTO.firstName(), betreuerDTO.lastName(), betreuerDTO.email(), betreuerDTO.phone(), betreuerDTO.tags(), themaSet);
    }

//    private String title;
//    private String description;
//
//    private List<String> tags = new ArrayList<>();
//    private List<String> requirements = new ArrayList<>();
//    private String tagInput;
//
//    private String betreuerName;

}
