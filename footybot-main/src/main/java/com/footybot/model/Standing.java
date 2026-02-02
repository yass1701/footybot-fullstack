package com.footybot.model;

import java.util.List;

// This is a simple Java object to hold data, not a database entity.
public class Standing {
    private int rank;
    private String teamName;
    private String crestUrl;
    private int played;
    private int win;
    private int draw;
    private int loss;
    private int goalsFor;
    private int goalsAgainst;
    private int goalDifference;
    private int points;
    private List<String> form;

    // Getters and Setters
    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getCrestUrl() { return crestUrl; }
    public void setCrestUrl(String crestUrl) { this.crestUrl = crestUrl; }
    public int getPlayed() { return played; }
    public void setPlayed(int played) { this.played = played; }
    public int getWin() { return win; }
    public void setWin(int win) { this.win = win; }
    public int getDraw() { return draw; }
    public void setDraw(int draw) { this.draw = draw; }
    public int getLoss() { return loss; }
    public void setLoss(int loss) { this.loss = loss; }
    public int getGoalsFor() { return goalsFor; }
    public void setGoalsFor(int goalsFor) { this.goalsFor = goalsFor; }
    public int getGoalsAgainst() { return goalsAgainst; }
    public void setGoalsAgainst(int goalsAgainst) { this.goalsAgainst = goalsAgainst; }
    public int getGoalDifference() { return goalDifference; }
    public void setGoalDifference(int goalDifference) { this.goalDifference = goalDifference; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    public List<String> getForm() { return form; }
    public void setForm(List<String> form) { this.form = form; }
}