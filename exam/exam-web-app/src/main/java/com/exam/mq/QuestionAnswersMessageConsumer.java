package com.exam.mq;

import com.alibaba.fastjson.JSON;
import com.exam.bean.exam.QuestionAnswers;
import com.exam.service.exam.ExamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * MQ 的消费者: 考试记录作答消息的消费者
 */
@Slf4j
public class QuestionAnswersMessageConsumer implements MessageListener {
    @Autowired
    private ExamService examService;

    public void onMessage(Message message) {
        // 这里我们知道生产者发送的就是一个纯文本消息，所以这里可以直接进行强制转换
        TextMessage textMsg = (TextMessage) message;

        try {
            QuestionAnswers questionAnswers = JSON.parseObject(textMsg.getText(), QuestionAnswers.class);
            examService.answerQuestions(questionAnswers);
        } catch (JMSException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
