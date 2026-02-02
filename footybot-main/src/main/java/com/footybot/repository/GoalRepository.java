// src/main/java/com/footybot/repository/GoalRepository.java
package com.footybot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.footybot.model.Goal;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
}