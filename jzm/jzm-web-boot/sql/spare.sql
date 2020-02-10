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
    ship_sn                varchar(256) DEFAULT '' COMMENT '芯片编号',
    ship_production_date   varchar(256) DEFAULT '' COMMENT '芯片生产时间',
    ship_aging_date        varchar(256) DEFAULT '' COMMENT '芯片老化时间',
    ship_power_consumption varchar(256) DEFAULT '' COMMENT '芯片功耗',
    firmware_version       varchar(256) DEFAULT '' COMMENT '固件版本',
    software_version       varchar(256) DEFAULT '' COMMENT '软件版本',
    ship_quantity          int(11)      DEFAULT 0  COMMENT '芯片数量',

    created_at datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (id)
) ENGINE=InnoDB;
