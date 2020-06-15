package com.xtuer.bean.exam;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 对考试记录题目的作答 (用于接收前端发送来的作答数据)
 */
@Getter
@Setter
@Accessors(chain = true)
public class QuestionAnswers {
    private long userId;       // 用户 ID
    private long examRecordId; // 考试记录 ID
    private boolean submitted; // 是否提交试卷
    private Date createdAt = new Date(); // 作答时间，使用消息队列异步保存时非常重要
    List<QuestionWithAnswer> questions;  // 题目的作答
}
