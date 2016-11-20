package com.xtuer.jms;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

public class MessageProducer {
    private JmsTemplate jmsTemplate;

    public void sendMessage(Destination destination, final String message) {
        System.out.println("---------------生产者发了一个消息：" + message);

        jmsTemplate.send(destination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(message);
            }
        });
    }

    public void sendMessage(final String message) {
        System.out.println("---------------生产者发了一个消息：" + message);

        // 消息的目的地使用字符串的队列名字表示, 而不是 Destination 对象, 这样就会省事一些
        jmsTemplate.send("default-destination", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                // Message.setIntProperty("messageType", 1) 区别消息的类型
                return session.createTextMessage(message);
            }
        });
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }
}
