package com.exam.bean.exam;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 考试记录，一个考试可以进行多次作答，每个作答即为一个考试记录
 */
@Getter
@Setter
@Accessors(chain = true)
public class ExamRecord {
    private long id;          // 试卷记录 ID
    private long userId;      // 考试用户 ID
    private long examId;      // 考试 ID
    private long paperId;     // 试卷 ID
    private long clazzId;     // 班级 ID
    private int  status;      // 状态: 0 (已创建)、1 (已提交)、2 (已批改) [点击考试的时候才创建考试记录]
    private int  elapsedTime; // 已考试时间，单位为秒
    private int  rank;        // 考试排名
    private double score;     // 考试得分
    private Date   submitted_time; // 提交试卷时间

    private Paper paper; // 考试的试卷
}
