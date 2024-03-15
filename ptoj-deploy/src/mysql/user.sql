/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE = ''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

CREATE DATABASE IF NOT EXISTS `ptoj-user` DEFAULT CHARACTER SET utf8;

USE `ptoj-user`;

/**
  `user_info`：用户信息表
  `session`：用户登录信息表
  `role`：角色表
  `auth`：权限表
  `role_auth`：角色权限表
  `user_ac_problem`：用户ac题目表
 */

/*Table structure for table `user_info` */

DROP TABLE IF EXISTS `user_info`;

CREATE TABLE `user_info`
(
    `uuid`         varchar(32)  NOT NULL,
    `username`     varchar(100) NOT NULL COMMENT '用户名',
    `password`     varchar(100) NOT NULL COMMENT '密码',
    `nickname`     varchar(100)          DEFAULT NULL COMMENT '昵称',
    `school`       varchar(100)          DEFAULT NULL COMMENT '学校',
    `course`       varchar(100)          DEFAULT NULL COMMENT '专业',
    `number`       varchar(20)           DEFAULT NULL COMMENT '学号',
    `realname`     varchar(100)          DEFAULT NULL COMMENT '真实姓名',
    `gender`       varchar(20)           DEFAULT 'secrecy' NOT NULL COMMENT '性别',
    `github`       varchar(255)          DEFAULT NULL COMMENT 'github地址',
    `blog`         varchar(255)          DEFAULT NULL COMMENT '博客地址',
    `cf_username`  varchar(255)          DEFAULT NULL COMMENT 'cf的username',
    `rating`       int(11)               DEFAULT NULL COMMENT 'cf得分',
    `email`        varchar(320)          DEFAULT NULL COMMENT '邮箱',
    `avatar`       varchar(255)          DEFAULT NULL COMMENT '头像地址',
    `signature`    mediumtext COMMENT '个性签名',
    `title_name`   varchar(255)          DEFAULT NULL COMMENT '头衔、称号',
    `title_color`  varchar(255)          DEFAULT NULL COMMENT '头衔、称号的颜色',
    `status`       int(11)      NOT NULL DEFAULT '0' COMMENT '0可用，1不可用',
    `role_id`      bigint(20) unsigned   DEFAULT NULL COMMENT '角色外键',
    `gmt_create`   datetime              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` datetime              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`uuid`),
    UNIQUE KEY `USERNAME_UNIQUE` (`username`),
    UNIQUE KEY `EMAIL_UNIQUE` (`email`),
    UNIQUE KEY `avatar` (`avatar`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/*Table structure for table `session` */

DROP TABLE IF EXISTS `session`;

CREATE TABLE `session`
(
    `id`           bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `uid`          varchar(255)        NOT NULL,
    `user_agent`   varchar(512) DEFAULT NULL,
    `ip`           varchar(255) DEFAULT NULL,
    `gmt_create`   datetime     DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `uid` (`uid`)
#     CONSTRAINT `session_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

/*Table structure for table `role` */

DROP TABLE IF EXISTS `role`;

CREATE TABLE `role`
(
    `id`           bigint(20) unsigned zerofill NOT NULL,
    `role`         varchar(50)                  NOT NULL COMMENT '角色',
    `description`  varchar(100)                          DEFAULT NULL COMMENT '描述',
    `status`       tinyint(4)                   NOT NULL DEFAULT '0' COMMENT '默认0可用，1不可用',
    `gmt_create`   datetime                              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` datetime                              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

# /*Table structure for table `user_role` */

DROP TABLE IF EXISTS `user_role`;

CREATE TABLE `user_role`
(
    `id`           bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `uid`          varchar(32)         NOT NULL,
    `role_id`      bigint(20) unsigned NOT NULL,
    `gmt_create`   datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `uid` (`uid`) USING BTREE,
    KEY `role_id` (`role_id`) USING BTREE
#     CONSTRAINT `user_role_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE,
#     CONSTRAINT `user_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

/*Table structure for table `auth` */

DROP TABLE IF EXISTS `auth`;

CREATE TABLE `auth`
(
    `id`           bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `name`         varchar(100)                 DEFAULT NULL COMMENT '权限名称',
    `permission`   varchar(100)                 DEFAULT NULL COMMENT '权限字符串',
    `status`       tinyint(4)          NOT NULL DEFAULT '0' COMMENT '0可用，1不可用',
    `gmt_create`   datetime                     DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime                     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

/*Table structure for table `role_auth` */

DROP TABLE IF EXISTS `role_auth`;

CREATE TABLE `role_auth`
(
    `id`           bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `auth_id`      bigint(20) unsigned NOT NULL,
    `role_id`      bigint(20) unsigned NOT NULL,
    `gmt_create`   datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `auth_id` (`auth_id`) USING BTREE,
    KEY `role_id` (`role_id`) USING BTREE
#     CONSTRAINT `role_auth_ibfk_1` FOREIGN KEY (`auth_id`) REFERENCES `auth` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
#     CONSTRAINT `role_auth_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

/*Table structure for table `user_record` */

DROP TABLE IF EXISTS `user_record`;

CREATE TABLE `user_record`
(
    `id`           bigint(20)  NOT NULL AUTO_INCREMENT,
    `uid`          varchar(32) NOT NULL COMMENT '用户id',
    `rating`       int(11)  DEFAULT NULL COMMENT 'cf得分',
    `gmt_create`   datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`, `uid`),
    KEY `uid` (`uid`)
#     CONSTRAINT `user_record_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

/*Table structure for table `user_ac_problem` */

DROP TABLE IF EXISTS `user_ac_problem`;

CREATE TABLE `user_ac_problem`
(
    `id`           bigint(20)          NOT NULL AUTO_INCREMENT,
    `uid`          varchar(32)         NOT NULL COMMENT '用户id',
    `pid`          bigint(20) unsigned NOT NULL COMMENT 'ac的题目id',
    `submit_id`    bigint(20) unsigned NOT NULL COMMENT '提交id',
    `gmt_create`   datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `submit_id` (`submit_id`),
    KEY `uid` (`uid`),
    KEY `pid` (`pid`)
#     CONSTRAINT `user_acproblem_ibfk_2` FOREIGN KEY (`pid`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
#     CONSTRAINT `user_acproblem_ibfk_3` FOREIGN KEY (`submit_id`) REFERENCES `judge` (`submit_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

/* DELETE */

delete
from `user_info`;

delete
from `session`;

delete
from `role`;

delete
from `auth`;

delete
from `role_auth`;

delete
from `user_record`;

delete
from `user_ac_problem`;

/* INSERT */

insert into `user_info`(`uuid`, `username`, `password`, `role_id`, `gmt_create`, `gmt_modified`)
values ('1', 'root', 'e10adc3949ba59abbe56e057f20f883e', 00000000000000001000, NOW(), NOW());

insert into `role`(`id`, `role`, `description`, `status`, `gmt_create`, `gmt_modified`)
values (00000000000000001000, 'root', '超级管理员', 0, '2020-10-25 00:16:30', '2020-10-25 00:16:30'),
       (00000000000000001001, 'admin', '管理员', 0, '2020-10-25 00:16:41', '2020-10-25 00:16:41'),
       (00000000000000001002, 'default_user', '默认用户', 0, '2020-10-25 00:16:52', '2021-05-15 07:39:05'),
       (00000000000000001003, 'no_subimit_user', '禁止提交用户', 0, '2021-05-15 07:10:14', '2021-05-15 07:39:14'),
       (00000000000000001004, 'no_discuss_user', '禁止发贴讨论用户', 0, '2021-05-15 07:11:28', '2021-05-15 07:39:16'),
       (00000000000000001005, 'mute_user', '禁言包括回复讨论发帖用户', 0, '2021-05-15 07:12:51', '2021-05-15 07:39:19'),
       (00000000000000001006, 'no_submit_no_discuss_user', '禁止提交同时禁止发帖用户', 0, '2021-05-15 07:38:08',
        '2021-05-15 07:39:34'),
       (00000000000000001007, 'no_submit_mute_user', '禁言禁提交用户', 0, '2021-05-15 07:39:00', '2021-05-15 07:39:26'),
       (00000000000000001008, 'problem_admin', '题目管理员', 0, '2021-06-12 23:15:06', '2021-06-12 23:15:06');

# insert  into `user_role`(`uid`,`role_id`,`gmt_create`,`gmt_modified`) values('1',00000000000000001000,NOW(),NOW());

insert into `auth`(`id`, `name`, `permission`, `status`, `gmt_create`, `gmt_modified`)
values (1, 'problem', 'problem_admin', 0, '2020-10-25 00:17:17', '2021-05-15 06:51:23'),
       (2, 'submit', 'submit', 0, '2020-10-25 00:17:22', '2021-05-15 06:41:59'),
       (3, 'contest', 'contest_admin', 0, '2020-10-25 00:17:33', '2021-05-15 06:51:28'),
       (4, 'rejudge', 'rejudge', 0, '2020-10-25 00:17:49', '2021-05-15 06:50:55'),
       (5, 'announcement', 'announcement_admin', 0, '2021-05-15 06:54:28', '2021-05-15 06:54:31'),
       (6, 'user', 'user_admin', 0, '2021-05-15 06:54:30', '2021-05-15 06:55:04'),
       (7, 'system_info', 'system_info_admin', 0, '2021-05-15 06:57:34', '2021-05-15 06:57:41'),
       (8, 'dicussion', 'discussion_add', 0, '2021-05-15 06:57:36', '2021-05-15 07:50:45'),
       (9, 'dicussion', 'discussion_del', 0, '2021-05-15 07:01:02', '2021-05-15 07:51:31'),
       (10, 'dicussion', 'discussion_edit', 0, '2021-05-15 07:02:15', '2021-05-15 07:51:34'),
       (11, 'comment', 'comment_add', 0, '2021-05-15 07:03:48', '2021-05-15 07:03:48'),
       (12, 'reply', 'reply_add', 0, '2021-05-15 07:04:55', '2021-05-15 07:04:55'),
       (13, 'group', 'group_add', 0, '2022-03-11 13:36:55', '2022-03-11 13:36:55'),
       (14, 'group', 'group_del', 0, '2022-03-11 13:36:55', '2022-03-11 13:36:55');

insert into `role_auth`(`id`, `auth_id`, `role_id`, `gmt_create`, `gmt_modified`)
values (1, 1, 1000, '2020-10-25 00:18:17', '2020-10-25 00:18:17'),
       (2, 2, 1000, '2020-10-25 00:18:38', '2021-05-15 07:17:35'),
       (3, 3, 1000, '2020-10-25 00:18:48', '2021-05-15 07:17:44'),
       (4, 4, 1000, '2021-05-15 07:17:56', '2021-05-15 07:17:56'),
       (5, 5, 1000, '2021-05-15 07:18:20', '2021-05-15 07:18:20'),
       (6, 6, 1000, '2021-05-15 07:18:29', '2021-05-15 07:18:29'),
       (7, 7, 1000, '2021-05-15 07:18:42', '2021-05-15 07:18:42'),
       (8, 8, 1000, '2021-05-15 07:18:59', '2021-05-15 07:18:59'),
       (9, 9, 1000, '2021-05-15 07:19:07', '2021-05-15 07:19:07'),
       (10, 10, 1000, '2021-05-15 07:19:10', '2021-05-15 07:19:10'),
       (11, 11, 1000, '2021-05-15 07:19:13', '2021-05-15 07:19:13'),
       (12, 12, 1000, '2021-05-15 07:19:18', '2021-05-15 07:19:30'),
       (13, 1, 1001, '2021-05-15 07:19:29', '2021-05-15 07:20:02'),
       (14, 2, 1001, '2021-05-15 07:20:25', '2021-05-15 07:20:25'),
       (15, 3, 1001, '2021-05-15 07:20:33', '2021-05-15 07:20:33'),
       (16, 8, 1001, '2021-05-15 07:21:56', '2021-05-15 07:21:56'),
       (17, 9, 1001, '2021-05-15 07:22:03', '2021-05-15 07:22:03'),
       (18, 10, 1001, '2021-05-15 07:22:10', '2021-05-15 07:22:10'),
       (19, 11, 1001, '2021-05-15 07:22:17', '2021-05-15 07:22:17'),
       (20, 12, 1001, '2021-05-15 07:22:21', '2021-05-15 07:22:21'),
       (21, 2, 1002, '2021-05-15 07:22:40', '2021-05-15 07:22:40'),
       (22, 8, 1002, '2021-05-15 07:23:49', '2021-05-15 07:23:49'),
       (23, 9, 1002, '2021-05-15 07:24:10', '2021-05-15 07:24:10'),
       (24, 10, 1002, '2021-05-15 07:24:14', '2021-05-15 07:24:14'),
       (25, 11, 1002, '2021-05-15 07:24:19', '2021-05-15 07:24:19'),
       (26, 12, 1002, '2021-05-15 07:24:23', '2021-05-15 07:24:23'),
       (27, 8, 1003, '2021-05-15 07:32:56', '2021-05-15 07:32:56'),
       (28, 9, 1003, '2021-05-15 07:33:01', '2021-05-15 07:33:01'),
       (29, 10, 1003, '2021-05-15 07:33:05', '2021-05-15 07:33:05'),
       (30, 11, 1003, '2021-05-15 07:33:09', '2021-05-15 07:33:09'),
       (31, 12, 1003, '2021-05-15 07:33:22', '2021-05-15 07:33:22'),
       (32, 2, 1004, '2021-05-15 07:33:38', '2021-05-15 07:33:38'),
       (33, 9, 1004, '2021-05-15 07:34:27', '2021-05-15 07:34:27'),
       (34, 10, 1004, '2021-05-15 07:34:31', '2021-05-15 07:34:31'),
       (35, 11, 1004, '2021-05-15 07:34:42', '2021-05-15 07:34:42'),
       (36, 12, 1004, '2021-05-15 07:34:47', '2021-05-15 07:34:47'),
       (37, 2, 1005, '2021-05-15 07:35:11', '2021-05-15 07:35:11'),
       (38, 9, 1005, '2021-05-15 07:35:46', '2021-05-15 07:35:46'),
       (39, 10, 1005, '2021-05-15 07:36:01', '2021-05-15 07:36:01'),
       (40, 9, 1006, '2021-05-15 07:40:09', '2021-05-15 07:40:09'),
       (41, 10, 1006, '2021-05-15 07:40:16', '2021-05-15 07:40:16'),
       (42, 11, 1006, '2021-05-15 07:40:30', '2021-05-15 07:40:30'),
       (43, 12, 1006, '2021-05-15 07:40:37', '2021-05-15 07:40:37'),
       (44, 9, 1007, '2021-05-15 07:40:54', '2021-05-15 07:40:54'),
       (45, 10, 1007, '2021-05-15 07:41:04', '2021-05-15 07:41:04'),
       (46, 1, 1008, '2021-06-12 23:16:10', '2021-06-12 23:16:10'),
       (47, 2, 1008, '2021-06-12 23:16:15', '2021-06-12 23:16:15'),
       (48, 3, 1008, '2021-06-12 23:16:19', '2021-06-12 23:16:19'),
       (49, 8, 1008, '2021-06-12 23:16:24', '2021-06-12 23:16:24'),
       (50, 9, 1008, '2021-06-12 23:16:45', '2021-06-12 23:16:45'),
       (51, 10, 1008, '2021-06-12 23:16:48', '2021-06-12 23:16:48'),
       (52, 11, 1008, '2021-06-12 23:16:52', '2021-06-12 23:16:52'),
       (53, 12, 1008, '2021-06-12 23:16:58', '2021-06-12 23:16:58'),
       (54, 13, 1000, '2021-06-12 23:16:58', '2021-06-12 23:16:58'),
       (55, 13, 1001, '2021-06-12 23:16:58', '2021-06-12 23:16:58'),
       (56, 13, 1002, '2021-06-12 23:16:58', '2021-06-12 23:16:58'),
       (57, 13, 1008, '2021-06-12 23:16:58', '2021-06-12 23:16:58'),
       (58, 14, 1000, '2021-06-12 23:16:58', '2021-06-12 23:16:58'),
       (59, 14, 1001, '2021-06-12 23:16:58', '2021-06-12 23:16:58'),
       (60, 14, 1002, '2021-06-12 23:16:58', '2021-06-12 23:16:58'),
       (61, 14, 1008, '2021-06-12 23:16:58', '2021-06-12 23:16:58');

insert into `user_record`(`uid`, `gmt_create`, `gmt_modified`)
values ('1', NOW(), NOW());

