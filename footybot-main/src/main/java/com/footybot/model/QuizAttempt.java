package com.footybot.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    @JsonIgnore
    private Quiz quiz;
    
    @Column(nullable = false)
    private int score;
    
    @Column(nullable = false)
    private int totalQuestions;
    
    @Column(nullable = false)
    private int correctAnswers;
    
    @Column(nullable = false)
    private int wrongAnswers;
    
    @Column(nullable = false)
    private int timeSpent; // in seconds
    
    @Column(nullable = false)
    private LocalDateTime startedAt;
    
    @Column(nullable = true)
    private LocalDateTime completedAt;
    
    @Column(nullable = false)
    private boolean isCompleted;
    
    @OneToMany(mappedBy = "quizAttempt", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<QuizAnswer> answers;
    
    // Constructors
    public QuizAttempt() {
        this.startedAt = LocalDateTime.now();
        this.isCompleted = false;
    }
    
    public QuizAttempt(User user, Quiz quiz) {
        this();
        this.user = user;
        this.quiz = quiz;
        this.totalQuestions = quiz.getTotalQuestions();
        this.score = 0;
        this.correctAnswers = 0;
        this.wrongAnswers = 0;
        this.timeSpent = 0;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Quiz getQuiz() {
        return quiz;
    }
    
    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public int getTotalQuestions() {
        return totalQuestions;
    }
    
    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
    
    public int getCorrectAnswers() {
        return correctAnswers;
    }
    
    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
    
    public int getWrongAnswers() {
        return wrongAnswers;
    }
    
    public void setWrongAnswers(int wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }
    
    public int getTimeSpent() {
        return timeSpent;
    }
    
    public void setTimeSpent(int timeSpent) {
        this.timeSpent = timeSpent;
    }
    
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public boolean isCompleted() {
        return isCompleted;
    }
    
    public void setCompleted(boolean completed) {
        isCompleted = completed;
        if (completed && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }
    
    public List<QuizAnswer> getAnswers() {
        return answers;
    }
    
    public void setAnswers(List<QuizAnswer> answers) {
        this.answers = answers;
    }
    
    // Helper methods
    public double getPercentage() {
        if (totalQuestions == 0) return 0.0;
        return (double) correctAnswers / totalQuestions * 100;
    }
    
    public void calculateScore() {
        this.score = this.correctAnswers * this.quiz.getPointsPerQuestion();
    }
}
