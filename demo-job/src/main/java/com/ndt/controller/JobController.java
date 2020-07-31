package com.ndt.controller;

import com.ndt.bean.Job;
import com.ndt.bean.Result;
import com.ndt.bean.Task;
import com.ndt.mapper.JobMapper;
import com.ndt.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobController {
    @Autowired
    public JobMapper jobMapper;

    @Autowired
    public JobService jobService;

    /**
     * 创建 Job
     *
     * 网址: http://localhost:8080/api/jobs
     * 参数: 无
     *
     * @return payload 为 job
     */
    @PostMapping("/api/jobs")
    public Result<Job> upsertJob() {
        Job job = new Job();
        job.setJobId(1);

        Task task1 = new Task(job.getJobId(), 1, 0, "com.ndt.bean.DemoTask");
        Task task2 = new Task(job.getJobId(), 2, 0, "com.ndt.bean.DemoTask");
        Task task3 = new Task(job.getJobId(), 3, 2, "com.ndt.bean.DemoTask");
        Task task4 = new Task(job.getJobId(), 4, 0, "com.ndt.bean.DemoTask");
        Task task5 = new Task(job.getJobId(), 5, 1, "com.ndt.bean.DemoTask");
        Task task6 = new Task(job.getJobId(), 6, 2, "com.ndt.bean.DemoTask");
        Task task7 = new Task(job.getJobId(), 7, 1, "com.ndt.bean.DemoTask");
        Task task8 = new Task(job.getJobId(), 8, 1, "com.ndt.bean.DemoTask");
        Task task9 = new Task(job.getJobId(), 9, 2, "com.ndt.bean.DemoTask");

        // 建立 task 的后继关系 (有向无环图)
        task1.getNextTaskIds().add(task3.getTaskId());
        task2.getNextTaskIds().add(task3.getTaskId());
        task3.getNextTaskIds().add(task6.getTaskId());
        task4.getNextTaskIds().add(task5.getTaskId());
        task5.getNextTaskIds().add(task6.getTaskId());
        task6.getNextTaskIds().add(task7.getTaskId());
        task6.getNextTaskIds().add(task8.getTaskId());
        task7.getNextTaskIds().add(task9.getTaskId());
        task8.getNextTaskIds().add(task9.getTaskId());

        job.getTasks().add(task1);
        job.getTasks().add(task2);
        job.getTasks().add(task3);
        job.getTasks().add(task4);
        job.getTasks().add(task5);
        job.getTasks().add(task6);
        job.getTasks().add(task7);
        job.getTasks().add(task8);
        job.getTasks().add(task9);

        jobMapper.upsertJob(job);

        return Result.ok(job);
    }

    /**
     * Job 执行前准备
     *
     * 网址: http://localhost:8080/api/jobs/{jobId}/prepare
     * 参数: 无
     */
    @PostMapping("/api/jobs/{jobId}/prepare")
    public Result<Long> prepareToExecuteJob(@PathVariable long jobId) {
        long executionId = jobService.executeJobPrepare(jobId);
        return Result.ok(executionId);
    }

    /**
     * 执行 Job
     *
     * 网址: http://localhost:8080/api/jobs/{jobId}/executions/{executionId}/execute
     * 参数: 无
     *
     * @param jobId       Job ID
     * @param executionId 执行 ID
     */
    @PostMapping("/api/jobs/{jobId}/executions/{executionId}/execute")
    public Result<Boolean> executeJob(@PathVariable long jobId, @PathVariable long executionId) {
        jobService.executeJob(jobId, executionId);
        return Result.ok();
    }
}
