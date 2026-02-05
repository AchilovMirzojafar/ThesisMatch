package org.hhu.chatgpt.persistence;

import org.springframework.data.relational.core.mapping.Table;

@Table("betreuer_tag")
public record BetreuerTagDTO(String tag) {}
