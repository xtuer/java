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

#-------------------------------------------
# 表名：uploaded_file
# 作者：黄彪
# 日期：2019-07-07
# 版本：1.0
# 描述：上传的文件信息表
#------------------------------------------
DROP TABLE IF EXISTS uploaded_file;

CREATE TABLE uploaded_file (
    id       bigint(20)   NOT NULL  COMMENT '每个上传的文件都有一个唯一的 ID',
    filename varchar(256) NOT NULL  COMMENT '文件的原始名字',
    url      varchar(256) NOT NULL  COMMENT '访问文件的 URL',
    type     int(11)      DEFAULT 0 COMMENT '文件的类型: 0 (临时文件), 1 (系统管理员上传的文件), 2 (老师上传的文件), 3 (学生上传的文件)',
    user_id  bigint(20)   DEFAULT 0 COMMENT '上传文件的用户 ID',

    created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id) COMMENT '文件的 ID 作为主键'
) ENGINE=InnoDB;

#-------------------------------------------
# 表名：disk
# 作者：黄彪
# 日期：2020-12-12
# 版本：1.0
# 描述：网盘
#------------------------------------------
DROP TABLE IF EXISTS disk;

CREATE TABLE disk (
    file_id bigint(20) NOT NULL  COMMENT '网盘中文件的 ID',
    created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (file_id) COMMENT '文件的 ID 作为主键'
) ENGINE=InnoDB;

#-------------------------------------------
# 表名：table_config
# 作者：黄彪
# 日期：2021-02-25
# 版本：1.0
# 描述：表格配置
#------------------------------------------
DROP TABLE IF EXISTS table_config;

CREATE TABLE table_config (
    table_name varchar(256) NOT NULL  COMMENT '表名',
    user_id    bigint(20)   DEFAULT 0 COMMENT '用户 ID',
    config     text                   COMMENT '表格配置，例如某个列的宽度',

    PRIMARY KEY (table_name, user_id) COMMENT '用户的表格配置唯一'
) ENGINE=InnoDB;
