package com.exam.bean.exam;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 对考试记录的作答 (用于接收前端发送来的作答数据)
 */
@Getter
@Setter
@Accessors(chain = true)
public class ExamRecordAnswer {
    private long userId;
    private long examRecordId;
    private boolean submitted;
    List<QuestionForAnswer> questions; // 题目的作答
}
