package org.hhu.chatgpt.domain.model;

import java.util.List;

public class Thema {

    private String title;
    private String description;

    private List<String> tags;
    private List<String> requirements;

    private String betreuerName;

    public Thema(String title,
                 String description,
                 List<String> tags,
                 List<String> requirements,
                String betreuerName) {

        this.title = title;
        this.description = description;
        this.tags = tags;
        this.requirements = requirements;
        this.betreuerName = betreuerName;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public List<String> getTags() { return tags; }
    public List<String> getRequirements() { return requirements; }
    public String getBetreuerName() { return betreuerName; }
}
