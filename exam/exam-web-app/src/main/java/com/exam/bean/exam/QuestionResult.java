package com.exam.bean.exam;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 题目批改的结果
 */
@Getter
@Setter
@Accessors(chain = true)
public class QuestionResult {
    public static final int STATUS_ERROR      = 0; // 错误
    public static final int STATUS_HALF_RIGHT = 1; // 半对
    public static final int STATUS_RIGHT      = 2; // 全对

    private long examRecordId; // 考试记录 ID
    private long questionId;   // 题目 ID
    private double score;      // 题目得分
    private int status;        // 题目的作答状态: 0 (错误)、1 (半对)、2 (全对)

    public QuestionResult() {

    }

    public QuestionResult(long examRecordId, long questionId, double score, int status) {
        this.examRecordId = examRecordId;
        this.questionId = questionId;
        this.score = score;
        this.status = status;
    }
}
