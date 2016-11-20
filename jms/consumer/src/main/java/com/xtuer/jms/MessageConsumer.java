package com.xtuer.jms;

import com.alibaba.fastjson.JSON;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class MessageConsumer implements MessageListener {
    public void onMessage(Message message) {
        // 这里我们知道生产者发送的就是一个纯文本消息，所以这里可以直接进行强制转换
        TextMessage textMsg = (TextMessage) message;

        try {
            // System.out.println(JSON.toJSONString(textMsg));
            System.out.println("接收到一个纯文本消息：" + textMsg.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
