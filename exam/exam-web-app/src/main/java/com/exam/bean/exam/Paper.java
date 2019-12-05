package com.exam.bean.exam;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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
    private int     type;       // 试卷类型: 0 (普通试卷)、1 (调查问卷)
    private double  totalScore; // 试卷总分
    private boolean objective;  // true (全是客观题)、false (包含主观题)
    private long    orgId;      // 机构 ID

    private List<Question> questions = new LinkedList<>(); // 试卷的题目
}
