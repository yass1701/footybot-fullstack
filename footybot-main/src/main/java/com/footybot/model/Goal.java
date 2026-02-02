// src/main/java/com/footybot/model/Goal.java
package com.footybot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long matchId;
    private String playerName;
    private String playerTeam;
    private int minuteScored;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public String getPlayerTeam() { return playerTeam; }
    public void setPlayerTeam(String playerTeam) { this.playerTeam = playerTeam; }
    public int getMinuteScored() { return minuteScored; }
    public void setMinuteScored(int minuteScored) { this.minuteScored = minuteScored; }
}