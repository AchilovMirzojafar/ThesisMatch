package org.hhu.chatgpt.domain.model;

import org.hhu.chatgpt.domain.model.betreuer.Betreuer;

public class MatchingResult {

    private Thema thema;
    private Betreuer betreuer;

    private int matchedRequirements;
    private int totalRequirements;

    private int matchedTags;
    private int totalTags;

    private int score;

    public MatchingResult(
            Thema thema,
            Betreuer betreuer,
            int matchedRequirements,
            int totalRequirements,
            int matchedTags,
            int totalTags,
            int score) {

        this.thema = thema;
        this.betreuer = betreuer;
        this.matchedRequirements = matchedRequirements;
        this.totalRequirements = totalRequirements;
        this.matchedTags = matchedTags;
        this.totalTags = totalTags;
        this.score = score;
    }

    public Thema getThema() { return thema; }
    public Betreuer getBetreuer() { return betreuer; }

    public int getMatchedRequirements() { return matchedRequirements; }
    public int getTotalRequirements() { return totalRequirements; }

    public int getMatchedTags() { return matchedTags; }
    public int getTotalTags() { return totalTags; }

    public int getScore() { return score; }
}
