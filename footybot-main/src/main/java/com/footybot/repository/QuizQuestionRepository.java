package com.footybot.repository;

import com.footybot.model.Quiz;
import com.footybot.model.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    
    // Find all questions for a specific quiz
    List<QuizQuestion> findByQuiz(Quiz quiz);
    
    // Find questions by quiz ID
    List<QuizQuestion> findByQuizId(Long quizId);
    
    // Find questions by difficulty level
    List<QuizQuestion> findByQuizAndDifficulty(Quiz quiz, int difficulty);
    
    // Find random questions for a quiz (for randomizing question order)
    @Query("SELECT q FROM QuizQuestion q WHERE q.quiz = :quiz ORDER BY RANDOM()")
    List<QuizQuestion> findRandomQuestionsByQuiz(@Param("quiz") Quiz quiz);
    
    // Count questions by quiz
    long countByQuiz(Quiz quiz);
    
    // Count questions by quiz ID
    long countByQuizId(Long quizId);
}
