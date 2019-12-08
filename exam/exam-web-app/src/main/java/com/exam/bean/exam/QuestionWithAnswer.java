package com.exam.bean.exam;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Transient;

import java.util.LinkedList;
import java.util.List;

/**
 * 保存作答信息的题目:
 * A. 考试记录中保存作答的题目: exam_record
 * B. 用于主观题逐题批改的作答: exam_question_correct
 */
@Getter
@Setter
@Accessors(chain = true)
public class QuestionWithAnswer {
    private long questionId; // 题目 ID
    private long teacherId;  // 批改题目老师的 ID
    private double score;    // 题目得分
    private int scoreStatus; // 得分状态: 0 (未知)、1 (错误)、2 (半对)、3 (全对)
    private String comment = ""; // 批改意见
    private int questionType;    // 题目类型 (方便批改时使用, 主观题为 4 和 5)

    private List<QuestionOptionAnswer> answers = new LinkedList<>(); // 选项的作答

    @Transient private long examId;       // 主观题作答: 考试 ID (考试记录中不存储，但主观题作答中存储)
    @Transient private long examRecordId; // 主观题作答: 考试记录 ID  (考试记录中不存储，但主观题作答中存储)
}
