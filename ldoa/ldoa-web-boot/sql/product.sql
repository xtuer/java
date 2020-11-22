#-------------------------------------------
# 表名: product
# 作者: 公孙二狗
# 日期: 2020-09-06
# 版本: 1.0
# 描述: 产品表
#------------------------------------------
DROP TABLE IF EXISTS product;

CREATE TABLE product (
    product_id bigint(20) unsigned NOT NULL COMMENT '产品 ID',
    name     varchar(128) DEFAULT '' COMMENT '产品名称',
    code     varchar(128) DEFAULT '' COMMENT '产品编码',
    `desc`   varchar(512) DEFAULT '' COMMENT '产品描述',
    model    varchar(128) DEFAULT '' COMMENT '产品规格/型号',

    created_at datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (product_id)
) ENGINE=InnoDB COMMENT '产品表';

#-------------------------------------------
# 表名: product_item
# 作者: 公孙二狗
# 日期: 2020-09-06
# 版本: 1.0
# 描述: 产品项表，又叫物料
#------------------------------------------
DROP TABLE IF EXISTS product_item;

CREATE TABLE product_item (
    product_item_id bigint(20) unsigned NOT NULL COMMENT '产品项 ID',
    name     varchar(128) DEFAULT '' COMMENT '物料名称',
    code     varchar(128) DEFAULT '' COMMENT '物料编码',
    type     varchar(128) DEFAULT '' COMMENT '物料类型',
    `desc`   varchar(512) DEFAULT '' COMMENT '物料描述',
    model    varchar(128) DEFAULT '' COMMENT '物料规格/型号',
    standard varchar(128) DEFAULT '' COMMENT '标准/规范',
    material varchar(128) DEFAULT '' COMMENT '材质',
    unit     varchar(128) DEFAULT '' COMMENT '单位',
    count    int(11)      DEFAULT 0  COMMENT '数量',
    warn_count int(11)    DEFAULT 10 COMMENT '库存告警数量',

    created_at datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (product_item_id)
) ENGINE=InnoDB COMMENT '产品项表';

#-------------------------------------------
# 表名: product_with_item
# 作者: 公孙二狗
# 日期: 2020-09-06
# 版本: 1.0
# 描述: 产品及产品项关联表
#------------------------------------------
DROP TABLE IF EXISTS product_with_item;

CREATE TABLE product_with_item (
    product_id      bigint(20) unsigned NOT NULL COMMENT '产品 ID',
    product_item_id bigint(20) unsigned NOT NULL COMMENT '产品项 ID',
    product_item_count int(11) DEFAULT 0         COMMENT '产品项数量',

    created_at datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY idx_pi (product_id, product_item_id) COMMENT '产品及产品项唯一',

    id int(11) PRIMARY KEY AUTO_INCREMENT COMMENT '无意义自增 ID'
) ENGINE=InnoDB COMMENT '产品及产品项关联表';
