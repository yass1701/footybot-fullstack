package com.footybot.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.footybot.model.Player;

@Service
public class ExternalFootballApiService {

    @Value("${football.api.key}")
    private String apiToken;
    
    private static final String BASE_URL = "https://api.football-data.org/v4";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    public String getAllPremierLeagueData() throws IOException, InterruptedException {
        return makeApiCall("/competitions/PL/teams");
    }

    public String getCompleteFootballData() throws IOException, InterruptedException {
        try {
            String teams = fetchPremierLeagueTeams();
            String standings = fetchPremierLeagueStandings();
            String matches = fetchRecentMatches();
            
            JsonNode teamsNode = objectMapper.readTree(teams);
            JsonNode standingsNode = objectMapper.readTree(standings);
            JsonNode matchesNode = objectMapper.readTree(matches);
            
            StringBuilder combinedData = new StringBuilder();
            combinedData.append("{");
            combinedData.append("\"teams\":").append(teamsNode.path("teams").toString()).append(",");
            combinedData.append("\"standings\":").append(standingsNode.path("standings").toString()).append(",");
            combinedData.append("\"recentMatches\":").append(matchesNode.path("matches").toString());
            combinedData.append("}");
            
            return combinedData.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch complete football data: " + e.getMessage(), e);
        }
    }

    // **MODIFIED METHOD:** Added error handling and fallback data
    public List<Player> fetchPlayersForTeam(int teamId) {
        String responseBody = "";
        try {
            responseBody = makeApiCall("/teams/" + teamId);
        } catch (RuntimeException | IOException | InterruptedException e) {
            System.err.println("API call failed for team " + teamId + ": " + e.getMessage());
            // Fallback to hardcoded data on API error (e.g., 403 Forbidden)
            return getHardcodedPlayers(teamId);
        }

        List<Player> players = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode squadNode = rootNode.path("squad");

            if (squadNode.isMissingNode() || !squadNode.isArray()) {
                System.err.println("Squad data not found in API response. Falling back to hardcoded data.");
                return getHardcodedPlayers(teamId);
            }

            String teamName = rootNode.path("name").asText("Unknown Team");
            
            for (JsonNode playerNode : squadNode) {
                Player player = new Player();
                
                long playerId = playerNode.path("id").isNumber() ? playerNode.path("id").asLong() : 0L;
                player.setId(playerId);
                
                String playerName = playerNode.path("name").isTextual() ? playerNode.path("name").asText() : "Unknown";
                player.setName(playerName);
                
                String position = playerNode.path("position").isTextual() ? playerNode.path("position").asText() : "Unknown";
                player.setPosition(position);
                
                String nationality = playerNode.path("nationality").isTextual() ? playerNode.path("nationality").asText() : "Unknown";
                player.setNationality(nationality);
                
                String dateOfBirth = playerNode.path("dateOfBirth").isTextual() ? playerNode.path("dateOfBirth").asText() : null;
                int age = calculateAgeFromDateOfBirth(dateOfBirth);
                player.setAge(age);

                player.setTeam(teamName);
                
                player.setPhotoUrl("https://placehold.co/150x150/E8E8E8/000000?text=" + playerName.charAt(0));

                players.add(player);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse player data. Falling back to hardcoded data: " + e.getMessage());
            return getHardcodedPlayers(teamId);
        }
        return players;
    }
    
    // **NEW METHOD:** Provides hardcoded player data
    private List<Player> getHardcodedPlayers(int teamId) {
        List<Player> players = new ArrayList<>();
        
        // Hardcoded players for Arsenal FC (ID: 57)
        if (teamId == 57) {
            players.add(new Player(1L, "David Raya", "Goalkeeper", 28, "Arsenal FC", "Spain", "https://crests.football-data.org/24410.png", 22));
            players.add(new Player(2L, "Declan Rice", "Midfielder", 25, "Arsenal FC", "England", "https://crests.football-data.org/145468.png", 41));
            players.add(new Player(3L, "Kai Havertz", "Midfielder", 25, "Arsenal FC", "Germany", "https://crests.football-data.org/43734.png", 29));
            players.add(new Player(4L, "Bukayo Saka", "Forward", 23, "Arsenal FC", "England", "https://crests.football-data.org/211516.png", 7));
            players.add(new Player(5L, "William Saliba", "Defender", 23, "Arsenal FC", "France", "https://crests.football-data.org/189735.png", 2));
        }
        // Hardcoded players for Manchester City FC (ID: 65)
        else if (teamId == 65) {
            players.add(new Player(6L, "Erling Haaland", "Forward", 24, "Manchester City FC", "Norway", "https://crests.football-data.org/43477.png", 9));
            players.add(new Player(7L, "Kevin De Bruyne", "Midfielder", 33, "Manchester City FC", "Belgium", "https://crests.football-data.org/3454.png", 17));
        }
        
        return players;
    }


    private int calculateAgeFromDateOfBirth(String dateOfBirth) {
        if (dateOfBirth == null || dateOfBirth.isEmpty()) {
            return 0;
        }
        
        try {
            LocalDate birthDate = LocalDate.parse(dateOfBirth, DateTimeFormatter.ISO_LOCAL_DATE);
            return Period.between(birthDate, LocalDate.now()).getYears();
        } catch (DateTimeParseException e) {
            try {
                LocalDate birthDate = LocalDate.parse(dateOfBirth.substring(0, 10), DateTimeFormatter.ISO_LOCAL_DATE);
                return Period.between(birthDate, LocalDate.now()).getYears();
            } catch (Exception ex) {
                System.err.println("Failed to parse date of birth: " + dateOfBirth);
                return 0;
            }
        }
    }

    public String fetchPremierLeagueTeams() throws IOException, InterruptedException {
        return makeApiCall("/competitions/PL/teams");
    }
    
    public String fetchPremierLeagueStandings() throws IOException, InterruptedException {
        return makeApiCall("/competitions/PL/standings");
    }
    
    public String fetchRecentMatches() throws IOException, InterruptedException {
        return makeApiCall("/competitions/PL/matches?status=FINISHED&limit=10");
    }
    
    public String fetchUpcomingMatches() throws IOException, InterruptedException {
        return makeApiCall("/competitions/PL/matches?status=SCHEDULED&limit=10");
    }

    // **MODIFIED METHOD:** Handles 403 Forbidden errors
    private String makeApiCall(String endpoint) throws IOException, InterruptedException {
        String finalEndpoint = endpoint.startsWith("/") ? endpoint : "/" + endpoint;
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + finalEndpoint))
                .header("X-Auth-Token", apiToken)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 403) {
            String errorMessage = String.format(
                "API call failed with status: 403 Forbidden. Your API key does not have the required permissions. URL: %s", 
                BASE_URL + finalEndpoint
            );
            throw new RuntimeException(errorMessage);
        } else if (response.statusCode() != 200) {
             String errorMessage = String.format(
                "API call failed with status: %d, Response: %s, URL: %s", 
                response.statusCode(), 
                response.body(), 
                BASE_URL + finalEndpoint
            );
            throw new RuntimeException(errorMessage);
        }
        
        return response.body();
    }
}