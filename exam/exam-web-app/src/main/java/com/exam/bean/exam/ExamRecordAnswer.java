package com.exam.bean.exam;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 对考试记录的作答 (用于接收前端发送来的作答数据)
 */
@Getter
@Setter
@Accessors(chain = true)
public class ExamRecordAnswer {
    private long userId;       // 用户 ID
    private long examRecordId; // 考试记录 ID
    private boolean submitted; // 是否提交试卷
    private Date submittedAt = new Date(); // 提交试卷时间
    List<QuestionForAnswer> questions; // 题目的作答
}
