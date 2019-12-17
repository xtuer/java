package com.exam.controller;

import com.exam.bean.Result;
import com.exam.mq.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 性能测试的控制器
 */
@RestController
public class PerformanceTestController {
    @Autowired
    private MessageProducer messageProducer;

    @PostMapping("/api/mq/performance-test")
    public Result<String> testMq() {
        messageProducer.sendPerformanceTestMessage();
        return Result.ok();
    }
}
