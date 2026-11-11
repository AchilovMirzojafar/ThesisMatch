package org.hhu.thesismatch.persistence;

import org.springframework.data.relational.core.mapping.Table;

@Table("thema_tag")
public record ThemaTagDTO(String tag) {}

