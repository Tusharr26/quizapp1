package com.quizapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Question implements Serializable {
    private Long id;
    private Long quizId;
    private String questionText;
    private String questionType; // e.g., "MULTIPLE_CHOICE", "TRUE_FALSE", etc.
    private List<Option> options;
    
    public Question() {
        options = new ArrayList<>();
    }
    
    public Question(Long quizId, String questionText, String questionType) {
        this.quizId = quizId;
        this.questionText = questionText;
        this.questionType = questionType;
        this.options = new ArrayList<>();
    }
    
    public Question(Long id, Long quizId, String questionText, String questionType) {
        this.id = id;
        this.quizId = quizId;
        this.questionText = questionText;
        this.questionType = questionType;
        this.options = new ArrayList<>();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getQuizId() {
        return quizId;
    }
    
    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }
    
    public String getQuestionText() {
        return questionText;
    }
    
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    
    public String getQuestionType() {
        return questionType;
    }
    
    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }
    
    public List<Option> getOptions() {
        return options;
    }
    
    public void setOptions(List<Option> options) {
        this.options = options;
    }
    
    public void addOption(Option option) {
        this.options.add(option);
    }
    
    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", quizId=" + quizId +
                ", questionText='" + questionText + '\'' +
                ", questionType='" + questionType + '\'' +
                ", options=" + options.size() +
                '}';
    }
}