# ************************************************************
# Sequel Pro SQL dump
# Version 4096
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: 192.168.10.222 (MySQL 5.5.29-log)
# Database: survey
# Generation Time: 2016-05-26 06:53:38 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table answer
# ------------------------------------------------------------

DROP TABLE IF EXISTS `answer`;

CREATE TABLE `answer` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `topic_id` int(11) DEFAULT NULL,
  `question_id` int(11) DEFAULT NULL,
  `question_item_id` int(11) DEFAULT NULL,
  `content` text,
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

LOCK TABLES `answer` WRITE;
/*!40000 ALTER TABLE `answer` DISABLE KEYS */;

INSERT INTO `answer` (`id`, `topic_id`, `question_id`, `question_item_id`, `content`, `last_modified`)
VALUES
	(1,53,84,248,'','2016-05-26 14:27:09'),
	(2,53,104,252,'','2016-05-26 14:27:09'),
	(3,53,103,0,'服了你了','2016-05-26 14:27:09'),
	(4,53,84,247,'','2016-05-26 14:33:45'),
	(5,53,104,249,'','2016-05-26 14:33:45'),
	(6,59,87,64,'','2016-05-26 14:35:18'),
	(7,59,88,180,'','2016-05-26 14:35:18'),
	(8,59,89,183,'','2016-05-26 14:35:18'),
	(9,59,93,142,'','2016-05-26 14:35:18'),
	(10,59,93,143,'','2016-05-26 14:35:18'),
	(11,59,91,185,'','2016-05-26 14:35:18'),
	(12,59,92,213,'','2016-05-26 14:35:18'),
	(13,59,92,216,'','2016-05-26 14:35:18'),
	(14,59,92,218,'','2016-05-26 14:35:18'),
	(15,59,94,226,'','2016-05-26 14:35:18'),
	(16,59,94,227,'','2016-05-26 14:35:18'),
	(17,59,95,223,'','2016-05-26 14:35:18'),
	(18,59,87,64,'','2016-05-26 14:37:38'),
	(19,59,88,180,'','2016-05-26 14:37:38'),
	(20,59,89,183,'','2016-05-26 14:37:38'),
	(21,59,93,142,'','2016-05-26 14:37:38'),
	(22,59,93,143,'','2016-05-26 14:37:38'),
	(23,59,91,185,'','2016-05-26 14:37:38'),
	(24,59,92,213,'','2016-05-26 14:37:38'),
	(25,59,92,216,'','2016-05-26 14:37:38'),
	(26,59,92,218,'','2016-05-26 14:37:38'),
	(27,59,94,226,'','2016-05-26 14:37:38'),
	(28,59,94,227,'','2016-05-26 14:37:38'),
	(29,59,94,232,'盗墓','2016-05-26 14:37:38'),
	(30,59,95,223,'','2016-05-26 14:37:38'),
	(31,59,87,63,'','2016-05-26 14:40:22'),
	(32,59,88,177,'','2016-05-26 14:40:22'),
	(33,59,89,182,'','2016-05-26 14:40:22'),
	(34,59,93,142,'','2016-05-26 14:40:22'),
	(35,59,93,143,'','2016-05-26 14:40:22'),
	(36,59,93,145,'程序开发','2016-05-26 14:40:22'),
	(37,59,91,186,'','2016-05-26 14:40:22'),
	(38,59,91,187,'','2016-05-26 14:40:22'),
	(39,59,92,214,'','2016-05-26 14:40:22'),
	(40,59,94,228,'','2016-05-26 14:40:22'),
	(41,59,94,230,'','2016-05-26 14:40:22'),
	(42,59,95,224,'','2016-05-26 14:40:22');

/*!40000 ALTER TABLE `answer` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table gift
# ------------------------------------------------------------

DROP TABLE IF EXISTS `gift`;

CREATE TABLE `gift` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL DEFAULT '',
  `info` varchar(128) NOT NULL DEFAULT '',
  `code` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

LOCK TABLES `gift` WRITE;
/*!40000 ALTER TABLE `gift` DISABLE KEYS */;

INSERT INTO `gift` (`id`, `name`, `info`, `code`)
VALUES
	(5,'优惠券','888 元',4),
	(4,'优惠券','388 元',3),
	(3,'优惠券','288 元',2),
	(2,'优惠券','188 元',1),
	(1,'优惠券','88 元',0);

/*!40000 ALTER TABLE `gift` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table participant
# ------------------------------------------------------------

DROP TABLE IF EXISTS `participant`;

CREATE TABLE `participant` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL DEFAULT '',
  `gender` tinyint(4) DEFAULT NULL,
  `telephone` varchar(56) NOT NULL DEFAULT '',
  `mail` varchar(128) NOT NULL DEFAULT '',
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

LOCK TABLES `participant` WRITE;
/*!40000 ALTER TABLE `participant` DISABLE KEYS */;

INSERT INTO `participant` (`id`, `name`, `gender`, `telephone`, `mail`, `last_modified`)
VALUES
	(21,'Lucky',1,'18001166441','lucky@gmail.com','2016-05-26 14:35:36');

/*!40000 ALTER TABLE `participant` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table participant_gift
# ------------------------------------------------------------

DROP TABLE IF EXISTS `participant_gift`;

CREATE TABLE `participant_gift` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `participant_id` int(11) NOT NULL,
  `gift_code` int(11) NOT NULL,
  `description` varchar(128) DEFAULT NULL,
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

LOCK TABLES `participant_gift` WRITE;
/*!40000 ALTER TABLE `participant_gift` DISABLE KEYS */;

INSERT INTO `participant_gift` (`id`, `participant_id`, `gift_code`, `description`, `last_modified`)
VALUES
	(14,21,4,'Hack--?','2016-05-26 14:35:58');

/*!40000 ALTER TABLE `participant_gift` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table question
# ------------------------------------------------------------

DROP TABLE IF EXISTS `question`;

CREATE TABLE `question` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `topic_id` int(11) DEFAULT NULL,
  `content` text,
  `type` int(11) DEFAULT NULL,
  `_order` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

LOCK TABLES `question` WRITE;
/*!40000 ALTER TABLE `question` DISABLE KEYS */;

INSERT INTO `question` (`id`, `topic_id`, `content`, `type`, `_order`)
VALUES
	(88,59,'2、您是',1,1),
	(87,59,'1、您是',1,0),
	(84,53,'1. 你是谁',1,0),
	(89,59,'3、您的工作年限',1,2),
	(91,59,'4-1：学历提升',2,4),
	(92,59,'4-2：IT培训',2,5),
	(93,59,'4-3：创业课程',2,6),
	(94,59,'4-4：个人兴趣、技能、逼格',2,7),
	(103,53,'最喜欢看诸葛亮，卧龙，孔明三个人互相斗了',3,2),
	(95,59,'5、学一单元课程小主您想用多少银子（RMB）？',1,8),
	(98,59,'4、如果小主想对自己进行投资，您愿意付费学哪些网络课程？（多选）',4,3),
	(104,53,'哪一个不是四大名著',1,100000),
	(102,53,'三国演义',4,1);

/*!40000 ALTER TABLE `question` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table question_item
# ------------------------------------------------------------

DROP TABLE IF EXISTS `question_item`;

CREATE TABLE `question_item` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `question_id` int(11) DEFAULT NULL,
  `content` text,
  `type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

LOCK TABLES `question_item` WRITE;
/*!40000 ALTER TABLE `question_item` DISABLE KEYS */;

INSERT INTO `question_item` (`id`, `question_id`, `content`, `type`)
VALUES
	(64,87,'帅哥',NULL),
	(250,104,'水浒传',0),
	(249,104,'三国演义',0),
	(248,84,'张飞',0),
	(247,84,'关羽',0),
	(19,71,'Java',NULL),
	(20,71,'Ruby',NULL),
	(21,71,'Python',NULL),
	(180,88,'30岁以上',0),
	(184,89,'其他',1),
	(183,89,'3-5年',0),
	(182,89,'1-3年',0),
	(181,89,'大学生',0),
	(179,88,'25—30岁',0),
	(178,88,'20—25岁',0),
	(177,88,'20岁以下',0),
	(63,87,'美女',NULL),
	(187,91,'名校第二本科  （会计、计算机、行政管理、英语、市场营销、护理学、法律等专业）',0),
	(185,91,'在职研究生含MBA、EMBA',0),
	(186,91,'专科升名校本科（会计、计算机、行政管理、英语、市场营销、护理学、法律等专业）',0),
	(251,104,'红楼梦',0),
	(252,104,'西厢记',0),
	(213,92,'编程语言（Java、PHP、SQL、Javascript）',0),
	(214,92,'软件应用 （PS、Axure、Illustrator、CAD、DW、 3DMAX、MAYA）',0),
	(215,92,'影视后期（影视特效、后期合成剪辑）',0),
	(216,92,'UI设计师（交互设计、Web UI、APP UI、图标设计）',0),
	(144,93,'团队建设（股权设计、期权激励设计、招人神技、企业文化设计、办公区图绘）',0),
	(143,93,'互联网运营（种子用户获取、流量获取、地推技巧、推文写作与传播）',0),
	(232,94,'其他',1),
	(246,84,'刘备',0),
	(245,103,'',0),
	(230,94,'音乐修养（吉他、口琴、手鼓、声乐、交响乐鉴赏、爵士乐鉴赏）',0),
	(229,94,'神秘国学（实用易经、风水、紫微斗数、奇门遁甲、鬼谷子、潜能开发）',0),
	(231,94,'恋爱秘籍（社交软件秘籍、相亲秘籍、沟通技巧、约会秘籍）',0),
	(227,94,'艺术鉴赏（素描、油画、雕塑、建筑）',0),
	(228,94,'炫酷自拍（人像、风光、静物、合照）',0),
	(224,95,'2000 以上',0),
	(223,95,'1000—2000',0),
	(222,95,'500—1000',0),
	(221,95,'200—500',0),
	(220,95,'200 以下',0),
	(218,92,'移动开发（Andriod、IOS、APP）',0),
	(217,92,'Web前端',0),
	(142,93,'精益创业（思维导图、商业模式画布、商业计划书、市场调研）',0),
	(226,94,'厨艺提升（八大菜系、家常西餐、特色小吃、滋补煲汤、烘焙）',0),
	(219,92,'其他',1),
	(145,93,'其他',1),
	(225,94,'化妆美容（化妆技巧及手法、彩妆）',0),
	(188,91,'其他',1);

/*!40000 ALTER TABLE `question_item` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table topic
# ------------------------------------------------------------

DROP TABLE IF EXISTS `topic`;

CREATE TABLE `topic` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `content` text,
  `url` varchar(1024) DEFAULT NULL,
  `_order` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

LOCK TABLES `topic` WRITE;
/*!40000 ALTER TABLE `topic` DISABLE KEYS */;

INSERT INTO `topic` (`id`, `content`, `url`, `_order`)
VALUES
	(53,'<p>古典知识问答</p>\n','',NULL),
	(59,'<p style=\"border: 1px solid rgb(204, 204, 204); padding: 5px 10px; background-color: rgb(238, 238, 238); text-align: center; background-position: initial initial; background-repeat: initial initial;\"><strong>&ldquo;你最想付费学习哪些网络课程&rdquo;</strong></p>\n\n<div style=\"text-align: right;\">&mdash;华夏大地教育网问卷调查</div>\n\n<div>小主吉祥！ &nbsp;</div>\n\n<div>春风和煦，想来各位小主已踏上求学之路，真是极好的。</div>\n\n<div>请各位小主花2分钟时间，勾选您最想学的网络课程，小的为您准备了礼物哦！</div>\n\n<div>说明：本次投票采用匿名形式，我们将严格保密您的信息，请放心作答。</div>\n','http://survey.edu-edu.com.cn/participant',NULL);

/*!40000 ALTER TABLE `topic` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
