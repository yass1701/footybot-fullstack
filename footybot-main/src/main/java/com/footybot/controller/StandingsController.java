package com.footybot.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.footybot.model.Standing;
import com.footybot.service.StandingsService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class StandingsController {

    private final StandingsService standingsService;

    public StandingsController(StandingsService standingsService) {
        this.standingsService = standingsService;
    }

    @GetMapping("/standings")
    public List<Standing> getStandings() {
        return standingsService.getStandingsForSeason("2025-26");
    }
}