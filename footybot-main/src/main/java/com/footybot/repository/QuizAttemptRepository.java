package com.footybot.repository;

import com.footybot.model.Quiz;
import com.footybot.model.QuizAttempt;
import com.footybot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    
    // Find attempts by user
    List<QuizAttempt> findByUser(User user);
    
    // Find attempts by quiz
    List<QuizAttempt> findByQuiz(Quiz quiz);
    
    // Find attempts by user and quiz
    List<QuizAttempt> findByUserAndQuiz(User user, Quiz quiz);
    
    // Find user's best attempt for a quiz
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user = :user AND qa.quiz = :quiz AND qa.isCompleted = true ORDER BY qa.score DESC")
    List<QuizAttempt> findBestAttemptByUserAndQuiz(@Param("user") User user, @Param("quiz") Quiz quiz);
    
    // Find top scores for a quiz
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quiz = :quiz AND qa.isCompleted = true ORDER BY qa.score DESC, qa.timeSpent ASC")
    List<QuizAttempt> findTopScoresByQuiz(@Param("quiz") Quiz quiz);
    
    // Find user's recent attempts
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user = :user ORDER BY qa.startedAt DESC")
    List<QuizAttempt> findRecentAttemptsByUser(@Param("user") User user);
    
    // Count completed attempts by user
    long countByUserAndIsCompletedTrue(User user);
    
    // Count attempts by quiz
    long countByQuiz(Quiz quiz);
    
    // Find incomplete attempts by user
    List<QuizAttempt> findByUserAndIsCompletedFalse(User user);
}
