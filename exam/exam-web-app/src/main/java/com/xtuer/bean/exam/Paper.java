package com.xtuer.bean.exam;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 试卷
 */
@Getter
@Setter
@Accessors(chain = true)
public class Paper {
    private long    id;         // 试卷 ID
    private String  title;      // 试卷标题
    private String  info;       // 试卷介绍
    private int     type;       // 试卷类型: 0 (普通试卷)、1 (调查问卷)
    private double  totalScore; // 试卷总分
    private boolean objective;  // true (全是客观题)、false (包含主观题)
    private long    holderId;   // 拥有者 ID
    private Date    createdAt;  // 创建时间
    private Date    updatedAt;  // 更新时间
    private int questionCount;  // 题目数量

    private List<Question> questions = new LinkedList<>(); // 试卷的题目
}
