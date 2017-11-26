package com.xtuer.bean;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Question {
    /**
     * 问题定义类.
     *
     * type 的值为 1, 2, 3, 4:
     *   1: 单选题
     *   2: 多选题
     *   3: 建议
     *   4: 描述
     */
    public static final int TYPE_SINGLE      = 1;
    public static final int TYPE_MULTIPLE    = 2;
    public static final int TYPE_SUGGESTION  = 3;
    public static final int TYPE_DESCRIPTION = 4;

    private int id;
    private int topicId;
    private int type;
    private String content;
    private List<QuestionItem> items;
    private int order = 1000; // 顺序默认为 1000

    public Question() {
    }

    public Question(int id, int topicId, int type, String content) {
        this.id = id;
        this.topicId = topicId;
        this.type = type;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", topicId=" + topicId +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", items=" + items +
                ", order=" + order +
                '}';
    }
}
