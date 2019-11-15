package com.exam.bean.exam;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 考试
 */
@Getter
@Setter
@Accessors(chain = true)
public class Exam {
    private long id;        // 考试 ID
    private long paperId;   // 试卷 ID
    private long clazzId;   // 班级 ID
    private String title;   // 考试标题
    private Date startTime; // 考试开始时间
    private Date endTime;   // 考试结束时间
    private long duration;  // 考试持续时间, 单位为秒
    private int  maxTimes;  // 允许考试次数

    private double highestScore; // 最高分
    private double lowestScore;  // 最低分
    private double averageScore; // 平均分
    private double passRate;     // 及格率
}
