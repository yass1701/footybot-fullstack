package com.footybot.repository;

import com.footybot.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    
    // Find matches by season
    List<Match> findBySeason(String season);
    
    // Find past matches (before current time)
    List<Match> findByMatchDateBeforeOrderByMatchDateDesc(LocalDateTime dateTime);
    
    // Find future matches (after current time)
    List<Match> findByMatchDateAfterOrderByMatchDateAsc(LocalDateTime dateTime);
    
    // Find head-to-head matches between two teams
    @Query("SELECT m FROM Match m WHERE " +
           "(m.homeTeam = :team1 AND m.awayTeam = :team2) OR " +
           "(m.homeTeam = :team2 AND m.awayTeam = :team1) " +
           "ORDER BY m.matchDate DESC")
    List<Match> findHeadToHeadMatches(@Param("team1") String team1, @Param("team2") String team2);
    
    // Find matches by team name (either home or away)
    @Query("SELECT m FROM Match m WHERE m.homeTeam = :teamName OR m.awayTeam = :teamName ORDER BY m.matchDate DESC")
    List<Match> findByTeamName(@Param("teamName") String teamName);
    
    // Find matches by matchday
    List<Match> findByMatchdayOrderByMatchDateAsc(int matchday);
    
    // Find matches by date range
    List<Match> findByMatchDateBetweenOrderByMatchDateAsc(LocalDateTime startDate, LocalDateTime endDate);
}