package com.exam.bean.exam;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 用户的考试记录，一个考试可以进行多次作答，每个作答即为一个考试记录
 */
@Getter
@Setter
@Accessors(chain = true)
public class ExamRecord {
    // 考试记录状态
    public static final int STATUS_CREATED          = 0; // 未作答
    public static final int STATUS_ANSWERED         = 1; // 已作答
    public static final int STATUS_SUBMITTED        = 2; // 已提交
    public static final int STATUS_AUTO_CORRECTED   = 3; // 自动批改
    public static final int STATUS_MANUAL_CORRECTED = 4; // 手动批改
    public static final int STATUS_FINISH_CORRECTED = 5; // 批改结束
    public static final String[] STATUS_LABELS = {"未作答", "已作答", "已提交", "自动批改", "手动批改", "批改结束"};

    private long id;          // 试卷记录 ID
    private long userId;      // 考试用户 ID
    private long examId;      // 考试 ID
    private long paperId;     // 试卷 ID，方便使用考试记录查找考试的试卷
    private int  status;      // 状态: 0 (未作答)、1 (已作答)、2 (已提交)、3 (自动批改)、4 (手动批改)、5 (批改结束) [点击考试的时候才创建考试记录]
    private int  elapsedTime; // 已考试时间，单位为秒
    private double score;     // 考试得分
    private Date    submitted_time;  // 提交试卷时间
    private boolean objective;       // true (使用的试卷全是客观题)、false (使用的试卷包含主观题)

    private Exam  exam;  // 考试
    private Paper paper; // 试卷

    /**
     * 获取考试记录的状态 label
     *
     * @return 返回状态的 label
     */
    public String getStatusLabel() {
        if (status < 0 || status >= STATUS_LABELS.length) {
            return "未定义";
        } else {
            return STATUS_LABELS[status];
        }
    }
}
