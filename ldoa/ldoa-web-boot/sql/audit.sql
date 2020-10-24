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
    passed       tinyint(4) DEFAULT 0         COMMENT '是否审批通过: 0 (不通过), 1 (通过)',

    created_at datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (audit_id),
    UNIQUE KEY index_audit (target_id, type) COMMENT '一个对象只能有一个审批'
) ENGINE=InnoDB COMMENT '审批';

#-------------------------------------------
# 表名: audit_item
# 作者: 公孙二狗
# 日期: 2020-10-18
# 版本: 1.0
# 描述: 审批项
#------------------------------------------
DROP TABLE IF EXISTS audit_item;

CREATE TABLE audit_item (
    type          varchar(64) NOT NULL         COMMENT '审批类型',
    audit_id      bigint(20) unsigned NOT NULL COMMENT '审批 ID',
    audit_item_id bigint(20) unsigned NOT NULL COMMENT '审批项 ID',
    applicant_id  bigint(20) unsigned NOT NULL COMMENT '审批申请人的 ID',
    target_id     bigint(20) unsigned NOT NULL COMMENT '审批目标的 ID',
    auditor_id    bigint(20) unsigned NOT NULL COMMENT '审批的阶段，每个审批可能需要多阶段，多个人进行审批',
    step          int(11)    DEFAULT 0         COMMENT '审批的阶段，每个审批可能需要多阶段，多个人进行审批',
    status        int(11)    DEFAULT 0         COMMENT '审批状态: 0 (初始化), 1 (待审批), 2 (拒绝), 3 (通过)',
    comment       varchar(999)                 COMMENT '审批意见',

    processed_at datetime COMMENT '处理时间',
    created_at   datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at   timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (audit_item_id),
    KEY index_audit (audit_id) COMMENT '按审批进行索引',
    KEY index_auditor (auditor_id, status) COMMENT '按审批员进行索引'
) ENGINE=InnoDB COMMENT '审批';

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
