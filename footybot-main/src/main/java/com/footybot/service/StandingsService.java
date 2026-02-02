package com.footybot.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.footybot.model.Match;
import com.footybot.model.MatchWithH2HDTO;
import com.footybot.model.Standing;
import com.footybot.model.Team;
import com.footybot.repository.MatchRepository;

@Service
public class StandingsService {

    private final MatchRepository matchRepository;
    private final PlayerDataService playerDataService;

    public StandingsService(MatchRepository matchRepository, PlayerDataService playerDataService) {
        this.matchRepository = matchRepository;
        this.playerDataService = playerDataService;
    }

    public List<Standing> getStandingsForSeason(String season) {
        List<Match> matches = matchRepository.findBySeason(season);
        Map<String, Standing> standingsMap = new HashMap<>();
        Map<String, String> teamCrests = playerDataService.getAllTeams().stream()
                .collect(Collectors.toMap(Team::getName, Team::getCrest, (a, b) -> a));
        Set<String> allTeamNames = teamCrests.keySet();

        for (String teamName : allTeamNames) {
            Standing standing = new Standing();
            standing.setTeamName(teamName);
            standing.setCrestUrl(teamCrests.get(teamName));
            standingsMap.put(teamName, standing);
        }

        for (Match match : matches) {
            if (match.getHomeGoals() != null && match.getAwayGoals() != null) {
                processTeamForStandings(standingsMap, match.getHomeTeam(), match.getHomeGoals(), match.getAwayGoals());
                processTeamForStandings(standingsMap, match.getAwayTeam(), match.getAwayGoals(), match.getHomeGoals());
            }
        }

        List<Standing> sortedStandings = new ArrayList<>(standingsMap.values());
        sortedStandings.sort(Comparator.comparingInt(Standing::getPoints).reversed()
                .thenComparingInt(Standing::getGoalDifference).reversed()
                .thenComparingInt(Standing::getGoalsFor).reversed());

        for (int i = 0; i < sortedStandings.size(); i++) {
            sortedStandings.get(i).setRank(i + 1);
        }
        return sortedStandings;
    }

    private void processTeamForStandings(Map<String, Standing> standingsMap, String teamName, int goalsFor, int goalsAgainst) {
        Standing standing = standingsMap.get(teamName);
        if (standing == null) return;

        standing.setPlayed(standing.getPlayed() + 1);
        standing.setGoalsFor(standing.getGoalsFor() + goalsFor);
        standing.setGoalsAgainst(standing.getGoalsAgainst() + goalsAgainst);
        standing.setGoalDifference(standing.getGoalsFor() - standing.getGoalsAgainst());

        if (goalsFor > goalsAgainst) {
            standing.setWin(standing.getWin() + 1);
            standing.setPoints(standing.getPoints() + 3);
        } else if (goalsFor == goalsAgainst) {
            standing.setDraw(standing.getDraw() + 1);
            standing.setPoints(standing.getPoints() + 1);
        } else {
            standing.setLoss(standing.getLoss() + 1);
        }
    }
    
    public List<MatchWithH2HDTO> getMatchesWithH2H() {
        List<Match> allMatches = matchRepository.findAll();
        List<MatchWithH2HDTO> result = new ArrayList<>();

        for (Match currentMatch : allMatches) {
            MatchWithH2HDTO dto = new MatchWithH2HDTO(currentMatch);
            int homeWins = 0;
            int awayWins = 0;
            int draws = 0;

            for (Match historicalMatch : allMatches) {
                if (historicalMatch.getHomeGoals() == null) continue;
                
                boolean isSameMatchup = (historicalMatch.getHomeTeam().equals(currentMatch.getHomeTeam()) && historicalMatch.getAwayTeam().equals(currentMatch.getAwayTeam())) ||
                                        (historicalMatch.getHomeTeam().equals(currentMatch.getAwayTeam()) && historicalMatch.getAwayTeam().equals(currentMatch.getHomeTeam()));

                if (isSameMatchup) {
                    if (historicalMatch.getHomeGoals() > historicalMatch.getAwayGoals()) {
                        if (historicalMatch.getHomeTeam().equals(currentMatch.getHomeTeam())) homeWins++; else awayWins++;
                    } else if (historicalMatch.getAwayGoals() > historicalMatch.getHomeGoals()) {
                        if (historicalMatch.getAwayTeam().equals(currentMatch.getHomeTeam())) homeWins++; else awayWins++;
                    } else {
                        draws++;
                    }
                }
            }
            
            dto.setH2hHomeWins(homeWins);
            dto.setH2hAwayWins(awayWins);
            dto.setH2hDraws(draws);
            result.add(dto);
        }
        
        result.sort(Comparator.comparing(dto -> dto.getMatch().getMatchDate(), Comparator.nullsLast(Comparator.reverseOrder())));
        return result;
    }
}