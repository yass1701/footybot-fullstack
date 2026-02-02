package com.footybot.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TeamService {

    private final ExternalFootballApiService externalFootballApiService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Map<String, Integer> teamNameToIdMap;

    @Autowired
    public TeamService(ExternalFootballApiService externalFootballApiService) {
        this.externalFootballApiService = externalFootballApiService;
    }

    // @PostConstruct
    // public void init() {
    //     try {
    //         String teamsJson = externalFootballApiService.fetchPremierLeagueTeams();
    //         JsonNode rootNode = objectMapper.readTree(teamsJson);
    //         // Correct way to map the team names to IDs
    //         this.teamNameToIdMap = StreamSupport.stream(rootNode.path("teams").spliterator(), false)
    //                 .collect(Collectors.toMap(
    //                     teamNode -> teamNode.path("name").asText(),
    //                     teamNode -> teamNode.path("id").asInt()
    //                 ));
                

    //         System.out.println("Team Name to ID Map initialized successfully.");
    //         teamNameToIdMap.forEach((key, value) -> System.out.println(key + " : " + value));

    //     } catch (IOException | InterruptedException e) {
    //         System.err.println("Failed to initialize team data from API: " + e.getMessage());
    //     }
    // }

    public int getTeamId(String teamName) {
        return teamNameToIdMap.getOrDefault(teamName, 0);
    }
}