package com.footybot.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.footybot.model.Player;
import com.footybot.service.PlayerDataService;

@RestController
@RequestMapping("/api/players")
@CrossOrigin(origins = "*")
public class PlayerController {

    private final PlayerDataService playerDataService;

    public PlayerController(PlayerDataService playerDataService) {
        this.playerDataService = playerDataService;
    }

    @GetMapping("/team/{teamName}")
    public List<Player> getPlayersByTeamName(@PathVariable String teamName) {
        System.out.println("Request received for team: " + teamName);
        List<Player> players = playerDataService.getPlayersByTeamName(teamName);
        System.out.println("Returning " + players.size() + " players for team: " + teamName);
        return players;
    }
}