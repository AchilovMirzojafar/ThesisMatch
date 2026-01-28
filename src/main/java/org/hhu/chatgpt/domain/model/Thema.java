package org.hhu.chatgpt.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Thema {

    private String title;
    private String description;

    private List<String> tags = new ArrayList<>();
    private List<String> requirements = new ArrayList<>();
    private String tagInput;

    private String betreuerName;

    public Thema() {
    }

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
    public String getTagInput() { return tagInput;}

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTags(List<String> tags) {
        this.tags = (tags == null) ? new ArrayList<>() : new ArrayList<>(tags);
    }

    public void setRequirements(List<String> requirements) {
        this.requirements = (requirements == null) ? new ArrayList<>() : new ArrayList<>(requirements);
    }

    public void setTagInput(String tagInput) {
        this.tagInput = tagInput;
    }

    public void setBetreuerName(String betreuerName) {
        this.betreuerName = betreuerName;
    }
}
