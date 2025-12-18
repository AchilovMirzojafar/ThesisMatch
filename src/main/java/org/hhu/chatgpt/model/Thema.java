package org.hhu.chatgpt.model;

public class Thema {

    private String title;
    private String description;
    private String tag;
    private String requirement;

    private String profName;
    private String goal;
    private String notes;

    public Thema(String title,
                 String description,
                 String tag,
                 String requirement,
                 String profName,
                 String goal,
                 String notes) {

        this.title = title;
        this.description = description;
        this.tag = tag;
        this.requirement = requirement;
        this.profName = profName;
        this.goal = goal;
        this.notes = notes;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getTag() { return tag; }
    public String getRequirement() { return requirement; }

    public String getProfName() { return profName; }
    public String getGoal() { return goal; }
    public String getNotes() { return notes; }
}