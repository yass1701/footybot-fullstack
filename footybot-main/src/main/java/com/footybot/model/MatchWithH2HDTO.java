package com.footybot.model;

import java.util.Comparator;
import java.util.List;

public class MatchWithH2HDTO {

    private Match match;
    private int h2hHomeWins;
    private int h2hAwayWins;
    private int h2hDraws;

    // This constructor is needed for the MatchService
    public MatchWithH2HDTO(Match match, List<Match> h2hHistory) {
        this.match = match;
        // Calculation logic can be added here if needed
    }

    // A simpler constructor that StandingsService was trying to use
    public MatchWithH2HDTO(Match match) {
        this.match = match;
    }

    // Getters
    public Match getMatch() { return match; }
    public int getH2hHomeWins() { return h2hHomeWins; }
    public int getH2hAwayWins() { return h2hAwayWins; }
    public int getH2hDraws() { return h2hDraws; }

    // Setters that StandingsService needs
    public void setH2hHomeWins(int h2hHomeWins) { this.h2hHomeWins = h2hHomeWins; }
    public void setH2hAwayWins(int h2hAwayWins) { this.h2hAwayWins = h2hAwayWins; }
    public void setH2hDraws(int h2hDraws) { this.h2hDraws = h2hDraws; }

    // Helper method for sorting
    public static Comparator<MatchWithH2HDTO> getComparator() {
        return Comparator.comparing(dto -> dto.getMatch().getMatchDate(), Comparator.nullsLast(Comparator.reverseOrder()));
    }
}