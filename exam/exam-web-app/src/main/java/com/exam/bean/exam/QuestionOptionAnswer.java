package com.exam.bean.exam;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 题目选项的作答
 */
@Getter
@Setter
@Accessors(chain = true)
public class QuestionOptionAnswer {
    private long questionId;
    private long questionOptionId;
    private String content;
}
