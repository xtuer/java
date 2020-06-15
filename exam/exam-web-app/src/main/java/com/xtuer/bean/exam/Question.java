package com.xtuer.bean.exam;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.LinkedList;
import java.util.List;

/**
 * 题目，分为二大类:
 * A. 可以作答的题目: 客观题 (单选题、多选题、判断题、标星题)、主观题 (填空题、问答题)
 * B. 不可作答的题目: 题型题、复合题的大题，他们既不是客观题，也不是主观题
 */
@Getter
@Setter
@Accessors(chain = true)
@JSONType(ignores = { "optionsJson" })
public class Question {
    // 题目类型
    public static final int SINGLE_CHOICE   = 1; // 单选题
    public static final int MULTIPLE_CHOICE = 2; // 多选题
    public static final int TFNG            = 3; // 判断题: true(是), false(否), not given(未提及)
    public static final int FITB            = 4; // 填空题: fill in the blank
    public static final int ESSAY           = 5; // 问答题
    public static final int COMPOSITE       = 6; // 复合题
    public static final int DESCRIPTION     = 7; // 题型题 (大题分组、介绍)
    public static final int STAR            = 8; // 标星题

    // 得分状态
    public static final int SCORE_STATUS_UNKNOWN    = 0; // 未知
    public static final int SCORE_STATUS_ERROR      = 1; // 错误
    public static final int SCORE_STATUS_HALF_RIGHT = 2; // 半对
    public static final int SCORE_STATUS_RIGHT      = 3; // 全对

    private long   id;         // 题目 ID
    private String stem;       // 题干
    private String key;        // 参考答案
    private String analysis;   // 题目解析
    private int    type;       // 题目类型: 0 (未知)、1 (单选题)、2 (多选题)、3 (判断题)、4 (填空题)、5 (问答题)、6 (复合题)、7 (题型题)、8 (打星题)
    private int    difficulty; // 题目难度: 0 (未知)、1 (容易)、2 (较易)、3 (一般)、4 (较难)、5 (困难)
    private int    position;   // 复合题的小题在题目中的位置
    private int    purpose;    // 题目用途: 0 (考试题目)、1 (问卷题目)
    private long   parentId;   // 复合题的小题所属大题 ID
    private boolean deleted;   // 是否被删除

    private List<QuestionOption> options = new LinkedList<>(); // 题目的选项
    private List<Question> subQuestions  = new LinkedList<>(); // 复合题的小题

    private long   paperId;    // 试卷: 试卷 ID
    private String snLabel;    // 试卷: 题目序号
    private int    groupSn;    // 试卷: 大题 (题型) 分组序号，例如属于第一大题单选题组，顺序表示在试卷中的位置
    private double score;      // 试卷: 每题得分 (创建试卷时题型题下的题目每题得分，方便构造题干): 单选题，每题 5 分，共 30 分
    private double totalScore; // 试卷: 题目满分 (题目、题型题、复合题的满分)
    private int    scoreStatus;     // 作答: 状态为 0 (未知)、1 (错误)、2 (半对)、3 (全对)
    private int    positionInPaper; // 试卷: 题目在试卷中的位置

    private String optionsJson; // 数据库: 选项的 JSON 字符串

    // MyBatis 使用: 保存到数据库时
    public String getOptionsJson() {
        return JSON.toJSONString(this.options);
    }

    // MyBatis 使用: 从数据库获取时
    public Question setOptionsJson(String optionsJson) {
        try {
            // JSON 解析的时候有可能抛异常
            this.options = JSON.parseObject(optionsJson, new TypeReference<LinkedList<QuestionOption>>() {});
        } catch (Exception ignored) {
        }

        this.options = (this.options != null) ? this.options : new LinkedList<>();
        this.optionsJson = optionsJson;

        return this;
    }

    /**
     * 判断题目是否客观题
     *
     * @return 客观题返回 true，否则返回 false
     */
    public boolean isObjective() {
        // 1. 单选题、多选题、判断题、标星题为客观题
        // 2. 其他题型为非客观题 (例如题型题、复合题)

        // [1] 单选题、多选题、判断题、标星题为客观题
        if (type == SINGLE_CHOICE || type == MULTIPLE_CHOICE || type == TFNG || type == STAR) {
            return true;
        }

        // [2] 其他题型为非客观题
        return false;
    }

    /**
     * 判断题目是否主观题
     *
     * @return 主观题返回 true，否则返回 false
     */
    public boolean isSubjective() {
        // 1. 填空题、问答题为主观题
        // 2. 其他题型为非主观题

        // [1] 填空题、问答题为主观题
        if (type == FITB || type == ESSAY) {
            return true;
        }

        // [2] 其他题型为非主观题
        return false;
    }
}
