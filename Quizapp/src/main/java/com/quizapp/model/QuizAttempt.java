package com.quizapp.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class QuizAttempt implements Serializable {
    private Long id;
    private Long userId;
    private Long quizId;
    private Timestamp startTime;
    private Timestamp endTime;
    private Double score;
    private List<Answer> answers;
    
    public QuizAttempt() {
        answers = new ArrayList<>();
    }
    
    public QuizAttempt(Long userId, Long quizId, Timestamp startTime) {
        this.userId = userId;
        this.quizId = quizId;
        this.startTime = startTime;
        this.answers = new ArrayList<>();
    }
    
    public QuizAttempt(Long id, Long userId, Long quizId, Timestamp startTime, Timestamp endTime, Double score) {
        this.id = id;
        this.userId = userId;
        this.quizId = quizId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.score = score;
        this.answers = new ArrayList<>();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getQuizId() {
        return quizId;
    }
    
    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }
    
    public Timestamp getStartTime() {
        return startTime;
    }
    
    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }
    
    public Timestamp getEndTime() {
        return endTime;
    }
    
    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }
    
    public Double getScore() {
        return score;
    }
    
    public void setScore(Double score) {
        this.score = score;
    }
    
    public List<Answer> getAnswers() {
        return answers;
    }
    
    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
    
    public void addAnswer(Answer answer) {
        this.answers.add(answer);
    }
    
    @Override
    public String toString() {
        return "QuizAttempt{" +
                "id=" + id +
                ", userId=" + userId +
                ", quizId=" + quizId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", score=" + score +
                ", answers=" + answers.size() +
                '}';
    }
}