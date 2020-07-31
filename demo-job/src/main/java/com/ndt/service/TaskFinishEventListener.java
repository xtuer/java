package com.ndt.service;

import com.ndt.bean.Task;
import com.ndt.bean.TaskSuccessEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 任务执行结束的事件监听器
 */
@Component
@Slf4j
public class TaskFinishEventListener implements ApplicationListener<TaskSuccessEvent> {
    @Autowired
    private JobService jobService;

    private final ExecutorService taskThreadPool = Executors.newSingleThreadExecutor();

    @Override
    public void onApplicationEvent(TaskSuccessEvent event) {
        // 保证串行执行任务的成功操作
        taskThreadPool.submit(() -> {
            Task task = (Task) event.getSource();

            if (task.getStatus() == Task.Status.SUCCESS) {
                // [1] 任务执行成功
                // [1.1] 执行任务成功的处理
                jobService.executeTaskSuccess(task);

                // [1.2] 继续执行任务
                jobService.executeJob(task.getJobId(), task.getExecutionId());
            } else {
                // [2] 任务执行失败，更新失败的任务状态
                jobService.executeTaskFailed(task);

                // TODO: 是否需要给用户发送一个失败通知，整个 Job 结束时是否也需要发送通知
            }
        });
    }
}
