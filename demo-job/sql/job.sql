#--------------------------------------------------------------------------------------
# 表名：job
# 作者：黄彪
# 日期：2020-07-28
# 版本：1.0
# 描述：任务
#      所有任务都保存一个 JSON 字符串，服务器端进行解析
#--------------------------------------------------------------------------------------
DROP TABLE IF EXISTS job;

CREATE TABLE job (
    job_id     bigint(20) NOT NULL COMMENT 'Job ID',
    simple     tinyint DEFAULT 0   COMMENT '为 1 则为简单任务 (直接执行)，为 0 则为需要使用 cron 定义 trigger 的任务',
    cron       varchar(512)        COMMENT 'Quartz cron 表达式',
    tasks_json text                COMMENT 'Tasks 的 JSON 字符串表示',

    created_at datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (job_id) COMMENT 'Job ID 作为主键'
) ENGINE=InnoDB;

#--------------------------------------------------------------------------------------
# 表名：task
# 作者：黄彪
# 日期：2020-07-28
# 版本：1.0
# 描述：任务的执行
#--------------------------------------------------------------------------------------
DROP TABLE IF EXISTS task;

CREATE TABLE task (
    task_id         bigint(20) NOT NULL   COMMENT 'Task ID',
    job_id          bigint(20) NOT NULL   COMMENT 'Job ID',
    execution_id    bigint(20) NOT NULL   COMMENT '执行 ID',

    start_at        datetime              COMMENT '创建时间',
    end_at          datetime              COMMENT '结束时间',
    log             text                  COMMENT '执行日志',
    status          char(16)              COMMENT '任务状态: READY (就绪)、EXECUTING (执行中)、SUCCESS (成功)、FAILED (失败)、CANCELED (取消)',
    in_degree       int DEFAULT 0         COMMENT '入度',
    auto            tinyint DEFAULT 1     COMMENT '1 (自动任务)、0 (手动任务)',
    task_class_name varchar(128) NOT NULL COMMENT '具体执行任务的类名',

    next_task_ids_json             varchar(512) COMMENT '邻接后续任务 IDs 的 JSON 字符串表示',
    success_previous_task_ids_json varchar(512) COMMENT '成功的前驱任务 IDs 的 JSON 字符串表示',
    params_json                    varchar(512) COMMENT '任务执行的参数的 JSON 字符串表示',

    created_at datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    UNIQUE KEY idx_execution_task (execution_id, task_id) COMMENT '执行 ID + Task ID 唯一'
) ENGINE=InnoDB;
