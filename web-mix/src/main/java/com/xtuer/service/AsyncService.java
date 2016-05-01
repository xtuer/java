package com.xtuer.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {
    /**
     * 需要在新线程中运行的方法
     */
    @Async
    public void asyncMethod() {
        try {
            for (int i = 0; i < 10; ++i) {
                System.out.println("==> " + i);
                Thread.sleep(1000);
            }
        } catch (Exception ex) {}
    }
}
