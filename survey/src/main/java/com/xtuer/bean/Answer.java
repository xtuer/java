package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
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
