package org.hhu.chatgpt.persistence;

import jakarta.validation.constraints.*;
import org.hhu.chatgpt.domain.model.Thema;
import org.hhu.chatgpt.domain.model.files.File;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.Set;

@Table("betreuer")
public record BetreuerDTO(@Id Integer id, Integer fachId, String titel, String firstName, String lastName, String email, String phone, Set<String> tags, Set<ThemaDTO> themaSet) {
}

