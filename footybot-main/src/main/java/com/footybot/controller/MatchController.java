package com.footybot.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.footybot.model.Goal;
import com.footybot.model.Match;
import com.footybot.model.MatchWithH2HDTO;
import com.footybot.repository.GoalRepository;
import com.footybot.repository.MatchRepository;
import com.footybot.service.StandingsService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MatchController {

    private final MatchRepository matchRepository;
    private final GoalRepository goalRepository;
    private final StandingsService standingsService; // Add the service

    public MatchController(MatchRepository matchRepository, GoalRepository goalRepository, StandingsService standingsService) {
        this.matchRepository = matchRepository;
        this.goalRepository = goalRepository;
        this.standingsService = standingsService; // Initialize the service
    }

    @PostMapping("/matches-with-scorers")
    public ResponseEntity<Match> addMatchWithScorers(@RequestBody Map<String, Object> payload) {
        Match match = new Match(
            "2025-26",
            1,
            LocalDateTime.now(),
            (String) payload.get("homeTeam"),
            (String) payload.get("awayTeam"),
            (Integer) payload.get("homeGoals"),
            (Integer) payload.get("awayGoals"),
            (Integer) payload.get("homePossession"),
            (Integer) payload.get("awayPossession"),
            (Integer) payload.get("homeShots"),
            (Integer) payload.get("awayShots"),
            (Integer) payload.get("homeFouls"),
            (Integer) payload.get("awayFouls")
        );
        Match savedMatch = matchRepository.save(match);

        List<Map<String, Object>> scorers = (List<Map<String, Object>>) payload.get("scorers");
        if (scorers != null) {
            for (Map<String, Object> scorerData : scorers) {
                Goal goal = new Goal();
                goal.setMatchId(savedMatch.getId());
                goal.setPlayerName((String) scorerData.get("playerName"));
                goal.setPlayerTeam((String) scorerData.get("playerTeam"));
                goal.setMinuteScored((Integer) scorerData.get("minuteScored"));
                goalRepository.save(goal);
            }
        }
        return ResponseEntity.ok(savedMatch);
    }
    
    // This endpoint now returns the data with H2H stats included
    @GetMapping("/matches")
    public List<MatchWithH2HDTO> getPastMatches() {
        return standingsService.getMatchesWithH2H();
    }

    @GetMapping("/fixtures")
    public List<Match> getFixtures() {
        return matchRepository.findByMatchDateAfterOrderByMatchDateAsc(LocalDateTime.now());
    }

    @GetMapping("/matches/h2h")
    public List<Match> getHeadToHeadMatches(@RequestParam String team1, @RequestParam String team2) {
        return matchRepository.findHeadToHeadMatches(team1, team2);
    }

    // Admin endpoints for managing matches
    @PostMapping("/matches")
    public ResponseEntity<Match> createMatch(@RequestBody Match match) {
        // Set default values if not provided
        if (match.getSeason() == null) {
            match.setSeason("2025-26");
        }
        if (match.getMatchday() == 0) {
            match.setMatchday(1);
        }
        if (match.getMatchDate() == null) {
            match.setMatchDate(LocalDateTime.now());
        }
        
        Match savedMatch = matchRepository.save(match);
        return ResponseEntity.ok(savedMatch);
    }

    @DeleteMapping("/matches/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long id) {
        if (!matchRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        matchRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}