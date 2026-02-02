package com.footybot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.footybot.model.Player;

@Service
public class  PlayerService {

    private final ExternalFootballApiService externalFootballApiService;

    public PlayerService(ExternalFootballApiService externalFootballApiService) {
        this.externalFootballApiService = externalFootballApiService;
    }
    
    public List<Player> getPlayersByTeamId(int teamId) {
        // The ExternalFootballApiService now handles its own exceptions internally
        // and returns an empty list or hardcoded data on failure, so the try-catch is no longer needed.
        return externalFootballApiService.fetchPlayersForTeam(teamId);
    }
}