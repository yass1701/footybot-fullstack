// src/main/java/com/footybot/model/GoalDTO.java
package com.footybot.model;

public class GoalDTO {
    private String playerName;
    private String playerTeam;
    private int minuteScored;

    // Getters and Setters
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public String getPlayerTeam() { return playerTeam; }
    public void setPlayerTeam(String playerTeam) { this.playerTeam = playerTeam; }
    public int getMinuteScored() { return minuteScored; }
    public void setMinuteScored(int minuteScored) { this.minuteScored = minuteScored; }
}