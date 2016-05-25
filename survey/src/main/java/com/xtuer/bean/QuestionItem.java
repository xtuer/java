package com.xtuer.bean;

public class QuestionItem {
    private int id;
    private int questionId;
    private String content;
    private int type;

    public QuestionItem() {
    }

    public QuestionItem(int id, int questionId, String content, int type) {
        this.id = id;
        this.questionId = questionId;
        this.content = content;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "QuestionItem{" +
                "id=" + id +
                ", questionId=" + questionId +
                ", content='" + content + '\'' +
                ", type=" + type +
                '}';
    }
}
