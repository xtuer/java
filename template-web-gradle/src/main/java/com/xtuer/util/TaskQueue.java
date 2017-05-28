package com.xtuer.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 任务队列，同时执行任务的数量由构造函数的参数 concurrentTaskCount 指定。
 */
public class TaskQueue {
    private ExecutorService executor;

    /**
     * 创建任务队列，concurrentTaskCount 指定同时执行任务的数量。
     * 有些情况下任务需要排队一个执行完后再执行另一个，此时 concurrentTaskCount 传入 1。
     *
     * @param concurrentTaskCount 同时执行任务的数量
     */
    public TaskQueue(int concurrentTaskCount) {
        executor = Executors.newFixedThreadPool(concurrentTaskCount);
    }

    /**
     * 添加任务，根据不同的业务逻辑定义一个任务类，继承自 Runnable，
     * 可以在属性中存储任务相关的数据，在 run() 中实现任务逻辑。
     * 当然也可以重载 addTask() 函数实现添加不同的任务。
     *
     * @param task
     */
    public void addTask(Runnable task) {
        executor.submit(task);
    }

    /**
     * 下面的实现是为了测试使用
     *
     * @param n 任务内容
     * @param delay 任务消耗的时间，单位为秒，为了测试用的
     */
    public void addTask(int n, int delay) {
        addTask(() -> {
            // 模拟任务执行消耗时间
            try {
                Thread.sleep(delay * 1000);
            } catch (InterruptedException e) {
            }

            System.out.println(n + " started at " + System.currentTimeMillis() + " and elapsed " + delay * 1000);
        });
    }

    /**
     * 销毁任务队列，不再接受新的任务。
     * Spring bean 的 destroy-method 函数。
     */
    public void destroy() {
        executor.shutdown();
    }

    public static void main(String[] args) throws Exception {
        TaskQueue taskQueue = new TaskQueue(1);

        taskQueue.addTask(1, 1);
        taskQueue.addTask(2, 1);
        taskQueue.addTask(3, 1);
        taskQueue.addTask(4, 1);
        taskQueue.addTask(5, 1);

        taskQueue.destroy();
    }
}
