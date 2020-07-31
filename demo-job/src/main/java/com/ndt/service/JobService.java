package com.ndt.service;

import com.ndt.bean.Job;
import com.ndt.bean.Task;
import com.ndt.bean.TaskSuccessEvent;
import com.ndt.mapper.JobMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 执行任务的服务。
 *
 * 在第一次执行任务时需要调用 executeJobPrepare(jobId) 创建执行记录。
 * 当执行记录存在后，就可以任意的调用 executeJob(jobId, executionId) 执行任务了，每次都会自动的执行入度为 0 的任务。
 * 还可以执行指定的任务 executeTask(taskId, executionId)
 */
@Service
@Slf4j
public class JobService {
    @Autowired
    private JobMapper jobMapper;

    @Autowired
    ApplicationContext applicationContext;

    // 执行任务的线程池
    private final ExecutorService taskThreadPool = Executors.newFixedThreadPool(10);

    /**
     * Job 执行前准备
     *
     * @param jobId Job ID
     */
    @Transactional(rollbackFor = Exception.class)
    public long executeJobPrepare(long jobId) {
        // 1. 查询 Job
        // 2. 创建 executionId，复制 Job 的 tasks 到任务表 task，其状态为 READY
        // 3. 返回 executionId

        // [1.] 查询 Job
        Job job = jobMapper.findJobById(jobId);
        if (job == null) {
            return -1;
        }

        // [2] 创建 executionId，复制 Job 的 tasks 到任务表 task，其状态为 READY
        long executionId = System.currentTimeMillis();
        for (Task task : job.getTasks()) {
            task.setExecutionId(executionId);
            task.setStatus(Task.Status.READY);
            jobMapper.insertTask(task);
        }

        // [3] 返回 executionId
        return executionId;
    }

    /**
     * 执行 Job
     *
     * @param jobId       Job ID
     * @param executionId 执行 ID
     */
    public void executeJob(long jobId, long executionId) {
        // 1. 查询 tasks
        // 2. 找到入度为 0，状态为 READY 的自动任务任务
        // 3. 异步执行 tasks
        // 4. 所有 task 执行完成，则 Job 执行完成

        // [1] 查询 tasks
        List<Task> tasks = jobMapper.findTasksByExecutionId(executionId);

        // System.out.println(Utils.toJson(tasks));

        // [2] 找到入度为 0，状态为 READY 的自动任务任务
        List<Task> executableTasks = tasks.stream()
                .filter(task -> task.getInDegree() == 0)
                .filter(task -> task.getStatus() == Task.Status.READY)
                .filter(Task::isAuto)
                .collect(Collectors.toList());

        // System.out.println(Utils.toJson(executableTasks));

        // [3] 异步执行 tasks
        executableTasks.forEach(task -> {
            executeTask(task.getTaskId(), task.getExecutionId());
        });

        // [4] 所有 task 执行完成，则 Job 执行完成
        long successCount = tasks.stream().filter(task -> task.getStatus() == Task.Status.SUCCESS).count();
        if (tasks.size() == successCount) {
            System.out.println("Job " + jobId + ", Execution " + executionId + " 执行结束");
        }
    }

    /**
     * 异步执行 Task
     */
    public void executeTask(long taskId, long executionId) {
        // 0. 只能执行 READY 或者 FAILED 的 Task
        // 1. 更新开始执行状态
        // 2. 执行 Task
        // 3. 更新任务日志
        // 4. 任务执行结束，发送执行结束的事件，在消息处理中更新任务的成功状态和处理后续任务

        taskThreadPool.submit(() -> {
            Task task     = null; // 具体执行任务的对象
            Task infoTask = jobMapper.findTaskByTaskIdAndExecutionId(taskId, executionId); // 存储任务信息的类

            // 0. 只能执行 READY 或者 FAILED 的 Task
            if (!infoTask.isExecutable()) {
                return;
            }

            try {
                task = (Task) Class.forName(infoTask.getTaskClassName()).newInstance();
                BeanUtils.copyProperties(infoTask, task);

                // [1] 更新开始执行状态
                executeTaskStart(task);

                // [2] 执行 Task
                task.execute(); // 使用自定义 task 执行任务
                task.setStatus(Task.Status.SUCCESS);
            } catch (ClassNotFoundException e) {
                task = infoTask;
                task.setLog(task.getLog() + "\n" + e.getMessage());
                log.warn("[错误] 执行任务错误: 任务的类不存在 [{}]", task.getTaskClassName());
            } catch (Exception e) {
                task.setLog(task.getLog() + "\n" + e.getMessage());
                log.warn("[错误] 执行任务错误: \n" + e.getMessage());
            }

            // [3] 更新任务日志
            jobMapper.updateTaskLog(task);

            // [4] 任务执行结束，发送执行结束的事件，在消息处理中更新任务的成功状态和处理后续任务
            // 注意: 需要使用消息队列或者分布式锁保证串行更新进行更新，
            // 否则 2 个 task 同时更新同一个后续 task 就可能会造成数据丢失 (每个 task 的执行都是在自己的线程中)
            applicationContext.publishEvent(new TaskSuccessEvent(task));
        });
    }

    /**
     * 执行任务前的处理函数: 更新开始执行状态
     *
     * @param task 任务
     */
    public void executeTaskStart(Task task) {
        task.setStatus(Task.Status.EXECUTING);
        task.setStartAt(new Date());
        task.setEndAt(null);
        jobMapper.updateTaskStatus(task);
    }

    /**
     * 任务执行成功的处理函数:
     * 1. 更新任务为成功的状态、结束时间
     * 2. 更新后继任务的入度和记录前驱任务
     *
     * @param task 任务
     */
    @Transactional(rollbackFor = Exception.class)
    public void executeTaskSuccess(Task task) {
        // 1. 查询执行的所有任务 tasks
        // 2. 找到 task 的后继任务 tasks
        // 3. 只留下后继 task 的已经完成任务列表中不包含 currentTask 的 task
        // 4. 把当前任务 ID 加到后续任务的 completedPreviousTaskIds，并且其入度减 1
        // 5. 保存到数据库

        // [0] 更新任务的成功执行状态
        task.setStatus(Task.Status.SUCCESS);
        task.setEndAt(new Date());
        jobMapper.updateTaskStatus(task);

        // [1] 查询执行的 tasks
        List<Task> tasks = jobMapper.findTasksByExecutionId(task.getExecutionId());

        tasks.stream()
                // [2] 找到 currentTask 的后继 tasks
                .filter(nt -> task.getNextTaskIds().contains(nt.getTaskId()))
                // [3] 只留下后继 task 的已经完成任务列表中不包含 currentTask 的 task
                .filter(nt -> !nt.getSuccessPreviousTaskIds().contains(task.getTaskId()))
                .forEach(nt -> {
                    // [4] 把当前任务 ID 加到后续任务的 completedPreviousTaskIds，并且其入度减 1
                    nt.getSuccessPreviousTaskIds().add(task.getTaskId());
                    nt.setInDegree(nt.getInDegree() - 1);

                    // [5] 保存到数据库
                    jobMapper.updateTaskStatus(nt);
                });
    }

    /**
     * 任务执行失败的处理函数: 更新任务为失败的状态、结束时间
     *
     * @param task 任务
     */
    public void executeTaskFailed(Task task) {
        task.setStatus(Task.Status.FAILED);
        task.setEndAt(new Date());
        jobMapper.updateTaskStatus(task);
    }
}
