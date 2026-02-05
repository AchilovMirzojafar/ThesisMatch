package org.hhu.chatgpt.web;

import java.util.Set;

public record ThemaListItem(
        String title,
        String description,
        Set<String> tags,
        Set<String> requirements,
        String betreuerName
) {}
