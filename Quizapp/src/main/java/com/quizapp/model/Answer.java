package com.quizapp.model;

import java.io.Serializable;

public class Answer implements Serializable {
    private Long id;
    private Long attemptId;
    private Long questionId;
    private Long optionId;
    
    public Answer() {
    }
    
    public Answer(Long attemptId, Long questionId, Long optionId) {
        this.attemptId = attemptId;
        this.questionId = questionId;
        this.optionId = optionId;
    }
    
    public Answer(Long id, Long attemptId, Long questionId, Long optionId) {
        this.id = id;
        this.attemptId = attemptId;
        this.questionId = questionId;
        this.optionId = optionId;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getAttemptId() {
        return attemptId;
    }
    
    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }
    
    public Long getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
    
    public Long getOptionId() {
        return optionId;
    }
    
    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }
    
    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", attemptId=" + attemptId +
                ", questionId=" + questionId +
                ", optionId=" + optionId +
                '}';
    }
}