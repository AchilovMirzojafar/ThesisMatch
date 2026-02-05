package org.hhu.chatgpt.domain.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Thema {

    private UUID id;

    private String title;
    private String description;

    private Set<String> tags = new HashSet<>();
    private Set<String> requirements = new HashSet<>();


    public Thema() {
        this.id = UUID.randomUUID();
    }

    public Thema(String title, String description, Set<String> tags, Set<String> requirements) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        setTags(tags);
        setRequirements(requirements);
    }


    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Set<String> getTags() { return tags; }
    public Set<String> getRequirements() { return requirements; }
    public UUID getId() {return id;}

    public void setId(UUID id) {this.id = id;}
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setTags(Set<String> tags) {
        this.tags = (tags == null) ? new HashSet<>() : new HashSet<>(tags);
    }
    public void setRequirements(Set<String> requirements) {
        this.requirements = (requirements == null) ? new HashSet<>() : new HashSet<>(requirements);}

}
