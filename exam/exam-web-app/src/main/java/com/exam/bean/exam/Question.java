package com.exam.bean.exam;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 题目，分为二大类:
 * A. 可以作答的题目: 客观题 (单选题、多选题、判断题)、主观题 (填空题、问答题)
 * B. 不可作答的题目: 题型题、复合题的大题
 */
@Getter
@Setter
@Accessors(chain = true)
@JSONType(ignores = {"optionsJson"})
public class Question {
    // 题目类型
    public static final int SINGLE_CHOICE   = 1; // 单选题
    public static final int MULTIPLE_CHOICE = 2; // 多选题
    public static final int TFNG            = 3; // 判断题: true(是), false(否), not given(未提及)
    public static final int FITB            = 4; // 填空题: fill in the blank
    public static final int ESSAY           = 5; // 问答题
    public static final int COMPOSITE       = 6; // 复合题
    public static final int DESCRIPTION     = 7; // 题型题 (大题分组、介绍)

    // 得分状态
    public static final int SCORE_STATUS_UNKNOWN    = 0; // 未知
    public static final int SCORE_STATUS_ERROR      = 1; // 错误
    public static final int SCORE_STATUS_HALF_RIGHT = 2; // 半对
    public static final int SCORE_STATUS_RIGHT      = 3; // 全对

    private long   id;   // 题目 ID
    private String stem; // 题干
    private String key;  // 参考答案
    private String analysis;   // 题目解析
    private int    type;       // 题目类型: 0 (未知)、1 (单选题)、2 (多选题)、3 (判断题)、4 (填空题)、5 (问答题)、6 (复合题)、7 (题型题)
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

    private String optionsJson; // 选项的 JSON 字符串

    // MyBatis 使用
    public String getOptionsJson() {
        return JSON.toJSONString(this.options);
    }

    // MyBatis 使用
    public Question setOptionsJson(String optionsJson) {
        this.optionsJson = optionsJson;

        if (optionsJson == null) {
            this.options = new LinkedList<>();
        } else {
            this.options = JSON.parseObject(optionsJson, new TypeReference<LinkedList<QuestionOption>>() {});
        }

        return this;
    }

    /**
     * 判断题目是否客观题
     *
     * @return 客观题返回 true，否则返回 false
     */
    public boolean isObjective() {
        return type == SINGLE_CHOICE || type == MULTIPLE_CHOICE || type == TFNG;
    }

    /**
     * 判断题目是否主观题
     *
     * @return 主观题返回 true，否则返回 false
     */
    public boolean isSubjective() {
        if (type == FITB || type == ESSAY) {
            return true;
        }

        // 复合题中任意一个小题是填空题和问答题，则此复合题为主观题
        for (Question sub : subQuestions) {
            if (sub.type == FITB || sub.type == ESSAY) {
                return true;
            }
        }

        return false;
    }
}
