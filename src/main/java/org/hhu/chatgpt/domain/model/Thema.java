package org.hhu.chatgpt.domain.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Thema {

    private String title;
    private String description;

    private Set<String> tags = new HashSet<>();
    private Set<String> requirements = new HashSet<>();


    private String betreuerName;

    public Thema() {
    }

    public Thema(String title,
                 String description,
                 Set<String> tags,
                 Set<String> requirements,
                 String betreuerName) {

        this.title = title;
        this.description = description;
        this.tags = tags;
        this.requirements = requirements;
        this.betreuerName = betreuerName;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Set<String> getTags() { return tags; }
    public Set<String> getRequirements() { return requirements; }
    public String getBetreuerName() { return betreuerName; }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTags(Set<String> tags) {
        this.tags = (tags == null) ? new HashSet<>() : new HashSet<>(tags);
    }

    public void setRequirements(List<String> requirements) {
        this.requirements = (requirements == null) ? new HashSet<>() : new HashSet<>(requirements);
    }


    public void setBetreuerName(String betreuerName) {
        this.betreuerName = betreuerName;
    }
}
