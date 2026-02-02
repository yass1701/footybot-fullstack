// src/main/java/com/footybot/model/TopScorerDTO.java
package com.footybot.model;

public class TopScorerDTO {
    private int rank;
    private String playerName;
    private String playerTeam;
    private String crestUrl;
    private int goals;

    // Getters and Setters
    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public String getPlayerTeam() { return playerTeam; }
    public void setPlayerTeam(String playerTeam) { this.playerTeam = playerTeam; }
    public String getCrestUrl() { return crestUrl; }
    public void setCrestUrl(String crestUrl) { this.crestUrl = crestUrl; }
    public int getGoals() { return goals; }
    public void setGoals(int goals) { this.goals = goals; }
}