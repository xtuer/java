#-------------------------------------------
# 表名: audit
# 作者: 公孙二狗
# 日期: 2020-10-18
# 版本: 1.0
# 描述: 审批
#------------------------------------------
DROP TABLE IF EXISTS audit;

CREATE TABLE audit (
    type         varchar(64) NOT NULL         COMMENT '审批类型',
    audit_id     bigint(20) unsigned NOT NULL COMMENT '审批 ID',
    applicant_id bigint(20) unsigned NOT NULL COMMENT '审批申请人的 ID',
    target_id    bigint(20) unsigned NOT NULL COMMENT '审批目标的 ID',
    target_json  mediumtext                   COMMENT '审批目标对象的 JSON 内容，方便前端转为响应对象进行展示',
    state        int(11)    DEFAULT 0         COMMENT '审批状态: 0 (初始化), 1 (拒绝), 2 (通过)',
    `desc`       text                         COMMENT '审批的简要描述',

    created_at datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (audit_id),
    UNIQUE KEY index_target (target_id) COMMENT '一个对象只能有一个审批'
) ENGINE=InnoDB COMMENT '审批';

#-------------------------------------------
# 表名: audit_step
# 作者: 公孙二狗
# 日期: 2020-10-18
# 版本: 1.0
# 描述: 审批项
#------------------------------------------
DROP TABLE IF EXISTS audit_step;

CREATE TABLE audit_step (
    type          varchar(64) NOT NULL         COMMENT '审批类型',
    audit_id      bigint(20) unsigned NOT NULL COMMENT '审批 ID',
    step          int(11)    DEFAULT 0         COMMENT '审批的阶段，每个阶段有多个候选审批员，当只会选择一个来审批',
    applicant_id  bigint(20) unsigned NOT NULL COMMENT '审批申请人的 ID',
    target_id     bigint(20) unsigned NOT NULL COMMENT '审批目标的 ID',
    auditor_id    bigint(20) DEFAULT 0         COMMENT '审批员 ID',
    state         int(11)    DEFAULT 0         COMMENT '审批状态: 0 (初始化), 1 (待审批), 2 (拒绝), 3 (通过)',
    comment       text                         COMMENT '审批意见',
    attachment_id bigint(20) DEFAULT 0         COMMENT '附件 ID',

    processed_at datetime COMMENT '处理时间',
    created_at   datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at   timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    id int(11) PRIMARY KEY AUTO_INCREMENT COMMENT '自增 ID',
    UNIQUE KEY idx_audit_step (audit_id, step) COMMENT '审批中的 step 不能重复',
    KEY index_auditor (auditor_id, state) COMMENT '按审批员进行索引',
    KEY index_target (target_id) COMMENT '按审批目标进行索引'
) ENGINE=InnoDB COMMENT '审批阶段';

#-------------------------------------------
# 表名: audit_config
# 作者: 公孙二狗
# 日期: 2020-10-18
# 版本: 1.0
# 描述: 审批配置
#------------------------------------------
DROP TABLE IF EXISTS audit_config;

CREATE TABLE audit_config (
    type varchar(64) NOT NULL COMMENT '审批类型',
    content_json mediumtext   COMMENT '审批配置的 JSON',

    created_at datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (type)
) ENGINE=InnoDB COMMENT '审批配置';
