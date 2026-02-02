package com.footybot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.footybot.service.ExternalFootballApiService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class Player1Controller {

    @Autowired
    private ExternalFootballApiService apiService;

    /**
     * MAIN ENDPOINT FOR YOUR COLLEGE PROJECT
     * Use this single endpoint to get all football data
     */
    @GetMapping("/football-data")
    public ResponseEntity<String> getAllFootballData() {
        try {
            String result = apiService.getAllPremierLeagueData();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            String errorResponse = "{\"error\": \"Failed to fetch football data\", \"message\": \"" + e.getMessage() + "\"}";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * ENHANCED ENDPOINT - Gets comprehensive data (teams + standings + matches)
     * Perfect for detailed analysis
     */
    @GetMapping("/complete-football-data")
    public ResponseEntity<String> getCompleteFootballData() {
        try {
            String result = apiService.getCompleteFootballData();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            String errorResponse = "{\"error\": \"Failed to fetch complete football data\", \"message\": \"" + e.getMessage() + "\"}";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Quick endpoints for specific data (backup options)
     */
    @GetMapping("/teams")
    public ResponseEntity<String> getTeams() {
        try {
            String result = apiService.fetchPremierLeagueTeams();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            String errorResponse = "{\"error\": \"Failed to fetch teams\"}";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

//     @GetMapping("/standings")
//     public ResponseEntity<String> getStandings() {
//         try {
//             String result = apiService.fetchPremierLeagueStandings();
//             return ResponseEntity.ok(result);
//         } catch (Exception e) {
//             String errorResponse = "{\"error\": \"Failed to fetch standings\"}";
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//         }
   }
 