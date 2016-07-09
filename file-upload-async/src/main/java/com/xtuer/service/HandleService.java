package com.xtuer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class HandleService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 需要在新线程中运行的方法，模拟耗时任务
     */
    @Async
    public void handle(String path) {
        try {
            for (int i = 0; i < 10; ++i) {
                // 插入 DB

                System.out.println("==> " + i);
                redisTemplate.opsForValue().set("uploadStatus", "" + i);
                Thread.sleep(1000);
            }

            redisTemplate.opsForValue().set("uploadStatus", "finished");
        } catch (Exception ex) {}
    }
}
