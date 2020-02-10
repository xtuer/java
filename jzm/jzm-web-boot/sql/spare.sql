#-------------------------------------------
# 表名：spare
# 作者：黄彪
# 日期：2020-02-10
# 版本：1.0
# 描述：备件
#------------------------------------------
DROP TABLE IF EXISTS `spare`;

CREATE TABLE `spare` (
    id                     bigint(20) NOT NULL     COMMENT '备件 ID',
    sn                     varchar(256) DEFAULT '' COMMENT '入库单号',
    type                   varchar(256) DEFAULT '' COMMENT '备件类型',
    chip_sn                varchar(256) DEFAULT '' COMMENT '芯片编号',
    chip_production_date   varchar(256) DEFAULT '' COMMENT '芯片生产时间',
    chip_aging_date        varchar(256) DEFAULT '' COMMENT '芯片老化时间',
    chip_power_consumption varchar(256) DEFAULT '' COMMENT '芯片功耗',
    firmware_version       varchar(256) DEFAULT '' COMMENT '固件版本',
    software_version       varchar(256) DEFAULT '' COMMENT '软件版本',
    chip_quantity          int(11)      DEFAULT 0  COMMENT '芯片数量',

    created_at datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (id)
) ENGINE=InnoDB;

#-------------------------------------------
# 表名：spare_warehousing_log
# 作者：黄彪
# 日期：2020-02-10
# 版本：1.0
# 描述：出库入库日志
#------------------------------------------
DROP TABLE IF EXISTS `spare_warehousing_log`;

CREATE TABLE `spare_warehousing_log` (
    username      varchar(256) DEFAULT '' COMMENT '用户账号',
    type          varchar(256) DEFAULT '' COMMENT '操作类型: 出库、入库',
    spare_id      bigint(20) NOT NULL     COMMENT '备件 ID',
    spare_sn      varchar(256) DEFAULT '' COMMENT '备件入库单号',
    old_chip_quantity int(11)  DEFAULT 0  COMMENT '操作前芯片数量',
    new_chip_quantity int(11)  DEFAULT 0  COMMENT '操作后芯片数量',
    quantity      int(11)      DEFAULT 0  COMMENT '出入库的数量',
    `date`        datetime  NOT NULL      COMMENT '操作时间',

    created_at datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    id int(11) PRIMARY KEY AUTO_INCREMENT COMMENT '无意义的主键 ID'
) ENGINE=InnoDB;
