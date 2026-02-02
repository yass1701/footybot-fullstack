package com.footybot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int founded;
    private String venue;
    private String colors;
    private String coach;
    private String crest;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getFounded() { return founded; }
    public void setFounded(int founded) { this.founded = founded; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public String getColors() { return colors; }
    public void setColors(String colors) { this.colors = colors; }
    public String getCoach() { return coach; }
    public void setCoach(String coach) { this.coach = coach; }
    public String getCrest() { return crest; }
    public void setCrest(String crest) { this.crest = crest; }
}
