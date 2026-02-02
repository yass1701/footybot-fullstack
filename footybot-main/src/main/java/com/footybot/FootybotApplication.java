package com.footybot;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.footybot.model.Match;
import com.footybot.model.User;
import com.footybot.repository.MatchRepository;
import com.footybot.repository.UserRepository;


@SpringBootApplication
@ComponentScan(basePackages = "com.footybot")
public class FootybotApplication { // <-- The class starts here

    public static void main(String[] args) {
        SpringApplication.run(FootybotApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadMatchData(MatchRepository matchRepository) {
        return args -> {
            if (matchRepository.count() > 0) {
                System.out.println("Match data already exists. Skipping seed.");
                return;
            }

            System.out.println("Seeding database with FINAL, ACCURATE sample results...");
            
            String season = "2025-26";
            LocalDateTime now = LocalDateTime.now();
            
            List<Match> matches = List.of(
                // --- Results to generate the exact target standings ---
                new Match(season, 1, now.minusDays(40), "Liverpool FC", "Newcastle United FC", 4, 0, 60,40,20,5,10,12),
                new Match(season, 2, now.minusDays(33), "Liverpool FC", "Burnley FC", 3, 0, 65,35,18,6,8,14),
                new Match(season, 3, now.minusDays(26), "Liverpool FC", "Fulham FC", 5, 0, 70,30,25,3,5,15),
                new Match(season, 4, now.minusDays(19), "Liverpool FC", "Wolverhampton Wanderers FC", 4, 0, 68,32,22,4,7,11),
                new Match(season, 5, now.minusDays(12), "Liverpool FC", "Aston Villa FC", 3, 0, 62,38,19,7,9,13),
                new Match(season, 6, now.minusDays(5), "Everton FC", "Liverpool FC", 1, 4, 35,65,8,24,14,6),
                new Match(season, 1, now.minusDays(40), "West Ham United FC", "Leeds United FC", 3, 0, 58,42,15,7,10,11),
                new Match(season, 2, now.minusDays(33), "West Ham United FC", "Nottingham Forest FC", 2, 1, 60,40,14,9,12,13),
                new Match(season, 3, now.minusDays(26), "West Ham United FC", "Manchester United FC", 3, 2, 51,49,12,11,11,11),
                new Match(season, 4, now.minusDays(19), "Crystal Palace FC", "West Ham United FC", 1, 2, 45,55,9,14,13,10),
                new Match(season, 5, now.minusDays(12), "Sunderland AFC", "West Ham United FC", 4, 3, 53,47,16,13,9,12),
                new Match(season, 1, now.minusDays(40), "Sunderland AFC", "Wolverhampton Wanderers FC", 3, 0, 59,41,17,6,8,14),
                new Match(season, 2, now.minusDays(33), "Sunderland AFC", "Fulham FC", 4, 0, 65,35,20,4,7,13),
                new Match(season, 3, now.minusDays(26), "Sunderland AFC", "Newcastle United FC", 2, 0, 58,42,15,5,9,11),
                new Match(season, 4, now.minusDays(19), "Brighton & Hove Albion FC", "Sunderland AFC", 2, 2, 50,50,12,12,10,10),
                new Match(season, 5, now.minusDays(12), "Chelsea FC", "Sunderland AFC", 2, 2, 61,39,18,8,8,12),
                new Match(season, 1, now.minusDays(42), "Everton FC", "Man City", 1, 0, 40,60,8,18,14,7),
                new Match(season, 2, now.minusDays(35), "Everton FC", "Leeds United FC", 2, 0, 55,45,13,7,10,12),
                new Match(season, 3, now.minusDays(28), "Everton FC", "Nottingham Forest FC", 1, 0, 58,42,14,6,9,11),
                new Match(season, 4, now.minusDays(21), "Tottenham Hotspur FC", "Everton FC", 2, 0, 60,40,17,8,8,13),
                new Match(season, 6, now.minusDays(7), "Brighton & Hove Albion FC", "Everton FC", 1, 1, 52,48,11,10,10,10),
                new Match(season, 7, now.minusDays(1), "Everton FC", "Crystal Palace FC", 1, 1, 51,49,12,11,11,12),
                new Match(season, 1, now.minusDays(41), "Chelsea FC", "Man City", 3, 0, 55,45,16,9,10,11),
                new Match(season, 2, now.minusDays(34), "Chelsea FC", "Burnley FC", 4, 0, 68,32,21,5,7,13),
                new Match(season, 3, now.minusDays(27), "Chelsea FC", "Leeds United FC", 3, 0, 65,35,19,6,8,12),
                new Match(season, 1, now.minusDays(41), "Tottenham Hotspur FC", "Manchester United FC", 2, 1, 54,46,14,10,11,12),
                new Match(season, 2, now.minusDays(34), "Tottenham Hotspur FC", "Burnley FC", 2, 0, 61,39,18,7,9,13),
                new Match(season, 3, now.minusDays(27), "Tottenham Hotspur FC", "Newcastle United FC", 2, 0, 59,41,16,6,8,14),
                new Match(season, 4, now.minusDays(20), "Chelsea FC", "Tottenham Hotspur FC", 2, 2, 52,48,15,13,10,10)
            );
            
            matchRepository.saveAll(matches);
            System.out.println("Final and correct match data has been loaded.");
        };
    }

    @Bean
    CommandLineRunner createAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if an admin user already exists
            if (userRepository.findByUsername("admin").isEmpty()) {
                System.out.println("Admin user not found. Creating admin user...");
                User admin = new User();
                admin.setUsername("admin");
                // The admin password is "admin123"
                admin.setPassword(passwordEncoder.encode("admin123")); 
                admin.setRole("ROLE_ADMIN");
                userRepository.save(admin);
                System.out.println("Admin user created successfully.");
            } else {
                System.out.println("Admin user already exists.");
            }
            
            // Create a default user for quiz functionality
            if (userRepository.findByUsername("quizuser").isEmpty()) {
                System.out.println("Creating default quiz user...");
                User quizUser = new User();
                quizUser.setUsername("quizuser");
                quizUser.setPassword(passwordEncoder.encode("quiz123"));
                quizUser.setRole("ROLE_USER");
                userRepository.save(quizUser);
                System.out.println("Default quiz user created successfully.");
            } else {
                System.out.println("Default quiz user already exists.");
            }
        };
    }
}