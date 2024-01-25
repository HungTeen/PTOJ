/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE = ''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

CREATE
DATABASE IF NOT EXISTS `ptoj-judge` DEFAULT CHARACTER SET utf8;

USE
`ptoj-judge`;

/**
  `judge`：判题记录表
  `judge_case`：判题样例表
  `judge_server`：判题机表
 */

/*Table structure for table `judge` */

DROP TABLE IF EXISTS `judge`;

CREATE TABLE `judge`
(
    `submit_id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `pid`              bigint(20) unsigned NOT NULL COMMENT '题目id',
    `display_pid`      varchar(255) NOT NULL COMMENT '题目展示id',
    `uid`              varchar(32)  NOT NULL COMMENT '用户id',
    `username`         varchar(255) DEFAULT NULL COMMENT '用户名',
    `submit_time`      datetime     NOT NULL COMMENT '提交的时间',
    `status`           int(11) DEFAULT NULL COMMENT '结果码具体参考文档',
    `share`            tinyint(1) DEFAULT '0' COMMENT '0为仅自己可见，1为全部人可见。',
    `error_message`    mediumtext COMMENT '错误提醒（编译错误，或者vj提醒）',
    `time`             int(11) DEFAULT NULL COMMENT '运行时间(ms)',
    `memory`           int(11) DEFAULT NULL COMMENT '运行内存（kb）',
    `score`            int(11) DEFAULT NULL COMMENT 'IO判题则不为空',
    `length`           int(11) DEFAULT NULL COMMENT '代码长度',
    `code`             longtext     NOT NULL COMMENT '代码',
    `language`         varchar(255) DEFAULT NULL COMMENT '代码语言',
    `gid`              bigint(20) unsigned DEFAULT NULL COMMENT '团队id，不为团队内提交则为null',
    `cid`              bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '比赛id，非比赛题目默认为0',
    `cpid`             bigint(20) unsigned DEFAULT '0' COMMENT '比赛中题目排序id，非比赛题目默认为0',
    `judger`           varchar(20)  DEFAULT NULL COMMENT '判题机ip',
    `ip`               varchar(20)  DEFAULT NULL COMMENT '提交者所在ip',
    `version`          int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁',
    `oi_rank_score`    int(11) NULL DEFAULT '0' COMMENT 'oi排行榜得分',
    `vjudge_submit_id` bigint(20) unsigned NULL COMMENT 'vjudge判题在其它oj的提交id',
    `vjudge_username`  varchar(255) NULL COMMENT 'vjudge判题在其它oj的提交用户名',
    `vjudge_password`  varchar(255) NULL COMMENT 'vjudge判题在其它oj的提交账号密码',
    `is_manual`        tinyint(1) DEFAULT '0' COMMENT '是否为人工评测',
    `gmt_create`       datetime     DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified`     datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`submit_id`, `pid`, `display_pid`, `uid`, `cid`),
    KEY                `pid` (`pid`),
    KEY                `uid` (`uid`),
    KEY                `username` (`username`),
    CONSTRAINT `judge_ibfk_1` FOREIGN KEY (`pid`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `judge_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `judge_ibfk_3` FOREIGN KEY (`username`) REFERENCES `user_info` (`username`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `judge_ibfk_4` FOREIGN KEY (`gid`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `judge_case` */

DROP TABLE IF EXISTS `judge_case`;

CREATE TABLE `judge_case`
(
    `id`           bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `submit_id`    bigint(20) unsigned NOT NULL COMMENT '提交判题id',
    `uid`          varchar(32) NOT NULL COMMENT '用户id',
    `pid`          bigint(20) unsigned NOT NULL COMMENT '题目id',
    `case_id`      bigint(20) DEFAULT NULL COMMENT '测试样例id',
    `status`       int(11) DEFAULT NULL COMMENT '具体看结果码',
    `time`         int(11) DEFAULT NULL COMMENT '测试该样例所用时间ms',
    `memory`       int(11) DEFAULT NULL COMMENT '测试该样例所用空间KB',
    `score`        int(11) DEFAULT NULL COMMENT 'IO得分',
    `group_num`    int(11) DEFAULT NULL COMMENT 'subtask分组的组号',
    `seq`          int(11) DEFAULT NULL COMMENT '排序',
    `mode`         varchar(255) DEFAULT 'default' COMMENT 'default,subtask_lowest,subtask_average',
    `input_data`   longtext COMMENT '样例输入，比赛不可看',
    `output_data`  longtext COMMENT '样例输出，比赛不可看',
    `user_output`  longtext COMMENT '用户样例输出，比赛不可看',
    `gmt_create`   datetime     DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`, `submit_id`, `uid`, `pid`),
    KEY            `case_id` (`case_id`),
    KEY            `judge_case_ibfk_1` (`uid`),
    KEY            `judge_case_ibfk_2` (`pid`),
    KEY            `judge_case_ibfk_3` (`submit_id`),
--     CONSTRAINT `judge_case_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE,
--     CONSTRAINT `judge_case_ibfk_2` FOREIGN KEY (`pid`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
--     CONSTRAINT `judge_case_ibfk_3` FOREIGN KEY (`submit_id`) REFERENCES `judge` (`submit_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `judge_server` */

DROP TABLE IF EXISTS `judge_server`;

CREATE TABLE `judge_server`
(
    `id`              int(11) NOT NULL AUTO_INCREMENT,
    `name`            varchar(255) DEFAULT NULL COMMENT '判题服务名字',
    `ip`              varchar(255) NOT NULL COMMENT '判题机ip',
    `port`            int(11) NOT NULL COMMENT '判题机端口号',
    `url`             varchar(255) DEFAULT NULL COMMENT 'ip:port',
    `cpu_core`        int(11) DEFAULT '0' COMMENT '判题机所在服务器cpu核心数',
    `task_number`     int(11) NOT NULL DEFAULT '0' COMMENT '当前判题数',
    `max_task_number` int(11) NOT NULL COMMENT '判题并发最大数',
    `status`          int(11) DEFAULT '0' COMMENT '0可用，1不可用',
    `is_remote`       tinyint(1) DEFAULT NULL COMMENT '是否开启远程判题vj',
    `cf_submittable`  tinyint(1) DEFAULT 1 NULL COMMENT '是否可提交CF',
    `gmt_create`      datetime     DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified`    datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY               `index_judge_remote` (`is_remote`),
    KEY               `index_judge_url` (`url`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `remote_judge_account` */

DROP TABLE IF EXISTS `remote_judge_account`;

CREATE TABLE `remote_judge_account`
(
    `id`           int(11) NOT NULL AUTO_INCREMENT,
    `oj`           varchar(50)  NOT NULL COMMENT '远程oj名字',
    `username`     varchar(255) NOT NULL COMMENT '账号',
    `password`     varchar(255) NOT NULL COMMENT '密码',
    `status`       tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否可用',
    `version`      bigint(20) DEFAULT '0' COMMENT '版本控制',
    `gmt_create`   datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
