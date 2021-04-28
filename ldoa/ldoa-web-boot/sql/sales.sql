#--------------------------------------------------------------------------------------
# 表名: customer
# 作者: 黄彪
# 日期: 2021-04-24
# 版本: 1.0
# 描述: 客户
#--------------------------------------------------------------------------------------
DROP TABLE IF EXISTS customer;

CREATE TABLE customer (
    customer_id bigint(20)   NOT NULL COMMENT '客户 ID',
    customer_sn varchar(128) NOT NULL COMMENT '客户编码',
    name        varchar(256) NOT NULL COMMENT '客户名称',
    business    varchar(128)          COMMENT '行业',
    region      varchar(128)          COMMENT '区域',
    type        varchar(128)          COMMENT '分类',
    phone       varchar(128)          COMMENT '电话',
    level       varchar(128)          COMMENT '关系等级',
    importance  varchar(128)          COMMENT '重要程度',
    status      varchar(128)          COMMENT '状态',

    address     varchar(999)          COMMENT '地址',
    owner       varchar(256)          COMMENT '负责人',
    remark      text                  COMMENT '备注',
    contacts_json text                COMMENT '客户联系人 JSON',

    created_at datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (customer_id) COMMENT '机构 ID 作为主键',
    UNIQUE  KEY index_sn (customer_sn) COMMENT '机构域名唯一',
    KEY idx_created_at (created_at) COMMENT '创建时间建立索引'
) ENGINE=InnoDB;
