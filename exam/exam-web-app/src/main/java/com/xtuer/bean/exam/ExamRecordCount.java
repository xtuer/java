package com.xtuer.bean.exam;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

/**
 * 考试的考试记录数量
 */
@Getter
@Setter
@Accessors(chain = true)
public class ExamRecordCount {
    @Id private long examId; // 考试 ID
    private int count; // 考试记录数量
}
