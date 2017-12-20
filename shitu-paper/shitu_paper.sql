# ------------------------------------------------------------
# 无意义自增 ID 是为了 MySQL 存储时能够使用索引，提高效率
# 为了能够分布式同步数据，数据的 ID 使用 UUID，UUID 36 的长度就足够了
# 表没有指定 encoding，是为了能够使用数据库的 encoding
#
# is_deleted: 1 表示已经删除，0 为没有删除，有些敏感数据不希望直接从数据库删除，故使用 is_deleted 来标记是否已经删除
# ------------------------------------------------------------

# ------------------------------------------------------------
# 试卷
# ------------------------------------------------------------
DROP TABLE IF EXISTS paper;

CREATE TABLE paper (
    id int(11) NOT NULL AUTO_INCREMENT    COMMENT '无意义自增 id',
    paper_id char(64) NOT NULL DEFAULT '' COMMENT '试卷 id',
    name varchar(512) NOT NULL DEFAULT '' COMMENT '试卷显示的名字',
    uuid_name char(128) NOT NULL          COMMENT '试卷文件的名字，使用 uuid，如 71CC8B1A-AFFD-4F4F-8B7A-D9B466EB915D.doc',
    original_name varchar(512) NOT NULL DEFAULT ''       COMMENT '试卷原来的名字，如 xyz.doc',
    paper_directory_id char(64) NOT NULL DEFAULT '0'     COMMENT '试卷所在逻辑目录的 id',
    real_directory_name varchar(512) NOT NULL DEFAULT '' COMMENT '试卷在文件系统上目录的名字',
    subject varchar(256) NOT NULL         COMMENT '学科，如高中语文',
    publish_year varchar(64) DEFAULT ''   COMMENT '发布时间',
    status tinyint(4) DEFAULT '0'         COMMENT '审核状态: 0: 未处理(默认值)，1: 已通过，2: 未通过',
    region varchar(128) DEFAULT ''        COMMENT '所属地区，如江苏',
    paper_from varchar(128) DEFAULT ''    COMMENT '来源，如江苏省启东中学',
    paper_type varchar(128) DEFAULT ''    COMMENT '类型，如高考模拟卷',
    tenant_code varchar(128) DEFAULT NULL COMMENT '电子书包中租户的 code',
    description tinytext,
    is_deleted tinyint(4) DEFAULT '0',
    original_paper_id varchar(128) DEFAULT NULL COMMENT '乐教乐学中的试卷 id',
    created_time datetime DEFAULT NULL          COMMENT '乐教乐学中的试卷的创建时间',
    modified_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY tenant_paper (paper_id,tenant_code) COMMENT '租户下的试卷唯一'
) ENGINE=InnoDB;


# ------------------------------------------------------------
# 试卷的逻辑目录:
#     试卷的目录有逻辑目录，试卷文件在文件系统中的目录:
#         逻辑目录用于管理系统中界面显示
#         文件系统中的目录用于试卷的文件存储
# ------------------------------------------------------------
DROP TABLE IF EXISTS paper_directory;

CREATE TABLE paper_directory (
    id int(11) NOT NULL AUTO_INCREMENT                      COMMENT '无意义自增 id',
    paper_directory_id char(64) NOT NULL DEFAULT ''         COMMENT '试卷逻辑目录 id',
    name varchar(512) NOT NULL DEFAULT ''                   COMMENT '目录的名字',
    parent_paper_directory_id char(64) NOT NULL DEFAULT ''  COMMENT '父目录 id',
    tenant_code varchar(128) DEFAULT NULL                   COMMENT '电子书包中租户的 code',
    is_deleted tinyint(4) DEFAULT '0',
    modified_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY tenant_directory (paper_directory_id, tenant_code) COMMENT '租户下的试卷目录唯一'
) ENGINE=InnoDB;


# ------------------------------------------------------------
# 知识点: 知识点可以有子知识点，组成一颗树
# ------------------------------------------------------------
DROP TABLE IF EXISTS knowledge_point;

CREATE TABLE knowledge_point (
    id int(11) NOT NULL AUTO_INCREMENT                      COMMENT '无意义自增 id',
    knowledge_point_id char(64) NOT NULL DEFAULT ''         COMMENT '知识点 id，一般使用 uuid',
    name varchar(256) NOT NULL DEFAULT ''                   COMMENT '知识点',
    parent_knowledge_point_id char(64) NOT NULL DEFAULT '0' COMMENT '父知识点 id',
    type tinyint(4) DEFAULT '1'                             COMMENT '1 为知识点, 0 为知识点分类',
    tenant_code varchar(128) DEFAULT NULL                   COMMENT '电子书包中租户的 code',
    is_deleted tinyint(4) NOT NULL DEFAULT '0'              COMMENT '1 表示已经删除，0 为没有删除',
    modified_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY tenant_knowledge_point (knowledge_point_id, tenant_code) COMMENT '租户下的知识点唯一'
) ENGINE=InnoDB;


# ------------------------------------------------------------
# 试卷和它的知识点的关系表
# ------------------------------------------------------------
DROP TABLE IF EXISTS paper_knowledge_point_relation;

CREATE TABLE paper_knowledge_point_relation (
    id int(11) NOT NULL AUTO_INCREMENT              COMMENT '无意义自增 id',
    paper_id char(64) NOT NULL DEFAULT ''           COMMENT '试卷 id',
    knowledge_point_id char(64) NOT NULL DEFAULT '' COMMENT '知识点 id',
    tenant_code varchar(128) DEFAULT NULL           COMMENT '电子书包中租户的 code',
    modified_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY tenant_paper_knowledge_point_relation (paper_id, knowledge_point_id, tenant_code) COMMENT '租户下的试卷+知识点唯一'
) ENGINE=InnoDB;


# ------------------------------------------------------------
# 试卷和它的知识点的 view: 使用 view 是为了查询方便
# ------------------------------------------------------------
DROP VIEW IF EXISTS view_paper_knowledge_point;

CREATE VIEW view_paper_knowledge_point
AS SELECT
    pkpr.paper_id AS paper_id,
    kp.knowledge_point_id AS knowledge_point_id,
    kp.name AS name,
    kp.tenant_code AS tenant_code
FROM paper_knowledge_point_relation pkpr
LEFT JOIN knowledge_point kp ON pkpr.knowledge_point_id = kp.knowledge_point_id
WHERE kp.is_deleted=0;

# ------------------------------------------------------------
# 单题的知识点
# ------------------------------------------------------------

DROP TABLE IF EXISTS `question_knowledge_point`;

CREATE TABLE `question_knowledge_point` (
  `id` bigint(11) unsigned NOT NULL   COMMENT '知识点的 id',
  `parent_id` bigint(11) DEFAULT NULL COMMENT '父知识点的 id',
  `name` varchar(1024) DEFAULT NULL   COMMENT '知识点的名字',
  `code` varchar(1024) DEFAULT NULL   COMMENT '知识点的编码',
  `subject_name` varchar(1024) DEFAULT NULL COMMENT '所属学科的名字',
  `subject_code` varchar(1024) DEFAULT NULL COMMENT '所属学科的编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;
