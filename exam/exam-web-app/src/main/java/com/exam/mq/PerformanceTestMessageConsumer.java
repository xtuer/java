package com.exam.mq;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 性能测试消息的消费者
 */
public class PerformanceTestMessageConsumer implements MessageListener {
    public void onMessage(Message message) {
        // 这里我们知道生产者发送的就是一个纯文本消息，所以这里可以直接进行强制转换
        TextMessage textMsg = (TextMessage) message;
    }
}
