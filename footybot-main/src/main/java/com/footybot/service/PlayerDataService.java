package com.footybot.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.footybot.model.Player;
import com.footybot.model.Team;

@Service
public class PlayerDataService {
    

    private List<Player> allPlayers = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

 public PlayerDataService() {
    System.out.println("--> 1. PlayerDataService constructor has been called.");
    File dataFile = new File("C:\\footydata\\players.json");

    if (!dataFile.exists()) {
        System.err.println("--> CRITICAL ERROR: File not found...");
        return;
    }

    try (InputStream is = new FileInputStream(dataFile)) {
        System.out.println("--> 2. Reading and parsing complex JSON structure...");
        
        // This is the correct logic for your nested JSON file
        JsonNode root = objectMapper.readTree(is);
        JsonNode teamsNode = root.path("teams"); 

        if (teamsNode.isArray()) {
            for (JsonNode teamNode : teamsNode) {
                String teamName = teamNode.path("name").asText();
                String teamLogo = teamNode.path("crest").asText(); // Get the team crest URL
                JsonNode squadNode = teamNode.path("squad");

                if (squadNode.isArray()) {
                    for (JsonNode playerNode : squadNode) {
                        Player player = new Player();
                        player.setId(playerNode.path("id").asLong(0));
                        player.setName(playerNode.path("name").asText(null));
                        player.setPosition(playerNode.path("position").asText(null));
                        player.setNationality(playerNode.path("nationality").asText(null));
                        player.setTeam(teamName);
                        player.setPhotoUrl(teamLogo); // Set player photo to the team's crest
                        allPlayers.add(player);
                    }
                }
            }
        }
        
        System.out.println("--> 3. ✅ Loaded " + allPlayers.size() + " total players from the data file.");

    } catch (IOException e) {
        System.err.println("--> CATASTROPHIC ERROR reading or parsing players.json: " + e.getMessage());
        e.printStackTrace();
    }
}

    // The rest of this file is now correct and doesn't need to change
    public List<Player> getPlayersByTeamName(String teamNameFromFrontend) {
        System.out.println("\n=== PlayerDataService Debug Log ===");
            System.out.println("1. Received team name from frontend: '" + teamNameFromFrontend + "'");
            

        String standardFrontendName = getStandardTeamName(teamNameFromFrontend);
        System.out.println("2. Standardized frontend name: '" + standardFrontendName + "'");
    System.out.println("3. Total players loaded from JSON: " + allPlayers.size());

        
        List<Player> filteredPlayers = allPlayers.stream()
            .filter(player -> {
                if (player == null || player.getTeam() == null) return false;
                
                String standardPlayerTeamName = getStandardTeamName(player.getTeam());
                            boolean matches = standardPlayerTeamName.equals(standardFrontendName);
                            

                return standardPlayerTeamName.equals(standardFrontendName);
            })
            
            .filter(distinctByKey(Player::getId))
            .collect(Collectors.toList());
        System.out.println("✅ Found " + filteredPlayers.size() + " players for '" + teamNameFromFrontend + "'");
        return filteredPlayers;
    }

    private String getStandardTeamName(String name) {
        if (name == null) return "unknown";
        String simplified = name.toLowerCase().trim();
        switch (simplified) {
            case "arsenal": case "arsenal fc": return "arsenal fc";
            case "aston villa": case "aston villa fc": return "aston villa fc";
            case "chelsea": case "chelsea fc": return "chelsea fc";
            case "man city": case "manchester city": case "manchester city fc": return "manchester city fc";
            case "man united": case "manchester united": case "manchester united fc": return "manchester united fc";
            case "spurs": case "tottenham": case "tottenham hotspur fc": return "tottenham hotspur fc";
            case "newcastle": case "newcastle united": case "newcastle united fc": return "newcastle united fc";
            case "west ham": case "west ham united": case "west ham united fc": return "west ham united fc";
            case "wolves": case "wolverhampton wanderers": case "wolverhampton wanderers fc": return "wolverhampton wanderers fc";
            case "liverpool": case "liverpool fc": return "liverpool fc";
            case "brighton": case "brighton & hove albion": case "brighton & hove albion fc": return "brighton & hove albion fc";
            default: return simplified;
        }
    }

   // Add this new method inside your PlayerDataService class

public List<Team> getAllTeams() {
    List<Team> teams = new ArrayList<>();
    File dataFile = new File("C:\\footydata\\players.json");
    if (!dataFile.exists()) {
        return teams; // Return empty list if file not found
    }

    try (InputStream is = new FileInputStream(dataFile)) {
        JsonNode root = objectMapper.readTree(is);
        JsonNode teamsNode = root.path("teams");
        if (teamsNode.isArray()) {
            for (JsonNode teamNode : teamsNode) {
                Team team = new Team();
                team.setName(teamNode.path("name").asText());
                team.setCrest(teamNode.path("crest").asText());
                teams.add(team);
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading players.json for teams: " + e.getMessage());
    }
    return teams;
}

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}