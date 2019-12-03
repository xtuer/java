package com.exam.mq;

import com.alibaba.fastjson.JSON;
import com.exam.bean.exam.ExamRecordAnswer;
import org.springframework.jms.core.JmsTemplate;

import javax.annotation.Resource;
import javax.jms.Destination;

/**
 * MQ 的消息生产者
 */
public class MessageProducer {
    @Resource(name = "jmsTemplate")
    private JmsTemplate jmsTemplate;

    @Resource(name = "answerExamRecordQueue")
    private Destination answerExamRecordQueue; // 考试记录作答队列

    /**
     * 发送考试记录作答的消息
     *
     * @param examRecordAnswer 考试记录的作答
     */
    public void sendAnswerExamRecordMessage(ExamRecordAnswer examRecordAnswer) {
        jmsTemplate.send(answerExamRecordQueue, session -> {
            return session.createTextMessage(JSON.toJSONString(examRecordAnswer));
        });
    }
}
