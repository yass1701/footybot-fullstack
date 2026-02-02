// src/main/java/com/footybot/controller/TopScorersController.java
package com.footybot.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.footybot.model.TopScorerDTO;
import com.footybot.service.TopScorersService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TopScorersController {

    private final TopScorersService topScorersService;

    public TopScorersController(TopScorersService topScorersService) {
        this.topScorersService = topScorersService;
    }

    @GetMapping("/top-scorers")
    public List<TopScorerDTO> getTopScorers() {
        return topScorersService.getTopScorers();
    }
}