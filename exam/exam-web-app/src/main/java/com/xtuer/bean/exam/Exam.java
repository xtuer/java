package com.xtuer.bean.exam;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.*;

/**
 * 考试
 */
@Getter
@Setter
@Accessors(chain = true)
@JSONType(ignores = { "paperIdsJson" })
public class Exam {
    // 状态值
    public static final int STATUS_NOT_STARTED = 0; // 未开始
    public static final int STATUS_STARTED     = 1; // 考试中
    public static final int STATUS_ENDED       = 2; // 已结束
    public static final String[] STATUS_LABELS = { "未开始", "考试中", "已结束" };

    private long   id;           // 考试 ID
    private String title;        // 考试标题
    private Date   startTime;    // 考试开始时间
    private Date   endTime;      // 考试结束时间
    private int    duration;     // 考试持续时间, 单位秒
    private int    maxTimes;     // 允许考试次数
    private long   holderId;     // 考试拥有者 ID，例如机构 ID、班级 ID 等，根据业务需求而定
    private String paperIdsJson; // 数据库: 试卷 IDs 的 JSON 字符串

    private double highestScore; // 最高分
    private double lowestScore;  // 最低分
    private double averageScore; // 平均分
    private double passRate;     // 及格率

    private Set<Long> paperIds = new HashSet<>();              // 试卷 IDs，一个考试有多个试卷，ID 之间使用英文逗号分隔
    private List<ExamRecord> examRecords = new LinkedList<>(); // 用户的考试记录

    /**
     * 通过计算得到考试状态
     * 考试的状态: 0 (未开始), 1 (考试中), 2 (已结束)
     *
     * @return 返回考试状态
     */
    public int getStatus() {
        Date now = new Date();

        // 未开始
        if (now.before(startTime)) {
            return STATUS_NOT_STARTED;
        }

        // 已结束
        if (now.after(endTime)) {
            return STATUS_ENDED;
        }

        // 考试中
        return STATUS_STARTED;
    }

    /**
     * 获取考试的状态 label
     *
     * @return 返回状态的 label
     */
    public String getStatusLabel() {
        int status = getStatus();

        if (status < 0 || status >= STATUS_LABELS.length) {
            return "未定义";
        } else {
            return STATUS_LABELS[status];
        }
    }

    // MyBatis 使用: 保存到数据库时
    public String getPaperIdsJson() {
        return JSON.toJSONString(this.paperIds);
    }

    // MyBatis 使用: 从数据库获取时
    public Exam setPaperIdsJson(String paperIdsJson) {
        try {
            // JSON 解析的时候有可能抛异常
            this.paperIds = JSON.parseObject(paperIdsJson, new TypeReference<HashSet<Long>>() {});
        } catch (Exception ignored) {
        }

        this.paperIds = (this.paperIds != null) ? this.paperIds : new HashSet<>();
        this.paperIdsJson = paperIdsJson;

        return this;
    }
}
