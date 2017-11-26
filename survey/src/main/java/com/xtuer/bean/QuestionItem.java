package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
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
