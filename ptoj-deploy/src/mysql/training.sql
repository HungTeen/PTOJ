/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE = ''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

CREATE
DATABASE IF NOT EXISTS `ptoj-training` DEFAULT CHARACTER SET utf8;

USE
`ptoj-training`;

/**
  `training`：题单表
  `training_register`：题单注册表
  `training_problem`：题单题目表
  `training_record`: 题单提交记录表
  `training_category`: 题单分类表
  `mapping_training_category`: 题单分类映射表
 */

/*Table structure for table `training` */

DROP TABLE IF EXISTS `training`;

CREATE TABLE `training`
(
    `id`           bigint unsigned NOT NULL AUTO_INCREMENT,
    `title`        varchar(255) DEFAULT NULL COMMENT '训练题单名称',
    `description`  longtext COMMENT '训练题单简介',
    `author`       varchar(255) NOT NULL COMMENT '训练题单创建者用户名',
    `auth`         varchar(255) NOT NULL COMMENT '训练题单权限类型：Public、Private',
    `private_pwd`  varchar(255) DEFAULT NULL COMMENT '训练题单权限为Private时的密码',
    `rank`         int          DEFAULT '0' COMMENT '编号，升序',
    `status`       tinyint(1) DEFAULT '1' COMMENT '是否可用',
    `is_group`     tinyint(1) DEFAULT '0',
    `gid`          bigint(20) unsigned DEFAULT NULL,
    `gmt_create`   datetime     DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
--     CONSTRAINT `training_ibfk_1` FOREIGN KEY (`gid`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `training_register` */

DROP TABLE IF EXISTS `training_register`;

CREATE TABLE `training_register`
(
    `id`           bigint unsigned NOT NULL AUTO_INCREMENT,
    `tid`          bigint unsigned NOT NULL COMMENT '训练id',
    `uid`          varchar(255) NOT NULL COMMENT '用户id',
    `status`       tinyint(1) DEFAULT '1' COMMENT '是否可用',
    `gmt_create`   datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY            `tid` (`tid`),
    KEY            `uid` (`uid`)
--     CONSTRAINT `training_register_ibfk_1` FOREIGN KEY (`tid`) REFERENCES `training` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
--     CONSTRAINT `training_register_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `training_problem` */

DROP TABLE IF EXISTS `training_problem`;

CREATE TABLE `training_problem`
(
    `id`           bigint unsigned NOT NULL AUTO_INCREMENT,
    `tid`          bigint unsigned NOT NULL COMMENT '训练id',
    `pid`          bigint unsigned NOT NULL COMMENT '题目id',
    `rank`         int      DEFAULT '0',
    `display_id`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `gmt_create`   datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY            `tid` (`tid`),
    KEY            `pid` (`pid`),
    KEY            `display_id` (`display_id`)
--     CONSTRAINT `training_problem_ibfk_1` FOREIGN KEY (`tid`) REFERENCES `training` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
--     CONSTRAINT `training_problem_ibfk_2` FOREIGN KEY (`pid`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
--     CONSTRAINT `training_problem_ibfk_3` FOREIGN KEY (`display_id`) REFERENCES `problem` (`problem_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `training_record` */

DROP TABLE IF EXISTS `training_record`;

CREATE TABLE `training_record`
(
    `id`           bigint unsigned NOT NULL AUTO_INCREMENT,
    `tid`          bigint unsigned NOT NULL,
    `tpid`         bigint unsigned NOT NULL,
    `pid`          bigint unsigned NOT NULL,
    `uid`          varchar(255) NOT NULL,
    `submit_id`    bigint unsigned NOT NULL,
    `gmt_create`   datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY            `tid` (`tid`),
    KEY            `tpid` (`tpid`),
    KEY            `pid` (`pid`),
    KEY            `uid` (`uid`),
    KEY            `submit_id` (`submit_id`)
--     CONSTRAINT `training_record_ibfk_1` FOREIGN KEY (`tid`) REFERENCES `training` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
--     CONSTRAINT `training_record_ibfk_2` FOREIGN KEY (`tpid`) REFERENCES `training_problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
--     CONSTRAINT `training_record_ibfk_3` FOREIGN KEY (`pid`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
--     CONSTRAINT `training_record_ibfk_4` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE,
--     CONSTRAINT `training_record_ibfk_5` FOREIGN KEY (`submit_id`) REFERENCES `judge` (`submit_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `training_category` */

DROP TABLE IF EXISTS `training_category`;

CREATE TABLE `training_category`
(
    `id`           bigint unsigned NOT NULL AUTO_INCREMENT,
    `name`         varchar(255) DEFAULT NULL,
    `color`        varchar(255) DEFAULT NULL,
    `gid`          bigint(20) unsigned DEFAULT NULL,
    `gmt_create`   datetime     DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
--     CONSTRAINT `training_category_ibfk_1` FOREIGN KEY (`gid`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `mapping_training_category` */

DROP TABLE IF EXISTS `mapping_training_category`;

CREATE TABLE `mapping_training_category`
(
    `id`           bigint unsigned NOT NULL AUTO_INCREMENT,
    `tid`          bigint unsigned NOT NULL,
    `cid`          bigint unsigned NOT NULL,
    `gmt_create`   datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY            `tid` (`tid`),
    KEY            `cid` (`cid`)
--     CONSTRAINT `mapping_training_category_ibfk_1` FOREIGN KEY (`tid`) REFERENCES `training` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
--     CONSTRAINT `mapping_training_category_ibfk_2` FOREIGN KEY (`cid`) REFERENCES `training_category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
