package com.footybot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String position;
    private int age;
    private String team;
    private String photoUrl;
    private String nationality;
    private int number;

    // Constructors
    public Player() {}

    public Player(String name, String position, int age, String team, String nationality, String photoUrl, int number) {
        this.name = name;
        this.position = position;
        this.age = age;
        this.team = team;
        this.nationality = nationality;
        this.photoUrl = photoUrl;
        this.number = number;
    }
    public Player(Long id, String name, String position, int age, String team, String nationality, String photoUrl, int number) {
    this.id = id;
    this.name = name;
    this.position = position;
    this.age = age;
    this.team = team;
    this.nationality = nationality;
    this.photoUrl = photoUrl;
    this.number = number;
}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", age=" + age +
                ", team='" + team + '\'' +
                ", nationality='" + nationality + '\'' +
                ", number=" + number +
                '}';
    }
}
