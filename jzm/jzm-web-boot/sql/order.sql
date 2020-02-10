#-------------------------------------------
# 表名：order
# 作者：黄彪
# 日期：2020-02-08
# 版本：1.0
# 描述：订单
#------------------------------------------
DROP TABLE IF EXISTS `order`;

CREATE TABLE `order` (
    id bigint(20)  NOT NULL                  COMMENT '订单 ID',
    customer_name    varchar(256) DEFAULT '' COMMENT '客户名称',
    type             varchar(64)  DEFAULT '' COMMENT '订单类型: 订货、样品',
    brand            varchar(64)  DEFAULT '' COMMENT '品牌   : P+H、BD、其他',
    software_version varchar(256) DEFAULT '' COMMENT '软件版本',
    person_in_charge varchar(256) DEFAULT '' COMMENT '负责人',
    status           int(11)      DEFAULT 0  COMMENT '订单状态: 0 (等待备件)、1 (组装中)、2 (完成组装)',

    order_date           datetime NOT NULL   COMMENT '下单日期',
    start_assemble_date  datetime            COMMENT '开始组装日期',
    finish_assemble_date datetime            COMMENT '完成组装日期',

    created_at datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (id)
) ENGINE=InnoDB;

#-------------------------------------------
# 表名：order_item
# 作者：黄彪
# 日期：2020-02-08
# 版本：1.0
# 描述：订单
#------------------------------------------
DROP TABLE IF EXISTS `order_item`;

CREATE TABLE `order_item` (
    order_id    bigint(20)  NOT NULL    COMMENT '订单的 ID',
    id          bigint(20)  NOT NULL    COMMENT '订单项 ID',
    type        varchar(256) DEFAULT '' COMMENT '型号',
    sn          varchar(64)  DEFAULT '' COMMENT '序列号',
    chip_sn     varchar(64)  DEFAULT '' COMMENT '芯片编号',
    shell_color varchar(64)  DEFAULT '' COMMENT '外壳颜色',
    shell_batch varchar(256) DEFAULT '' COMMENT '外壳批次',
    sensor_info varchar(256) DEFAULT '' COMMENT '传感器信息',
    circle_info varchar(256) DEFAULT '' COMMENT 'Ο 型圈信息',
    status      int(11)      DEFAULT 0  COMMENT '订单状态',
    count       int(11)      DEFAULT 0  COMMENT '数量',

    created_at datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (id),
    KEY index_order (order_id) COMMENT '加速查找订单的订单项'
) ENGINE=InnoDB;
