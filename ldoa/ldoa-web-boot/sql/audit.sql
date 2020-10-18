#-------------------------------------------
# 表名: audit
# 作者: 公孙二狗
# 日期: 2020-10-18
# 版本: 1.0
# 描述: 审批
#------------------------------------------
DROP TABLE IF EXISTS audit;

CREATE TABLE audit (
    name  varchar(128) NOT NULL COMMENT '序列号名字',
    value int          NOT NULL COMMENT '序列号的值',

    created_at datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (name)
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
    name  varchar(128) NOT NULL COMMENT '序列号名字',
    value int          NOT NULL COMMENT '序列号的值',

    created_at datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (name)
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
