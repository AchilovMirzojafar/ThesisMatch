package org.hhu.chatgpt.domain.model;

import java.util.Set;

public record ThemaListItem(
        String title,
        String description,
        Set<String> tags,
        Set<String> requirements,
        String betreuerName
) {}
