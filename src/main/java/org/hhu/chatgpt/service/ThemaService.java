package org.hhu.chatgpt.service;

import java.util.List;
import java.util.stream.Collectors;

import org.hhu.chatgpt.dummydata.ThemaDummyData;
import org.hhu.chatgpt.model.Thema;
import org.springframework.stereotype.Service;

@Service
public class ThemaService {

    public List<Thema> getGefilterteThemen(
            String search,
            String tag,
            String requirement) {

        return ThemaDummyData.getAlleThemen().stream()
                .filter(t -> search == null || t.getTitle().toLowerCase().contains(search.toLowerCase()))
                .filter(t -> tag == null || tag.isEmpty() || t.getTag().equals(tag))
                .filter(t -> requirement == null || requirement.isEmpty() || t.getRequirement().equals(requirement))
                .collect(Collectors.toList());
    }
}
