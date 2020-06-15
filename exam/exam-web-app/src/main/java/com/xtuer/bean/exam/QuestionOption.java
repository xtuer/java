package com.xtuer.bean.exam;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 题目的选项
 */
@Getter
@Setter
@Accessors(chain = true)
public class QuestionOption {
    private long    id;          // 选项 ID
    private String  description; // 选项描述
    private boolean correct;     // 是否正确选项
    private int     position;    // 选项在题目中的位置
    private long    questionId;  // 所属题目 ID
    private String  mark;        // 选项序号: A, B, C, D
    private boolean deleted;     // 是否被删除

    private boolean checked; // 试卷作答: 是否已被选中
    private String  answer;  // 试卷作答: 对选项的回答
    private int     value;   // 试卷作答: 标星题的选值
}
