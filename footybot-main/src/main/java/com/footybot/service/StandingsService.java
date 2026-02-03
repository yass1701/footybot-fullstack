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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class StandingsService {

    private final MatchRepository matchRepository;
    private final PlayerDataService playerDataService;
    private final ExternalFootballApiService externalFootballApiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public StandingsService(
            MatchRepository matchRepository,
            PlayerDataService playerDataService,
            ExternalFootballApiService externalFootballApiService
    ) {
        this.matchRepository = matchRepository;
        this.playerDataService = playerDataService;
        this.externalFootballApiService = externalFootballApiService;
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

    /**
     * Option A: LIVE Premier League table.
     *
     * Fetches the official standings from the external API and maps them into our {@link Standing} model
     * so the existing frontend table works without changes.
     */
    public List<Standing> getLivePremierLeagueStandings() {
        try {
            // Get current season + current matchday so the table matches "current PL table"
            Integer seasonYear = null;
            Integer currentMatchday = null;
            try {
                String competitionJson = externalFootballApiService.fetchPremierLeagueCompetition();
                JsonNode competitionRoot = objectMapper.readTree(competitionJson);
                JsonNode currentSeason = competitionRoot.path("currentSeason");
                String startDate = currentSeason.path("startDate").asText(null); // e.g. "2025-08-09"
                if (startDate != null && startDate.length() >= 4) {
                    seasonYear = Integer.parseInt(startDate.substring(0, 4));
                }
                currentMatchday = currentSeason.path("currentMatchday").isNumber()
                        ? currentSeason.path("currentMatchday").asInt()
                        : null;
            } catch (Exception e) {
                // If metadata fetch fails, fall back to default standings endpoint.
                System.err.println("Failed to read competition metadata for season/matchday: " + e.getMessage());
            }

            String json = (seasonYear != null || currentMatchday != null)
                    ? externalFootballApiService.fetchPremierLeagueStandings(seasonYear, currentMatchday)
                    : externalFootballApiService.fetchPremierLeagueStandings();
            JsonNode root = objectMapper.readTree(json);

            JsonNode standingsArray = root.path("standings");
            if (!standingsArray.isArray()) return List.of();

            JsonNode totalStanding = null;
            for (JsonNode s : standingsArray) {
                if ("TOTAL".equalsIgnoreCase(s.path("type").asText())) {
                    totalStanding = s;
                    break;
                }
            }
            if (totalStanding == null) {
                // fallback: take the first standings entry if TOTAL isn't present
                totalStanding = standingsArray.size() > 0 ? standingsArray.get(0) : null;
            }
            if (totalStanding == null) return List.of();

            JsonNode table = totalStanding.path("table");
            if (!table.isArray()) return List.of();

            List<Standing> result = new ArrayList<>();
            for (JsonNode row : table) {
                Standing standing = new Standing();

                standing.setRank(row.path("position").asInt(0));
                JsonNode team = row.path("team");
                standing.setTeamName(team.path("name").asText(""));
                standing.setCrestUrl(team.path("crest").asText(""));

                standing.setPlayed(row.path("playedGames").asInt(0));
                standing.setWin(row.path("won").asInt(0));
                standing.setDraw(row.path("draw").asInt(0));
                standing.setLoss(row.path("lost").asInt(0));
                standing.setGoalsFor(row.path("goalsFor").asInt(0));
                standing.setGoalsAgainst(row.path("goalsAgainst").asInt(0));
                standing.setGoalDifference(row.path("goalDifference").asInt(0));
                standing.setPoints(row.path("points").asInt(0));

                result.add(standing);
            }

            // ensure consistent ordering by rank
            result.sort(Comparator.comparingInt(Standing::getRank));
            return result;
        } catch (Exception e) {
            System.err.println("Failed to fetch live PL standings: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
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