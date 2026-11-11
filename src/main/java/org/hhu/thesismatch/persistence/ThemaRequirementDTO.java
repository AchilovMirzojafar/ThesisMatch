package org.hhu.thesismatch.persistence;

import org.springframework.data.relational.core.mapping.Table;

@Table("thema_requirement")
public record ThemaRequirementDTO(String requirement) {}
