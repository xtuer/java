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
    // 状态值
    public static final int STATUS_NOT_STARTED = 0; // 未开始
    public static final int STATUS_STARTED     = 1; // 考试中
    public static final int STATUS_ENDED       = 2; // 已结束
    public static final String[] STATUS_LABELS = {"未开始", "考试中", "已结束"};

    private long id;        // 考试 ID
    private long paperId;   // 试卷 ID
    private long holderId;  // 考试拥有者 ID，例如机构 ID、班级 ID 等，根据业务需求而定
    private String title;   // 考试标题
    private Date startTime; // 考试开始时间
    private Date endTime;   // 考试结束时间
    private long duration;  // 考试持续时间, 单位为秒
    private int  maxTimes;  // 允许考试次数

    private double highestScore; // 最高分
    private double lowestScore;  // 最低分
    private double averageScore; // 平均分
    private double passRate;     // 及格率

    // 考试的状态: 0 (未开始), 1 (考试中), 2 (已结束)
    // 注意: 通过计算得到，不保存到数据库
    private int status;

    /**
     * 获取考试状态
     *
     * @return 返回考试状态
     */
    public int getStatus() {
        Date now = new Date();

        if (now.before(startTime)) {
            return STATUS_NOT_STARTED;
        }

        if (now.after(endTime)) {
            return STATUS_ENDED;
        }

        return STATUS_STARTED;
    }

    /**
     * 获取考试的状态 label
     *
     * @return 返回状态的 label
     */
    public String getStatusLabel() {
        if (status < 0 || status > 2) {
            return "未定义";
        } else {
            return STATUS_LABELS[status];
        }
    }
}
