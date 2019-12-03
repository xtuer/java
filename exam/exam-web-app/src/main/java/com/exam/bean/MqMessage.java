package com.exam.bean;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

/**
 * MQ 消息对象
 */
@Getter
@Setter
public class MqMessage {
    public static final int ANSWER_EXAM_RECORD = 1; // 考试记录的作答

    private int type; // 消息类型: 1 (为考试记录的作答)
    private JSONObject content = new JSONObject(); // 消息内容

    public MqMessage() {

    }

    public MqMessage(int type, JSONObject content) {
        this.type = type;
        this.content = content;
    }
}
