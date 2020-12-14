#-------------------------------------------
# 表名: stock_record
# 作者: 公孙二狗
# 日期: 2020-11-21
# 版本: 1.0
# 描述: 库存操作记录 (出库、入库)
#------------------------------------------
DROP TABLE IF EXISTS stock_record;

CREATE TABLE stock_record (
    stock_record_id   bigint(20)  unsigned NOT NULL COMMENT '物料 ID',
    product_item_id   bigint(20)  unsigned NOT NULL COMMENT '物料 ID',
    product_item_type varchar(64) DEFAULT ''        COMMENT '物料的类型: 成品、零件、部件',
    type              varchar(16)          NOT NULL COMMENT '库存操作类型: IN (入库), OUT (出库)',
    count             int(11)     DEFAULT 0         COMMENT '数量',
    batch             varchar(64) DEFAULT ''        COMMENT '批次',
    manufacturer      varchar(99) DEFAULT ''        COMMENT '生产厂家',
    comment           varchar(64) DEFAULT ''        COMMENT '备注',
    complete          tinyint(4)  DEFAULT 0         COMMENT '操作是否完成: 0 (未完成), 1 (完成), 入库直接标记为完成，出库申请提交后创建出库记录，当领取物料后才标记为完成',
    stock_request_id  bigint(20) unsigned          COMMENT '出库申请 ID (出库时才需要)',
    user_id           bigint(20)  unsigned NOT NULL COMMENT '用户 ID',

    created_at datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (stock_record_id)
) ENGINE=InnoDB COMMENT '库存操作记录 (出库、入库)';

#-------------------------------------------
# 表名: stock_request
# 作者: 公孙二狗
# 日期: 2020-11-21
# 版本: 1.0
# 描述: 出库操作申请
#------------------------------------------
DROP TABLE IF EXISTS stock_request;

CREATE TABLE stock_request (
    stock_request_id bigint(20) unsigned NOT NULL  COMMENT '出库申请 ID',
    type             varchar(16)         NOT NULL  COMMENT '库存操作类型: IN (入库), OUT (出库)',
    order_id         bigint(20) unsigned DEFAULT 0 COMMENT '订单号 [可选]',
    applicant_id     bigint(20) unsigned NOT NULL  COMMENT '申请者 ID',
    state            int(11)    DEFAULT 0          COMMENT '状态: 0 (初始化), 1 (待审批), 2 (审批拒绝), 3 (审批完成), 4 (完成)',
    `desc`           varchar(2048) DEFAULT ''      COMMENT '描述，展示时需要使用',

    created_at datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (stock_request_id)
) ENGINE=InnoDB COMMENT '出库申请';
