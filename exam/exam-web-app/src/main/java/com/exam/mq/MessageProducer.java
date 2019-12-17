package com.exam.mq;

import com.alibaba.fastjson.JSON;
import com.exam.bean.exam.QuestionAnswers;
import org.springframework.jms.core.JmsTemplate;

import javax.annotation.Resource;
import javax.jms.Destination;

/**
 * MQ 的消息生产者
 */
public class MessageProducer {
    @Resource(name = "jmsTemplate")
    private JmsTemplate jmsTemplate; // 发送同步消息

    @Resource(name = "jmsTemplateAsync")
    private JmsTemplate jmsTemplateAsync; // 发送异步消息

    @Resource(name = "questionAnswersQueue")
    private Destination questionAnswersQueue; // 题目作答队列

    @Resource(name = "performanceTestQueue")
    private Destination performanceTestQueue; // 性能测试队列

    /**
     * 发送考试记录作答的消息
     *
     * @param questionAnswers 考试记录的作答
     */
    public void sendAnswerQuestionsMessage(QuestionAnswers questionAnswers) {
        jmsTemplate.send(questionAnswersQueue, session -> {
            return session.createTextMessage(JSON.toJSONString(questionAnswers));
        });
    }

    /**
     * 发送考试记录作答的消息
     *
     * @param questionAnswers 考试记录的作答
     */
    public void sendAnswerQuestionsMessageAsync(QuestionAnswers questionAnswers) {
        jmsTemplateAsync.send(questionAnswersQueue, session -> {
            return session.createTextMessage(JSON.toJSONString(questionAnswers));
        });
    }

    /**
     * 发送性能测试的消息
     */
    public void sendPerformanceTestMessage() {
        jmsTemplate.send(performanceTestQueue, session -> {
            return session.createTextMessage("Post with 81 votes and 1707 views");
        });
    }

    /**
     * 发送性能测试的消息
     */
    public void sendPerformanceTestMessageAsync() {
        jmsTemplateAsync.send(performanceTestQueue, session -> {
            return session.createTextMessage("Post with 81 votes and 1707 views");
        });
    }
}
