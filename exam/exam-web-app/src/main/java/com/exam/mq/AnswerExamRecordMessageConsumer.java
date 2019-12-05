package com.exam.mq;

import com.alibaba.fastjson.JSON;
import com.exam.bean.exam.ExamRecordAnswer;
import com.exam.service.exam.ExamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * MQ 的消费者: 考试记录作答消息的消费者
 */
@Slf4j
public class AnswerExamRecordMessageConsumer implements MessageListener {
    @Autowired
    private ExamService examService;

    public void onMessage(Message message) {
        // 这里我们知道生产者发送的就是一个纯文本消息，所以这里可以直接进行强制转换
        TextMessage textMsg = (TextMessage) message;

        try {
            ExamRecordAnswer examRecordAnswer = JSON.parseObject(textMsg.getText(), ExamRecordAnswer.class);
            examService.answerQuestions(examRecordAnswer);
        } catch (JMSException e) {
            log.warn(ExceptionUtils.getStackTrace(e));
        }
    }
}
