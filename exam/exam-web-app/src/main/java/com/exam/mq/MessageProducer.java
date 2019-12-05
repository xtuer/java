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

    @Resource(name = "questionAnswersQueue")
    private Destination questionAnswersQueue; // 题目作答队列

    /**
     * 发送考试记录作答的消息
     *
     * @param examRecordAnswer 考试记录的作答
     */
    public void sendAnswerQuestionsMessage(ExamRecordAnswer examRecordAnswer) {
        jmsTemplate.send(questionAnswersQueue, session -> {
            return session.createTextMessage(JSON.toJSONString(examRecordAnswer));
        });
    }
}
