package com.ndt.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ndt.util.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Job 用于任务编排，设定每个 Task 的前驱后继，执行计划等
 *
 * Task (任务): 通常表示一个过程
 * Job  (作业): 通常表示用户提交的一个任务 (目标)，期望明确的结果，job 可以包含多个 task，可以认为 task 是一个 JobHandler
 */
@Getter
@Setter
@JsonIgnoreProperties({ "tasksJson" })
public class Job {
    private long       jobId;  // Job ID
    private boolean    simple; // 为 true 则为简单任务 (直接执行)，为 false 则为需要使用 cron 定义 trigger 的任务
    private String     cron;   // Quartz cron 表达式
    private List<Task> tasks = new LinkedList<>(); // Job 的任务
    private String     tasksJson;                  // 任务的 JSON 字符串表示，仅操作数据库时使用

    // 提示: 保存到数据库时调用
    public String getTasksJson() {
        return Utils.toJson(tasks);
    }

    // 提示: 从数据库查询时调用
    public void setTasksJson(String tasksJson) {
        this.tasksJson = tasksJson;
        this.tasks = Utils.fromJson(tasksJson, new TypeReference<List<Task>>() {});
        this.tasks = Optional.ofNullable(this.tasks).orElse(new LinkedList<>());
    }
}
