package com.xtuer.bean;

public class Answer {
    private int id;
    private int topicId;
    private int questionId;
    private int questionItemId;
    private String content;

    public Answer() {
    }

    public Answer(int id, int topicId, int questionId, int questionItemId, String content) {
        this.id = id;
        this.topicId = topicId;
        this.questionId = questionId;
        this.questionItemId = questionItemId;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getQuestionItemId() {
        return questionItemId;
    }

    public void setQuestionItemId(int questionItemId) {
        this.questionItemId = questionItemId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", topicId=" + topicId +
                ", questionId=" + questionId +
                ", questionItemId=" + questionItemId +
                ", content='" + content + '\'' +
                '}';
    }
}
