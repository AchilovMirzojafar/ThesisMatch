package org.hhu.chatgpt.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Table("thema")
public class ThemaDTO {

    @Id
    private UUID id;

    private String title;
    private String description;

    @Column("betreuer")
    private Integer betreuerId;

    @MappedCollection(idColumn = "thema")
    private Set<ThemaTagDTO> tags = new HashSet<>();

    @MappedCollection(idColumn = "thema")
    private Set<ThemaRequirementDTO> requirements = new HashSet<>();


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getBetreuerId() {
        return betreuerId;
    }

    public void setBetreuerId(Integer betreuerId) {
        this.betreuerId = betreuerId;
    }

    public Set<ThemaTagDTO> getTags() {
        return tags;
    }

    public void setTags(Set<ThemaTagDTO> tags) {
        this.tags = tags;
    }

    public Set<ThemaRequirementDTO> getRequirements() {
        return requirements;
    }

    public void setRequirements(Set<ThemaRequirementDTO> requirements) {
        this.requirements = requirements;
    }
}
