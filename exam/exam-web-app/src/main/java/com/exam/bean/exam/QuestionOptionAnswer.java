package com.exam.bean.exam;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Transient;

/**
 * 题目选项的作答
 */
@Getter
@Setter
@Accessors(chain = true)
public class QuestionOptionAnswer {
    @Transient private long examId;       // 考试 ID
    @Transient private long examRecordId; // 考试记录 ID

    private long questionId;       // 题目 ID
    private long questionOptionId; // 选项 ID
    private String content;        // 主观题的作答
}
