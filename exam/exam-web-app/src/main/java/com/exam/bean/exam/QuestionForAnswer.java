package com.exam.bean.exam;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Transient;

import java.util.LinkedList;
import java.util.List;

/**
 * 考试时对题目选项的作答
 */
@Getter
@Setter
@Accessors(chain = true)
public class QuestionForAnswer {
    // 得分状态
    public static final int SCORE_STATUS_UNKNOWN    = 0; // 未知
    public static final int SCORE_STATUS_ERROR      = 1; // 错误
    public static final int SCORE_STATUS_HALF_RIGHT = 2; // 半对
    public static final int SCORE_STATUS_RIGHT      = 3; // 全对

    private long questionId; // 题目 ID
    private long teacherId;  // 批改题目老师的 ID
    private double score;    // 题目得分
    private int scoreStatus; // 得分状态: 0 (未知)、1 (错误)、2 (半对)、3 (全对)

    private List<QuestionOptionAnswer> answers = new LinkedList<>(); // 选项的作答

    @Transient private long examId;       // 主观题作答: 考试 ID (考试记录中不存储，但主观题作答中存储)
    @Transient private long examRecordId; // 主观题作答: 考试记录 ID  (考试记录中不存储，但主观题作答中存储)
}