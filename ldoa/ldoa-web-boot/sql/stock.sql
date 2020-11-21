#-------------------------------------------
# 表名: stock
# 作者: 公孙二狗
# 日期: 2020-11-21
# 版本: 1.0
# 描述: 库存
#------------------------------------------
DROP TABLE IF EXISTS stock;

CREATE TABLE stock (
    product_item_id bigint(20) unsigned NOT NULL COMMENT '物料 ID',
    count           int(11)    DEFAULT 0         COMMENT '数量',

    created_at datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (product_item_id)
) ENGINE=InnoDB COMMENT '库存';

#-------------------------------------------
# 表名: stock_record
# 作者: 公孙二狗
# 日期: 2020-11-21
# 版本: 1.0
# 描述: 库存操作记录 (出库、入库)
#------------------------------------------
DROP TABLE IF EXISTS stock_record;

CREATE TABLE stock_record (
    stock_record_id bigint(20)  unsigned NOT NULL COMMENT '物料 ID',
    product_item_id bigint(20)  unsigned NOT NULL COMMENT '物料 ID',
    type            tinyint(4)  DEFAULT 0         COMMENT '库存操作类型: 0 (入库), 1 (出库)',
    count           int(11)     DEFAULT 0         COMMENT '数量',
    batch           varchar(64) DEFAULT ''        COMMENT '批次',
    warehouse       varchar(64) DEFAULT ''        COMMENT '仓库',
    comment         varchar(64) DEFAULT ''        COMMENT '备注',
    completed       tinyint(4)  DEFAULT 0         COMMENT '操作是否完成: 0 (未完成), 1 (完成), 入库直接标记为完成，出库申请提交后创建出库记录，当领取物料后才标记为完成',
    stock_out_request_id bigint(20) unsigned      COMMENT '出库申请 ID (出库时才需要)',
    user_id         bigint(20)  unsigned NOT NULL COMMENT '用户 ID',

    created_at datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (stock_record_id)
) ENGINE=InnoDB COMMENT '库存操作记录 (出库、入库)';

#-------------------------------------------
# 表名: stock_out_request
# 作者: 公孙二狗
# 日期: 2020-11-21
# 版本: 1.0
# 描述: 出库申请
#------------------------------------------
DROP TABLE IF EXISTS stock_out_request;

CREATE TABLE stock_out_request (
    stock_out_request_id bigint(20) unsigned NOT NULL COMMENT '物料 ID',
    order_sn             varchar(64) DEFAULT ''       COMMENT '订单号 (可选)',
    status               int(11)     DEFAULT 0        COMMENT '状态: 0 (初始化), 1 (待审批), 2 (审批拒绝), 3 (审批完成), 4 (完成)',
    applicant_id         bigint(20)  unsigned NOT NULL COMMENT '用户 ID',

    created_at datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (stock_out_request_id)
) ENGINE=InnoDB COMMENT '出库申请';
