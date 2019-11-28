package com.exam.bean.exam;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.LinkedList;
import java.util.List;

/**
 * 考试时对题目选项的作答
 */
@Getter
@Setter
@Accessors(chain = true)
public class QuestionForAnswer {
    private long   examRecordId; // 考试记录 ID
    private long   questionId;   // 题目 ID
    private double score;        // 题目得分
    private int    scoreStatus;  // 得分状态: 0 (未批改)、1 (错误)、2 (半对)、3 (全对)

    private List<QuestionOptionAnswer> answers = new LinkedList<>(); // 选项的作答

    @Getter
    @Setter
    public static class QuestionOptionAnswer {
        private long questionId;
        private long questionOptionId;
        private String content;
    }
}
