package org.hhu.chatgpt.persistence;

import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Table("thema")
public record ThemaDTO(String titel, String description, Set<String> tags, Set<String> requirements, String betreuerName) {
}
