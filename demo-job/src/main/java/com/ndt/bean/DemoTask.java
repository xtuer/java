package com.ndt.bean;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DemoTask extends Task {
    @Override
    public void execute() throws Exception {
        // 随机等待，模拟任务执行耗时
        Random random = new Random();
        int s = random.nextInt(10) + 2;
        // s = 3;

        System.out.println("Task.execute(): JobId: " + getJobId()
                + ", TaskId: " + getTaskId()
                + ", ExecutionId: " + getExecutionId()
                + ", Sleep " + s + "s");

        TimeUnit.SECONDS.sleep(s);
    }
}
