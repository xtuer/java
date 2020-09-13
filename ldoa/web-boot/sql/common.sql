#-------------------------------------------
# 表名: sequence
# 作者: 公孙二狗
# 日期: 2020-09-06
# 版本: 1.0
# 描述: 序列号表 (不同的序列号名字使用不同的序列号)
#------------------------------------------
DROP TABLE IF EXISTS sequence;

CREATE TABLE sequence (
    name  varchar(128) NOT NULL COMMENT '序列号名字',
    value int          NOT NULL COMMENT '序列号的值',

    created_at datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (name)
) ENGINE=InnoDB COMMENT '序列号表';
