package com.xtuer.bean.exam;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 接收题目批改的数据
 */
@Getter
@Setter
@Accessors(chain = true)
public class QuestionCorrect {
    private long   examRecordId; // 考试记录 ID
    private long   questionId;   // 题目 ID
    private double score;        // 得分
    private String comment;      // 评语
}
