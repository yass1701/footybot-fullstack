package com.footybot.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.footybot.model.Match;
import com.footybot.model.MatchWithH2HDTO;
import com.footybot.repository.MatchRepository;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    // This method is for your main "Results" page
    public List<MatchWithH2HDTO> getPastMatchesWithH2H() {
        List<Match> pastMatches = matchRepository.findByMatchDateBeforeOrderByMatchDateDesc(LocalDateTime.now());

        return pastMatches.stream().map(match -> {
            List<Match> h2hHistory = matchRepository.findHeadToHeadMatches(match.getHomeTeam(), match.getAwayTeam());
            return new MatchWithH2HDTO(match, h2hHistory);
        }).collect(Collectors.toList());
    }

    // This is for getting future matches (fixtures)
    public List<Match> getFixtures() {
        return matchRepository.findByMatchDateAfterOrderByMatchDateAsc(LocalDateTime.now());
    }

    // This is for your H2H page
    public List<Match> getHeadToHeadMatches(String team1, String team2) {
        return matchRepository.findHeadToHeadMatches(team1, team2);
    }

    // --- Methods for the Admin Panel ---

    public Match createMatch(Match match) {
        return matchRepository.save(match);
    }

    public void deleteMatch(Long id) {
        matchRepository.deleteById(id);
    }
}