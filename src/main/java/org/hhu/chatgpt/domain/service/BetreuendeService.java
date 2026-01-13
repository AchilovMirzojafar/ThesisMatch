package org.hhu.chatgpt.domain.service;

import java.util.List;
import java.util.stream.Collectors;

import org.hhu.chatgpt.domain.model.Betreuende;
import org.hhu.chatgpt.dummydata.BetreuendeDummyData;
import org.springframework.stereotype.Service;

@Service
public class BetreuendeService {
    
    public List<Betreuende> getGesuchteName(String name, String tag) {
        return BetreuendeDummyData.getAlleBetreuende().stream()
                .filter(b -> name == null || name.isEmpty()
                        || b.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(b -> tag == null || tag.isEmpty()
                        || b.getTag().equalsIgnoreCase(tag))
                .collect(Collectors.toList());
    }
}
