package com.quizapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Quiz implements Serializable {
    private Long id;
    private String title;
    private String description;
    private String topic;
    private Long creatorId;
    private int timeLimit; // in minutes
    private boolean published;
    private List<Question> questions;

    public Quiz() {
        questions = new ArrayList<>();
    }

    public Quiz(String title, String description, String topic, Long creatorId) {
        this.title = title;
        this.description = description;
        this.topic = topic;
        this.creatorId = creatorId;
        this.questions = new ArrayList<>();
    }

    public Quiz(Long id, String title, String description, String topic, Long creatorId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.topic = topic;
        this.creatorId = creatorId;
        this.questions = new ArrayList<>();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", topic='" + topic + '\'' +
                ", creatorId=" + creatorId +
                ", timeLimit=" + timeLimit +
                ", published=" + published +
                ", questions=" + questions.size() +
                '}';
    }
}
