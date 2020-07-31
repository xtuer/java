package com.ndt.mapper;

import com.ndt.bean.Job;
import com.ndt.bean.Task;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface JobMapper {
    /**
     * 查询 Job
     *
     * @param jobId Job ID
     * @return 返回查询到的 Job，查询不到返回 null
     */
    Job findJobById(long jobId);

    /**
     * 插入或者更新 Job
     *
     * @param job Job
     */
    void upsertJob(Job job);

    /**
     * 查找执行的 Task
     *
     * @param executionId 执行 ID
     * @return 返回执行的 Task 数组
     */
    List<Task> findTasksByExecutionId(long executionId);

    /**
     * 查找执行的 Task
     *
     * @param taskId      任务 ID
     * @param executionId 执行 ID
     * @return 返回查询到的任务，查询不到返回 null
     */
    Task findTaskByTaskIdAndExecutionId(long taskId, long executionId);

    /**
     * 插入 Task
     *
     * @param task Task
     */
    void insertTask(Task task);

    /**
     * 更新任务的状态 (执行时间、状态、入度、成功的前驱)
     *
     * @param task 任务
     */
    void updateTaskStatus(Task task);

    /**
     * 更新任务的日志
     *
     * @param task 任务
     */
    void updateTaskLog(Task task);
}
