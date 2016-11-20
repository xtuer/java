package com.xtuer.controller;

import com.xtuer.jms.MessageProducer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.jms.Destination;

@Controller
public class ProducerController {
    @Resource(name = "queueDestination")
    private Destination queueDestination;

    @Resource(name = "topicDestination")
    private Destination topicDestination;

    @Resource(name = "messageProducer")
    private MessageProducer producer;

    @GetMapping("/test-queue")
    @ResponseBody
    public String testQueue() {
        String message = "Queue: " + System.nanoTime();
        producer.sendMessage(queueDestination, message);

        return message;
    }

    @GetMapping("/test-topic")
    @ResponseBody
    public String testTopic() {
        String message = "Topic: " + System.nanoTime();
        producer.sendMessage(topicDestination, message);

        return message;
    }
}
