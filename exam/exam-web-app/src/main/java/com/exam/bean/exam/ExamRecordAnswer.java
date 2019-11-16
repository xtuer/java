package com.exam.bean.exam;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 对考试记录的作答
 */
@Getter
@Setter
@Accessors(chain = true)
public class ExamRecordAnswer {
    private long examRecordId;
    private boolean submitted;
    List<QuestionOptionAnswer> answers;
}
