package com.exam.bean.exam;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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

    private boolean checked; // 试卷: 是否已被选中
    private String  answer;  // 试卷: 对选项的回答
}
