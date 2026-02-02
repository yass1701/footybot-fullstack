package com.footybot.repository;

import com.footybot.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    
    // Find all active quizzes
    List<Quiz> findByIsActiveTrue();
    
    // Find quizzes by category
    List<Quiz> findByCategoryAndIsActiveTrue(String category);
    
    // Find quiz by title
    Optional<Quiz> findByTitle(String title);
    
    // Find quizzes by category
    List<Quiz> findByCategory(String category);
    
    // Custom query to find quizzes with their question count
    @Query("SELECT q FROM Quiz q LEFT JOIN q.questions qu WHERE q.isActive = true")
    List<Quiz> findActiveQuizzesWithQuestions();
    
    // Find quizzes with minimum questions
    @Query("SELECT q FROM Quiz q WHERE q.isActive = true AND SIZE(q.questions) >= :minQuestions")
    List<Quiz> findQuizzesWithMinimumQuestions(@Param("minQuestions") int minQuestions);
}
