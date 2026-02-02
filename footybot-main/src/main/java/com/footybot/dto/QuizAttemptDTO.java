package com.footybot.dto;

import java.time.LocalDateTime;

public class QuizAttemptDTO {
    private Long id;
    private String username;
    private String quizTitle;
    private int score;
    private int totalQuestions;
    private int correctAnswers;
    private int wrongAnswers;
    private int timeSpent;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private boolean isCompleted;

    // Constructors
    public QuizAttemptDTO() {}

    public QuizAttemptDTO(Long id, String username, String quizTitle, int score, 
                         int totalQuestions, int correctAnswers, int wrongAnswers, 
                         int timeSpent, LocalDateTime startedAt, LocalDateTime completedAt, 
                         boolean isCompleted) {
        this.id = id;
        this.username = username;
        this.quizTitle = quizTitle;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.timeSpent = timeSpent;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.isCompleted = isCompleted;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
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
    }
}
