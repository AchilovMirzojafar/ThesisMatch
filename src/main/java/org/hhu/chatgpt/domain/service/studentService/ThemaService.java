package org.hhu.chatgpt.domain.service.studentService;

import java.util.List;

import org.hhu.chatgpt.domain.model.Thema;
import org.hhu.chatgpt.dummydata.ThemaDummyData;
import org.springframework.stereotype.Service;

@Service
public class ThemaService {

    public List<Thema> getGefilterteThemen(
            String search,
            String tag,
            String requirement) {

        return ThemaDummyData.getAlleThemen().stream()

                // Title search
                .filter(t ->
                        search == null || search.isBlank()
                                || t.getTitle().toLowerCase().contains(search.toLowerCase())
                )

                // Tag filter
                .filter(t ->
                        tag == null || tag.isBlank()
                                || t.getTags().contains(tag)
                )

                // Requirement filter
                .filter(t ->
                        requirement == null || requirement.isBlank()
                                || t.getRequirements().contains(requirement)
                )

                .toList();
    }
}

