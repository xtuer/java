package com.ndt.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ndt.util.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Task 用于任务的执行，保存任务的执行时间、状态、参数、所属执行等。子任务继承他，实现 execute() 方法。
 *
 * Task (任务): 通常表示一个过程
 * Job  (作业): 通常表示用户提交的一个任务 (目标)，期望明确的结果，job 可以包含多个 task，可以认为 task 是一个 JobHandler
 */
@Getter
@Setter
@JsonIgnoreProperties({ "nextTaskIdsJson", "successPreviousTaskIdsJson", "paramsJson" })
public class Task {
    // 任务的状态
    public enum Status {
        READY, EXECUTING, SUCCESS, FAILED, CANCELED
    };

    private long taskId;         // 任务 ID
    private long jobId;          // Job ID
    private long executionId;    // 执行 ID，Job 的每一次执行都有自己的 executionId
    private Date    startAt;     // 开始时间
    private Date    endAt;       // 结束时间
    private String  log;         // 执行日志
    private Status  status;      // 任务状态: READY (就绪)、EXECUTING (执行中)、SUCCESS (成功)、FAILED (失败)、CANCELED (取消)
    private boolean auto = true; // true (自动任务)、false (手动任务)
    private int     inDegree;    // 入度 (前驱任务数量)
    private List<Long> nextTaskIds            = new LinkedList<>(); // 邻接后续任务 IDs
    private List<Long> successPreviousTaskIds = new LinkedList<>(); // 成功的前驱任务 IDs
    private Map<String, String> params        = new HashMap<>();    // 任务执行的参数

    private String nextTaskIdsJson;            // 邻接后续任务 IDs 的 JSON 字符串表示，仅操作数据库时使用
    private String successPreviousTaskIdsJson; // 成功的前驱任务 IDs 的 JSON 字符串表示，仅操作数据库时使用
    private String paramsJson;                 // 任务执行的参数的 JSON 字符串表示，仅操作数据库时使用
    private String taskClassName; // 具体执行任务的类名，继承自 Task，任务执行时会自动把 Task 的属性复制给他，重写 execute() 方法执行任务逻辑

    public Task() {

    }

    public Task(long jobId, long taskId, int inDegree, String taskClassName) {
        this.jobId    = jobId;
        this.taskId   = taskId;
        this.inDegree = inDegree;
        this.taskClassName = taskClassName;
    }

    /**
     * 任务的执行逻辑，抛异常时表示任务执行失败
     */
    public void execute() throws Exception {
        // 重写实现自己的任务逻辑
    }

    /**
     * 设置入度
     *
     * @param inDegree 入度
     */
    public void setInDegree(int inDegree) {
        if (inDegree >= 0) {
            this.inDegree = inDegree;
        }
    }

    /**
     * 判断任务是否可以执行
     *
     * @return 可执行返回 true，否则返回 false
     */
    public boolean isExecutable() {
        return status == Status.READY || status == Status.FAILED;
    }

    // 提示: 从数据库查询时调用
    public String getNextTaskIdsJson() {
        return Utils.toJson(nextTaskIds);
    }

    // 提示: 保存到数据库时调用
    public void setNextTaskIdsJson(String nextTaskIdsJson) {
        this.nextTaskIdsJson = nextTaskIdsJson;
        this.nextTaskIds = Utils.fromJson(nextTaskIdsJson, new TypeReference<List<Long>>() {});
        this.nextTaskIds = Optional.ofNullable(this.nextTaskIds).orElse(new LinkedList<>());
    }

    // 提示: 从数据库查询时调用
    public String getSuccessPreviousTaskIdsJson() {
        return Utils.toJson(successPreviousTaskIds);
    }

    // 提示: 保存到数据库时调用
    public void setSuccessPreviousTaskIdsJson(String successPreviousTaskIdsJson) {
        this.successPreviousTaskIdsJson = successPreviousTaskIdsJson;
        this.successPreviousTaskIds = Utils.fromJson(successPreviousTaskIdsJson, new TypeReference<List<Long>>() {});
        this.successPreviousTaskIds = Optional.ofNullable(this.successPreviousTaskIds).orElse(new LinkedList<>());
    }

    // 提示: 从数据库查询时调用
    public String getParamsJson() {
        return Utils.toJson(paramsJson);
    }

    // 提示: 保存到数据库时调用
    public void setParamsJson(String paramsJson) {
        this.paramsJson = paramsJson;
        this.params = Utils.fromJson(nextTaskIdsJson, new TypeReference<Map<String, String>>() {});
        this.params = Optional.ofNullable(this.params).orElse(new HashMap<>());
    }
}
