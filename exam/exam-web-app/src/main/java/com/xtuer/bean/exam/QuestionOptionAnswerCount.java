package com.xtuer.bean.exam;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

/**
 * 题目选项的作答次数
 */
@Getter
@Setter
@Accessors(chain = true)
public class QuestionOptionAnswerCount {
    @Id private long questionOptionId; // 题目选项 ID
    private int count; // 选项作答次数
}
