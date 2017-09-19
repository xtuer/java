# ------------------------------------------------------------
# Dump of table knowledge_point
# ------------------------------------------------------------

DROP TABLE IF EXISTS knowledge_point;

CREATE TABLE knowledge_point (
    id int(11) NOT NULL AUTO_INCREMENT                      COMMENT '无意义的 id',
    knowledge_point_id char(64) NOT NULL DEFAULT ''         COMMENT '知识点的 id，一般使用 uuid',
    name varchar(256) NOT NULL DEFAULT ''                   COMMENT '知识点',
    parent_knowledge_point_id char(64) NOT NULL DEFAULT '0' COMMENT '父知识点的 id',
    type tinyint(11) DEFAULT '1'                            COMMENT '1 为知识点, 0 为知识点分类',
    tenant_code varchar(128) DEFAULT NULL                   COMMENT '电子书包中租户的 code',
    is_deleted tinyint(11) NOT NULL DEFAULT '0'             COMMENT '1 表示已经删除，0 为没有删除',
    modified_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY knowledge_point_id (knowledge_point_id)
) ENGINE=InnoDB;


# ------------------------------------------------------------
# Dump of table paper
# ------------------------------------------------------------

DROP TABLE IF EXISTS paper;

CREATE TABLE paper (
    id int(11) NOT NULL AUTO_INCREMENT    COMMENT '无意义的 id',
    paper_id char(64) NOT NULL DEFAULT '' COMMENT '试卷的 id',
    name varchar(512) NOT NULL DEFAULT '' COMMENT '试卷显示的名字',
    uuid_name char(128) NOT NULL          COMMENT '试卷文件的名字，使用 uuid，如 71CC8B1A-AFFD-4F4F-8B7A-D9B466EB915D.doc',
    original_name varchar(512) NOT NULL DEFAULT ''       COMMENT '试卷原来的名字，如 xyz.doc',
    paper_directory_id char(64) NOT NULL DEFAULT '0'     COMMENT '试卷所在逻辑目录的 id',
    real_directory_name varchar(512) NOT NULL DEFAULT '' COMMENT '试卷在文件系统上目录的名字',
    subject varchar(256) NOT NULL       COMMENT '学科，如高中语文',
    publish_year varchar(64) DEFAULT '' COMMENT '发布时间',
    status tinyint(11) DEFAULT '0'      COMMENT '审核状态: 0: 未处理(默认值)，1: 已通过，2: 未通过',
    region varchar(128) DEFAULT ''      COMMENT '所属地区，如江苏',
    paper_from varchar(128) DEFAULT ''  COMMENT '来源，如江苏省启东中学',
    paper_type varchar(128) DEFAULT ''  COMMENT '类型，如高考模拟卷',
    description tinytext,
    is_deleted tinyint(4) DEFAULT '0',
    original_paper_id varchar(128) DEFAULT NULL COMMENT '乐教乐学中的试卷 id',
    created_time datetime DEFAULT NULL,
    modified_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY paper_id (paper_id)
) ENGINE=InnoDB;


# ------------------------------------------------------------
# Dump of table paper_directory
# ------------------------------------------------------------

DROP TABLE IF EXISTS paper_directory;

CREATE TABLE paper_directory (
    id int(11) NOT NULL AUTO_INCREMENT,
    paper_directory_id char(64) NOT NULL DEFAULT '',
    name varchar(512) NOT NULL DEFAULT '',
    parent_paper_directory_id char(64) NOT NULL DEFAULT '',
    tenant_code varchar(128) DEFAULT NULL,
    is_deleted tinyint(4) DEFAULT '0',
    modified_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY paper_directory_id (paper_directory_id)
) ENGINE=InnoDB;


# ------------------------------------------------------------
# Dump of table paper_knowledge_point_relation
# ------------------------------------------------------------

DROP TABLE IF EXISTS paper_knowledge_point_relation;

CREATE TABLE paper_knowledge_point_relation (
    id int(11) NOT NULL AUTO_INCREMENT,
    paper_id char(64) NOT NULL DEFAULT '',
    knowledge_point_id char(64) NOT NULL DEFAULT '',
    is_deleted tinyint(4) DEFAULT '0',
    modified_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB;


# ------------------------------------------------------------
# Dump of table view_paper_knowledge_point
# ------------------------------------------------------------

DROP VIEW IF EXISTS view_paper_knowledge_point;

CREATE TABLE view_paper_knowledge_point (
    paper_id CHAR(64) NOT NULL DEFAULT '',
    knowledge_point_id CHAR(64) NULL DEFAULT '',
    name VARCHAR(256) NULL DEFAULT '',
    tenant_code VARCHAR(128) NULL DEFAULT NULL
) ENGINE=MyISAM;


# ------------------------------------------------------------
# Replace placeholder table for view_paper_knowledge_point with correct view syntax
# ------------------------------------------------------------

DROP TABLE view_paper_knowledge_point;

CREATE VIEW view_paper_knowledge_point
AS SELECT
    pkpr.paper_id AS paper_id,
    kp.knowledge_point_id AS knowledge_point_id,
    kp.name AS name,
    kp.tenant_code AS tenant_code
FROM (paper_knowledge_point_relation pkpr left join knowledge_point kp on((pkpr.knowledge_point_id = kp.knowledge_point_id))) where (pkpr.is_deleted = 0);
