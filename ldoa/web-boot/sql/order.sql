#--------------------------------------------------------------------------------------
# 表名: order
# 作者: 黄彪
# 日期: 2020-09-20
# 版本: 1.0
# 描述: 订单
#--------------------------------------------------------------------------------------
DROP TABLE IF EXISTS `order`;

CREATE TABLE `order` (
    order_id bigint(20) NOT NULL              COMMENT '订单 ID',
    order_sn varchar(8) NOT NULL              COMMENT '题目类型',
    customer_company varchar(256)  DEFAULT '' COMMENT '客户单位',
    customer_contact varchar(64)   DEFAULT '' COMMENT '客户联系人',
    customer_address varchar(512)  DEFAULT '' COMMENT '客户收件地址',
    salesperson_id   bigint(20) NOT NULL      COMMENT '销售员',
    calibrated       tinyint(4)    DEFAULT 0  COMMENT '是否校准，0 为否，1 为是',
    calibration_info varchar(1024) DEFAULT '' COMMENT '校准信息',
    requirement      text                     COMMENT '要求',
    attachment       varchar(128)  DEFAULT '' COMMENT '附件 URL',
    status           int(11)       DEFAULT 1  COMMENT '状态: 0 (已完成), 1 (流转中)',
    order_date       datetime NOT NULL        COMMENT '下订日期',
    delivery_date    datetime NOT NULL        COMMENT '交货日期',

    created_at datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (order_id) COMMENT '订单 ID 作为主键',
    UNIQUE KEY idx_sn (order_sn) COMMENT '订单号唯一主键'
) ENGINE=InnoDB;

#--------------------------------------------------------------------------------------
# 表名: order_item
# 作者: 黄彪
# 日期: 2020-09-20
# 版本: 1.0
# 描述: 订单项
#--------------------------------------------------------------------------------------
DROP TABLE IF EXISTS order_item;

CREATE TABLE order_item (
    order_item_id bigint(20) NOT NULL COMMENT '订单项 ID',
    order_id      bigint(20) NOT NULL COMMENT '订单 ID',
    product_id    bigint(20) NOT NULL COMMENT '产品 ID',
    count         int(11) DEFAULT 0   COMMENT '产品数量',
    comment       text                COMMENT '备注',

    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (order_item_id) COMMENT '订单项 ID 作为主键',
    KEY idx_order (order_id) COMMENT '订单 ID 建立索引'
) ENGINE=InnoDB;
