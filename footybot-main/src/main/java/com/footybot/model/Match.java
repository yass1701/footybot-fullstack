// src/main/java/com/footybot/model/Match.java
package com.footybot.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String season;
    private int matchday;
    private LocalDateTime matchDate;
    private String homeTeam;
    private String awayTeam;
    private Integer homeGoals;
    private Integer awayGoals;
    private Integer homePossession;
    private Integer awayPossession;
    private Integer homeShots;
    private Integer awayShots;
    private Integer homeFouls;
    private Integer awayFouls;

    public Match() {}

    // Full constructor with detailed stats
    public Match(String season, int matchday, LocalDateTime matchDate, String homeTeam, String awayTeam, Integer homeGoals, Integer awayGoals, Integer homePossession, Integer awayPossession, Integer homeShots, Integer awayShots, Integer homeFouls, Integer awayFouls) {
        this.season = season;
        this.matchday = matchday;
        this.matchDate = matchDate;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
        this.homePossession = homePossession;
        this.awayPossession = awayPossession;
        this.homeShots = homeShots;
        this.awayShots = awayShots;
        this.homeFouls = homeFouls;
        this.awayFouls = awayFouls;
    }

    // --- Getters and Setters for ALL fields ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSeason() { return season; }
    public void setSeason(String season) { this.season = season; }
    public int getMatchday() { return matchday; }
    public void setMatchday(int matchday) { this.matchday = matchday; }
    public LocalDateTime getMatchDate() { return matchDate; }
    public void setMatchDate(LocalDateTime matchDate) { this.matchDate = matchDate; }
    public String getHomeTeam() { return homeTeam; }
    public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }
    public String getAwayTeam() { return awayTeam; }
    public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }
    public Integer getHomeGoals() { return homeGoals; }
    public void setHomeGoals(Integer homeGoals) { this.homeGoals = homeGoals; }
    public Integer getAwayGoals() { return awayGoals; }
    public void setAwayGoals(Integer awayGoals) { this.awayGoals = awayGoals; }
    public Integer getHomePossession() { return homePossession; }
    public void setHomePossession(Integer homePossession) { this.homePossession = homePossession; }
    public Integer getAwayPossession() { return awayPossession; }
    public void setAwayPossession(Integer awayPossession) { this.awayPossession = awayPossession; }
    public Integer getHomeShots() { return homeShots; }
    public void setHomeShots(Integer homeShots) { this.homeShots = homeShots; }
    public Integer getAwayShots() { return awayShots; }
    public void setAwayShots(Integer awayShots) { this.awayShots = awayShots; }
    public Integer getHomeFouls() { return homeFouls; }
    public void setHomeFouls(Integer homeFouls) { this.homeFouls = homeFouls; }
    public Integer getAwayFouls() { return awayFouls; }
    public void setAwayFouls(Integer awayFouls) { this.awayFouls = awayFouls; }
}