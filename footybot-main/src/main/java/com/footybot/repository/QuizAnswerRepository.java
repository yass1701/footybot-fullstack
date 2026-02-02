package com.footybot.repository;

import com.footybot.model.QuizAttempt;
import com.footybot.model.QuizAnswer;
import com.footybot.model.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    
    // Find answers by quiz attempt
    List<QuizAnswer> findByQuizAttempt(QuizAttempt quizAttempt);
    
    // Find answer by attempt and question
    QuizAnswer findByQuizAttemptAndQuestion(QuizAttempt quizAttempt, QuizQuestion question);
    
    // Find correct answers by attempt
    List<QuizAnswer> findByQuizAttemptAndIsCorrectTrue(QuizAttempt quizAttempt);
    
    // Find wrong answers by attempt
    List<QuizAnswer> findByQuizAttemptAndIsCorrectFalse(QuizAttempt quizAttempt);
}
