/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE = ''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

CREATE
    DATABASE IF NOT EXISTS `ptoj-problem` DEFAULT CHARACTER SET utf8;

USE
    `ptoj-problem`;

/**
  `problem`：题目表
  `problem_case`：题目样例表
  `tag`：标签表
  `tag_classification`：标签分类表
  `problem_tag`：题目标签表
  `language`：语言表
  `problem_language`：题目语言表
  `code_template`：代码模板表
  `judge`：判题记录表
  `judge_case`：判题样例表
 */

/*Table structure for table `problem` */

DROP TABLE IF EXISTS `problem`;

CREATE TABLE `problem`
(
    `id`                    bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `problem_id`            varchar(255)        NOT NULL COMMENT '问题的自定义ID 例如（HOJ-1000）',
    `title`                 varchar(255)        NOT NULL COMMENT '题目',
    `author`                varchar(255)                 DEFAULT '未知' COMMENT '作者',
    `type`                  int(11)             NOT NULL DEFAULT '0' COMMENT '0为ACM,1为OI',
    `time_limit`            int(11)                      DEFAULT '1000' COMMENT '单位ms',
    `memory_limit`          int(11)                      DEFAULT '65535' COMMENT '单位kb',
    `stack_limit`           int(11)                      DEFAULT '128' COMMENT '单位mb',
    `description`           longtext COMMENT '描述',
    `input`                 longtext COMMENT '输入描述',
    `output`                longtext COMMENT '输出描述',
    `examples`              longtext COMMENT '题面样例',
    `is_remote`             tinyint(1)                   DEFAULT '0' COMMENT '是否为vj判题',
    `source`                text COMMENT '题目来源',
    `difficulty`            int(11)                      DEFAULT '0' COMMENT '题目难度,0简单，1中等，2困难',
    `hint`                  longtext COMMENT '备注,提醒',
    `auth`                  int(11)                      DEFAULT '1' COMMENT '默认为1公开，2为私有，3为比赛题目',
    `io_score`              int(11)                      DEFAULT '100' COMMENT '当该题目为io题目时的分数',
    `code_share`            tinyint(1)                   DEFAULT '1' COMMENT '该题目对应的相关提交代码，用户是否可用分享',
    `judge_mode`            varchar(255)                 DEFAULT 'default' COMMENT '题目评测模式,default、spj、interactive',
    `judge_case_mode`       varchar(255)                 DEFAULT 'default' COMMENT '题目样例评测模式,default,subtask_lowest,subtask_average',
    `user_extra_file`       mediumtext                   DEFAULT NULL COMMENT '题目评测时用户程序的额外文件 json key:name value:content',
    `judge_extra_file`      mediumtext                   DEFAULT NULL COMMENT '题目评测时交互或特殊程序的额外文件 json key:name value:content',
    `spj_code`              longtext COMMENT '特判程序或交互程序代码',
    `spj_language`          varchar(255)                 DEFAULT NULL COMMENT '特判程序或交互程序代码的语言',
    `is_remove_end_blank`   tinyint(1)                   DEFAULT '1' COMMENT '是否默认去除用户代码的文末空格',
    `open_case_result`      tinyint(1)                   DEFAULT '1' COMMENT '是否默认开启该题目的测试样例结果查看',
    `is_upload_case`        tinyint(1)                   DEFAULT '1' COMMENT '题目测试数据是否是上传文件的',
    `case_version`          varchar(40)                  DEFAULT '0' COMMENT '题目测试数据的版本号',
    `modified_user`         varchar(255)                 DEFAULT NULL COMMENT '修改题目的管理员用户名',
    `is_group`              tinyint(1)                   DEFAULT '0',
    `gid`                   bigint(20) unsigned          DEFAULT NULL,
    `apply_public_progress` int(11)                      DEFAULT NULL COMMENT '申请公开的进度：null为未申请，1为申请中，2为申请通过，3为申请拒绝',
    `is_file_io`            tinyint(1)                   DEFAULT '0' COMMENT '是否是file io自定义输入输出文件模式',
    `io_read_file_name`     varchar(255)                 DEFAULT NULL COMMENT '题目指定的file io输入文件的名称',
    `io_write_file_name`    varchar(255)                 DEFAULT NULL COMMENT '题目指定的file io输出文件的名称',
    `gmt_create`            datetime                     DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified`          datetime                     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `author` (`author`),
    KEY `problem_id` (`problem_id`)
    # CONSTRAINT `problem_ibfk_1` FOREIGN KEY (`author`) REFERENCES `user_info` (`username`) ON DELETE NO ACTION ON UPDATE CASCADE,
    # CONSTRAINT `problem_ibfk_2` FOREIGN KEY (`gid`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1000
  DEFAULT CHARSET = utf8;

/*Table structure for table `problem_case` */

DROP TABLE IF EXISTS `problem_case`;

CREATE TABLE `problem_case`
(
    `id`           bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `pid`          bigint(20) unsigned NOT NULL COMMENT '题目id',
    `input`        longtext COMMENT '测试样例的输入',
    `output`       longtext COMMENT '测试样例的输出',
    `score`        int(11)  DEFAULT NULL COMMENT '该测试样例的IO得分',
    `status`       int(11)  DEFAULT '0' COMMENT '0可用，1不可用',
    `group_num`    int(11)  DEFAULT '1' COMMENT 'subtask分组的编号',
    `gmt_create`   datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `pid` (`pid`)
    # CONSTRAINT `problem_case_ibfk_1` FOREIGN KEY (`pid`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

/*Table structure for table `tag` */

DROP TABLE IF EXISTS `tag`;

CREATE TABLE `tag`
(
    `id`           bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `name`         varchar(255)        NOT NULL COMMENT '标签名字',
    `color`        varchar(10)         DEFAULT NULL COMMENT '标签颜色',
    `oj`           varchar(255)        DEFAULT 'ME' COMMENT '标签所属oj',
    `gid`          bigint(20) unsigned DEFAULT NULL,
    `tcid`         bigint(20) unsigned DEFAULT NULL,
    `gmt_create`   datetime            DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`, `oj`, `gid`)
    # CONSTRAINT `tag_ibfk_1` FOREIGN KEY (`gid`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    # CONSTRAINT `tag_ibfk_2` FOREIGN KEY (`tcid`) REFERENCES `tag_classification` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

/*Table structure for table `tag_classification` */

DROP TABLE IF EXISTS `tag_classification`;

CREATE TABLE `tag_classification`
(
    `id`           bigint UNSIGNED                                               NOT NULL AUTO_INCREMENT,
    `name`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签分类名称',
    `oj`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签分类所属oj',
    `gmt_create`   datetime                                                      NULL DEFAULT NULL,
    `gmt_modified` datetime                                                      NULL DEFAULT NULL,
    `rank`         int(10) UNSIGNED ZEROFILL                                     NULL DEFAULT NULL COMMENT '标签分类优先级 越小越高',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

/*Table structure for table `problem_tag` */

DROP TABLE IF EXISTS `problem_tag`;

CREATE TABLE `problem_tag`
(
    `id`           bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `pid`          bigint(20) unsigned NOT NULL,
    `tid`          bigint(20) unsigned NOT NULL,
    `gmt_create`   datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `pid` (`pid`),
    KEY `tid` (`tid`)
    # CONSTRAINT `problem_tag_ibfk_1` FOREIGN KEY (`pid`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    # CONSTRAINT `problem_tag_ibfk_2` FOREIGN KEY (`tid`) REFERENCES `tag` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

/*Table structure for table `language` */

DROP TABLE IF EXISTS `language`;

CREATE TABLE `language`
(
    `id`              bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `content_type`    varchar(255) DEFAULT NULL COMMENT '语言类型',
    `description`     varchar(255) DEFAULT NULL COMMENT '语言描述',
    `name`            varchar(255) DEFAULT NULL COMMENT '语言名字',
    `compile_command` mediumtext COMMENT '编译指令',
    `template`        longtext COMMENT '模板',
    `code_template`   longtext COMMENT '语言默认代码模板',
    `is_spj`          tinyint(1)   DEFAULT '0' COMMENT '是否可作为特殊判题的一种语言',
    `oj`              varchar(255) DEFAULT NULL COMMENT '该语言属于哪个oj，自身oj用ME',
    `seq`             int(11)      DEFAULT '0' COMMENT '语言排序',
    `gmt_create`      datetime     DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified`    datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

/*Table structure for table `problem_language` */

DROP TABLE IF EXISTS `problem_language`;

CREATE TABLE `problem_language`
(
    `id`           bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `pid`          bigint(20) unsigned NOT NULL,
    `lid`          bigint(20) unsigned NOT NULL,
    `gmt_create`   datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `pid` (`pid`),
    KEY `lid` (`lid`)
    # CONSTRAINT `problem_language_ibfk_1` FOREIGN KEY (`pid`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    # CONSTRAINT `problem_language_ibfk_2` FOREIGN KEY (`lid`) REFERENCES `language` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

/*Table structure for table `code_template` */

DROP TABLE IF EXISTS `code_template`;

CREATE TABLE `code_template`
(
    `id`           int(11)             NOT NULL AUTO_INCREMENT,
    `pid`          bigint(20) unsigned NOT NULL,
    `lid`          bigint(20) unsigned NOT NULL,
    `code`         longtext            NOT NULL,
    `status`       tinyint(1) DEFAULT '0',
    `gmt_create`   datetime   DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `pid` (`pid`),
    KEY `lid` (`lid`)
    # CONSTRAINT `code_template_ibfk_1` FOREIGN KEY (`pid`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    # CONSTRAINT `code_template_ibfk_2` FOREIGN KEY (`lid`) REFERENCES `language` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

/*Table structure for table `judge` */

DROP TABLE IF EXISTS `judge`;

CREATE TABLE `judge`
(
    `submit_id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `pid`              bigint(20) unsigned NOT NULL COMMENT '题目id',
    `display_pid`      varchar(255)        NOT NULL COMMENT '题目展示id',
    `uid`              varchar(32)         NOT NULL COMMENT '用户id',
    `username`         varchar(255)                 DEFAULT NULL COMMENT '用户名',
    `submit_time`      datetime            NOT NULL COMMENT '提交的时间',
    `status`           int(11)                      DEFAULT NULL COMMENT '结果码具体参考文档',
    `share`            tinyint(1)                   DEFAULT '0' COMMENT '0为仅自己可见，1为全部人可见。',
    `error_message`    mediumtext COMMENT '错误提醒（编译错误，或者vj提醒）',
    `time`             int(11)                      DEFAULT NULL COMMENT '运行时间(ms)',
    `memory`           int(11)                      DEFAULT NULL COMMENT '运行内存（kb）',
    `score`            int(11)                      DEFAULT NULL COMMENT 'IO判题则不为空',
    `length`           int(11)                      DEFAULT NULL COMMENT '代码长度',
    `code`             longtext            NOT NULL COMMENT '代码',
    `language`         varchar(255)                 DEFAULT NULL COMMENT '代码语言',
    `gid`              bigint(20) unsigned          DEFAULT NULL COMMENT '团队id，不为团队内提交则为null',
    `cid`              bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '比赛id，非比赛题目默认为0',
    `cpid`             bigint(20) unsigned          DEFAULT '0' COMMENT '比赛中题目排序id，非比赛题目默认为0',
    `judger`           varchar(20)                  DEFAULT NULL COMMENT '判题机ip',
    `ip`               varchar(20)                  DEFAULT NULL COMMENT '提交者所在ip',
    `version`          int(11)             NOT NULL DEFAULT '0' COMMENT '乐观锁',
    `oi_rank_score`    int(11)             NULL     DEFAULT '0' COMMENT 'oi排行榜得分',
    `vjudge_submit_id` bigint(20) unsigned NULL COMMENT 'vjudge判题在其它oj的提交id',
    `vjudge_username`  varchar(255)        NULL COMMENT 'vjudge判题在其它oj的提交用户名',
    `vjudge_password`  varchar(255)        NULL COMMENT 'vjudge判题在其它oj的提交账号密码',
    `is_manual`        tinyint(1)                   DEFAULT '0' COMMENT '是否为人工评测',
    `gmt_create`       datetime                     DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified`     datetime                     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`submit_id`, `pid`, `display_pid`, `uid`, `cid`),
    KEY `pid` (`pid`),
    KEY `uid` (`uid`),
    KEY `username` (`username`)
#     CONSTRAINT `judge_ibfk_1` FOREIGN KEY (`pid`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
#     CONSTRAINT `judge_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE,
#     CONSTRAINT `judge_ibfk_3` FOREIGN KEY (`username`) REFERENCES `user_info` (`username`) ON DELETE CASCADE ON UPDATE CASCADE,
#     CONSTRAINT `judge_ibfk_4` FOREIGN KEY (`gid`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

/*Table structure for table `judge_case` */

DROP TABLE IF EXISTS `judge_case`;

CREATE TABLE `judge_case`
(
    `id`           bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `submit_id`    bigint(20) unsigned NOT NULL COMMENT '提交判题id',
    `uid`          varchar(32)         NOT NULL COMMENT '用户id',
    `pid`          bigint(20) unsigned NOT NULL COMMENT '题目id',
    `case_id`      bigint(20)   DEFAULT NULL COMMENT '测试样例id',
    `status`       int(11)      DEFAULT NULL COMMENT '具体看结果码',
    `time`         int(11)      DEFAULT NULL COMMENT '测试该样例所用时间ms',
    `memory`       int(11)      DEFAULT NULL COMMENT '测试该样例所用空间KB',
    `score`        int(11)      DEFAULT NULL COMMENT 'IO得分',
    `group_num`    int(11)      DEFAULT NULL COMMENT 'subtask分组的组号',
    `seq`          int(11)      DEFAULT NULL COMMENT '排序',
    `mode`         varchar(255) DEFAULT 'default' COMMENT 'default,subtask_lowest,subtask_average',
    `input_data`   longtext COMMENT '样例输入，比赛不可看',
    `output_data`  longtext COMMENT '样例输出，比赛不可看',
    `user_output`  longtext COMMENT '用户样例输出，比赛不可看',
    `gmt_create`   datetime     DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`, `submit_id`, `uid`, `pid`),
    KEY `case_id` (`case_id`),
    KEY `judge_case_ibfk_1` (`uid`),
    KEY `judge_case_ibfk_2` (`pid`),
    KEY `judge_case_ibfk_3` (`submit_id`)
--     CONSTRAINT `judge_case_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE,
--     CONSTRAINT `judge_case_ibfk_2` FOREIGN KEY (`pid`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
--     CONSTRAINT `judge_case_ibfk_3` FOREIGN KEY (`submit_id`) REFERENCES `judge` (`submit_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

/* DELETE */

delete
from `problem`;

delete
from `problem_case`;

delete
from `tag`;

delete
from `problem_tag`;

delete
from `language`;

delete
from `problem_language`;

delete
from `code_template`;

delete
from judge;

delete
from judge_case;

/*Data for the table `language` */
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-csrc', 'GCC 9.4.0', 'C',
        '/usr/bin/gcc -DONLINE_JUDGE -w -fmax-errors=3 -std=c11 {src_path} -lm -o {exe_path}',
        '#include <stdio.h>\r\nint main() {\r\n    int a,b;\r\n    scanf(\"%d %d\",&a,&b);\r\n    printf(\"%d\",a+b);\r\n    return 0;\r\n}',
        '//PREPEND BEGIN\r\n#include <stdio.h>\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\nint add(int a, int b) {\r\n  // Please fill this blank\r\n  return ___________;\r\n}\r\n//TEMPLATE END\r\n\r\n//APPEND BEGIN\r\nint main() {\r\n  printf(\"%d\", add(1, 2));\r\n  return 0;\r\n}\r\n//APPEND END',
        1, 'ME', '2020-12-12 23:11:44', '2021-06-14 21:40:28');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-csrc', 'GCC 9.4.0', 'C With O2',
        '/usr/bin/gcc -DONLINE_JUDGE -O2 -w -fmax-errors=3 -std=c11 {src_path} -lm -o {exe_path}',
        '#include <stdio.h>\r\nint main() {\r\n    int a,b;\r\n    scanf(\"%d %d\",&a,&b);\r\n    printf(\"%d\",a+b);\r\n    return 0;\r\n}',
        '//PREPEND BEGIN\r\n#include <stdio.h>\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\nint add(int a, int b) {\r\n  // Please fill this blank\r\n  return ___________;\r\n}\r\n//TEMPLATE END\r\n\r\n//APPEND BEGIN\r\nint main() {\r\n  printf(\"%d\", add(1, 2));\r\n  return 0;\r\n}\r\n//APPEND END',
        0, 'ME', '2021-06-14 21:05:57', '2021-06-14 21:20:08');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-c++src', 'G++ 9.4.0', 'C++',
        '/usr/bin/g++ -DONLINE_JUDGE -w -fmax-errors=3 -std=c++14 {src_path} -lm -o {exe_path}',
        '#include<iostream>\r\nusing namespace std;\r\nint main()\r\n{\r\n    int a,b;\r\n    cin >> a >> b;\r\n    cout << a + b;\r\n    return 0;\r\n}',
        '//PREPEND BEGIN\r\n#include <iostream>\r\nusing namespace std;\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\nint add(int a, int b) {\r\n  // Please fill this blank\r\n  return ___________;\r\n}\r\n//TEMPLATE END\r\n\r\n//APPEND BEGIN\r\nint main() {\r\n  cout << add(1, 2);\r\n  return 0;\r\n}\r\n//APPEND END',
        1, 'ME', '2020-12-12 23:12:44', '2021-06-14 21:40:36');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-c++src', 'G++ 9.4.0', 'C++ With O2',
        '/usr/bin/g++ -DONLINE_JUDGE -O2 -w -fmax-errors=3 -std=c++14 {src_path} -lm -o {exe_path}',
        '#include<iostream>\r\nusing namespace std;\r\nint main()\r\n{\r\n    int a,b;\r\n    cin >> a >> b;\r\n    cout << a + b;\r\n    return 0;\r\n}',
        '//PREPEND BEGIN\r\n#include <iostream>\r\nusing namespace std;\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\nint add(int a, int b) {\r\n  // Please fill this blank\r\n  return ___________;\r\n}\r\n//TEMPLATE END\r\n\r\n//APPEND BEGIN\r\nint main() {\r\n  cout << add(1, 2);\r\n  return 0;\r\n}\r\n//APPEND END',
        0, 'ME', '2021-06-14 21:09:35', '2021-06-14 21:20:19');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-c++src', 'G++ 9.4.0', 'C++ 17',
        '/usr/bin/g++ -DONLINE_JUDGE -w -fmax-errors=3 -std=c++17 {src_path} -lm -o {exe_path}',
        '#include<iostream>\r\nusing namespace std;\r\nint main()\r\n{\r\n    int a,b;\r\n    cin >> a >> b;\r\n    cout << a + b;\r\n    return 0;\r\n}',
        '//PREPEND BEGIN\r\n#include <iostream>\r\nusing namespace std;\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\nint add(int a, int b) {\r\n  // Please fill this blank\r\n  return ___________;\r\n}\r\n//TEMPLATE END\r\n\r\n//APPEND BEGIN\r\nint main() {\r\n  cout << add(1, 2);\r\n  return 0;\r\n}\r\n//APPEND END',
        0, 'ME', '2020-12-12 23:12:44', '2021-06-14 21:40:36');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-c++src', 'G++ 9.4.0', 'C++ 17 With O2',
        '/usr/bin/g++ -DONLINE_JUDGE -O2 -w -fmax-errors=3 -std=c++17 {src_path} -lm -o {exe_path}',
        '#include<iostream>\r\nusing namespace std;\r\nint main()\r\n{\r\n    int a,b;\r\n    cin >> a >> b;\r\n    cout << a + b;\r\n    return 0;\r\n}',
        '//PREPEND BEGIN\r\n#include <iostream>\r\nusing namespace std;\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\nint add(int a, int b) {\r\n  // Please fill this blank\r\n  return ___________;\r\n}\r\n//TEMPLATE END\r\n\r\n//APPEND BEGIN\r\nint main() {\r\n  cout << add(1, 2);\r\n  return 0;\r\n}\r\n//APPEND END',
        0, 'ME', '2021-06-14 21:09:35', '2021-06-14 21:20:19');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-c++src', 'G++ 9.4.0', 'C++ 20',
        '/usr/bin/g++ -DONLINE_JUDGE -w -fmax-errors=3 -std=c++20 {src_path} -lm -o {exe_path}',
        '#include<iostream>\r\nusing namespace std;\r\nint main()\r\n{\r\n    int a,b;\r\n    cin >> a >> b;\r\n    cout << a + b;\r\n    return 0;\r\n}',
        '//PREPEND BEGIN\r\n#include <iostream>\r\nusing namespace std;\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\nint add(int a, int b) {\r\n  // Please fill this blank\r\n  return ___________;\r\n}\r\n//TEMPLATE END\r\n\r\n//APPEND BEGIN\r\nint main() {\r\n  cout << add(1, 2);\r\n  return 0;\r\n}\r\n//APPEND END',
        0, 'ME', '2020-12-12 23:12:44', '2021-06-14 21:40:36');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-c++src', 'G++ 9.4.0', 'C++ 20 With O2',
        '/usr/bin/g++ -DONLINE_JUDGE -O2 -w -fmax-errors=3 -std=c++20 {src_path} -lm -o {exe_path}',
        '#include<iostream>\r\nusing namespace std;\r\nint main()\r\n{\r\n    int a,b;\r\n    cin >> a >> b;\r\n    cout << a + b;\r\n    return 0;\r\n}',
        '//PREPEND BEGIN\r\n#include <iostream>\r\nusing namespace std;\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\nint add(int a, int b) {\r\n  // Please fill this blank\r\n  return ___________;\r\n}\r\n//TEMPLATE END\r\n\r\n//APPEND BEGIN\r\nint main() {\r\n  cout << add(1, 2);\r\n  return 0;\r\n}\r\n//APPEND END',
        0, 'ME', '2021-06-14 21:09:35', '2021-06-14 21:20:19');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-java', 'OpenJDK 1.8', 'Java', '/usr/bin/javac {src_path} -d {exe_dir} -encoding UTF8',
        'import java.util.Scanner;\r\npublic class Main{\r\n    public static void main(String[] args){\r\n        Scanner in=new Scanner(System.in);\r\n        int a=in.nextInt();\r\n        int b=in.nextInt();\r\n        System.out.println((a+b));\r\n    }\r\n}',
        '//PREPEND BEGIN\r\nimport java.util.Scanner;\r\n//PREPEND END\r\n\r\npublic class Main{\r\n    //TEMPLATE BEGIN\r\n    public static Integer add(int a,int b){\r\n        return _______;\r\n    }\r\n    //TEMPLATE END\r\n\r\n    //APPEND BEGIN\r\n    public static void main(String[] args){\r\n        System.out.println(add(a,b));\r\n    }\r\n    //APPEND END\r\n}\r\n',
        0, 'ME', '2020-12-12 23:12:51', '2021-06-14 21:19:52');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-python', 'Python 3.7.5', 'Python3', '/usr/bin/python3 -m py_compile {src_path}',
        'a, b = map(int, input().split())\r\nprint(a + b)',
        '//PREPEND BEGIN\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\ndef add(a, b):\r\n    return a + b\r\n//TEMPLATE END\r\n\r\n\r\nif __name__ == \'__main__\':  \r\n    //APPEND BEGIN\r\n    a, b = 1, 1\r\n    print(add(a, b))\r\n    //APPEND END',
        0, 'ME', '2020-12-12 23:14:23', '2021-06-14 21:19:50');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-python', 'Python 2.7.17', 'Python2', '/usr/bin/python -m py_compile {src_path}',
        'a, b = map(int, raw_input().split())\r\nprint a+b',
        '//PREPEND BEGIN\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\ndef add(a, b):\r\n    return a + b\r\n//TEMPLATE END\r\n\r\n\r\nif __name__ == \'__main__\':  \r\n    //APPEND BEGIN\r\n    a, b = 1, 1\r\n    print add(a, b)\r\n    //APPEND END',
        0, 'ME', '2021-01-26 11:11:44', '2021-06-14 21:19:45');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-go', 'Golang 1.19', 'Golang', '/usr/bin/go build -o {exe_path} {src_path}',
        'package main\r\nimport \"fmt\"\r\n\r\nfunc main(){\r\n    var x int\r\n    var y int\r\n    fmt.Scanln(&x,&y)\r\n    fmt.Printf(\"%d\",x+y)  \r\n}\r\n',
        '\r\npackage main\r\n\r\n//PREPEND BEGIN\r\nimport \"fmt\"\r\n//PREPEND END\r\n\r\n\r\n//TEMPLATE BEGIN\r\nfunc add(a,b int)int{\r\n    return ______\r\n}\r\n//TEMPLATE END\r\n\r\n//APPEND BEGIN\r\nfunc main(){\r\n    var x int\r\n    var y int\r\n    fmt.Printf(\"%d\",add(x,y))  \r\n}\r\n//APPEND END\r\n',
        0, 'ME', '2021-03-28 23:15:54', '2021-06-14 21:20:26');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-csharp', 'C# Mono 4.6.2', 'C#', '/usr/bin/mcs -optimize+ -out:{exe_path} {src_path}',
        'using System;\r\nusing System.Linq;\r\n\r\nclass Program {\r\n    public static void Main(string[] args) {\r\n        Console.WriteLine(Console.ReadLine().Split().Select(int.Parse).Sum());\r\n    }\r\n}',
        '//PREPEND BEGIN\r\nusing System;\r\nusing System.Collections.Generic;\r\nusing System.Text;\r\n//PREPEND END\r\n\r\nclass Solution\r\n{\r\n    //TEMPLATE BEGIN\r\n    static int add(int a,int b){\r\n        return _______;\r\n    }\r\n    //TEMPLATE END\r\n\r\n    //APPEND BEGIN\r\n    static void Main(string[] args)\r\n    {\r\n        int a ;\r\n        int b ;\r\n        Console.WriteLine(add(a,b));\r\n    }\r\n    //APPEND END\r\n}',
        0, 'ME', '2021-04-13 16:10:03', '2021-06-14 21:20:36');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-php', 'PHP 7.3.33', 'PHP', '/usr/bin/php {src_path}', '<?=array_sum(fscanf(STDIN, \"%d %d\"));', NULL,
        0, 'ME', '2022-02-25 20:55:30', '2022-09-20 21:43:01');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-python', 'PyPy 2.7.18 (7.3.8)', 'PyPy2', '/usr/bin/pypy -m py_compile {src_path}',
        'print sum(int(x) for x in raw_input().split(\' \'))',
        '//PREPEND BEGIN\n//PREPEND END\n\n//TEMPLATE BEGIN\ndef add(a, b):\n    return a + b\n//TEMPLATE END\n\n\nif __name__ == \'__main__\':  \n    //APPEND BEGIN\n    a, b = 1, 1\n    print add(a, b)\n    //APPEND END',
        0, 'ME', '2022-02-25 20:55:30', '2022-09-20 21:43:03');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-python', 'PyPy 3.8.12 (7.3.8)', 'PyPy3', '/usr/bin/pypy3 -m py_compile {src_path}',
        'print(sum(int(x) for x in input().split(\' \')))',
        '//PREPEND BEGIN\n//PREPEND END\n\n//TEMPLATE BEGIN\ndef add(a, b):\n    return a + b\n//TEMPLATE END\n\n\nif __name__ == \'__main__\':  \n    //APPEND BEGIN\n    a, b = 1, 1\n    print(add(a, b))\n    //APPEND END',
        0, 'ME', '2022-02-25 20:55:30', '2022-09-20 21:43:06');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/javascript', 'Node.js 14.19.0', 'JavaScript Node', '/usr/bin/node {src_path}',
        'var readline = require(\'readline\');\nconst rl = readline.createInterface({\n        input: process.stdin,\n        output: process.stdout\n});\nrl.on(\'line\', function(line){\n   var tokens = line.split(\' \');\n    console.log(parseInt(tokens[0]) + parseInt(tokens[1]));\n});',
        NULL, 0, 'ME', '2022-02-25 20:55:30', '2022-09-20 21:43:09');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/javascript', 'JavaScript V8 8.4.109', 'JavaScript V8', '/usr/bin/jsv8/d8 {src_path}',
        'const [a, b] = readline().split(\' \').map(n => parseInt(n, 10));\nprint((a + b).toString());', NULL, 0, 'ME',
        '2022-02-25 20:55:30', '2022-09-20 21:43:14');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-csrc', 'GCC', 'GCC', NULL, NULL, NULL, 0, 'HDU', '2021-02-18 21:32:34', '2022-09-20 21:44:32');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-c++src', 'G++', 'G++', NULL, NULL, NULL, 0, 'HDU', '2021-02-18 21:32:58', '2022-09-20 21:44:34');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-c++src', 'C++', 'C++', NULL, NULL, NULL, 0, 'HDU', '2021-02-18 21:33:11', '2022-09-20 21:44:36');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-csrc', 'C', 'C', NULL, NULL, NULL, 0, 'HDU', '2021-02-18 21:33:41', '2022-09-20 21:44:38');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-pascal', 'Pascal', 'Pascal', NULL, NULL, NULL, 0, 'HDU', '2021-02-18 21:34:33', '2022-09-20 21:44:41');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-java', 'Java', 'Java', NULL, NULL, NULL, 0, 'HDU', '2022-09-20 21:25:00', '2022-09-20 21:44:46');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-csharp', 'C#', 'C#', NULL, NULL, NULL, 0, 'HDU', '2022-09-20 21:25:00', '2022-09-20 21:45:32');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-csrc', 'GNU GCC C11 5.1.0', 'GNU GCC C11 5.1.0', NULL, NULL, NULL, 0, 'CF', '2021-03-03 19:46:24',
        '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-c++src', 'Clang++17 Diagnostics', 'Clang++17 Diagnostics', NULL, NULL, NULL, 0, 'CF',
        '2021-03-03 19:46:24', '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-c++src', 'GNU G++14 6.4.0', 'GNU G++14 6.4.0', NULL, NULL, NULL, 0, 'CF', '2021-03-03 19:46:24',
        '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-c++src', 'GNU G++17 7.3.0', 'GNU G++17 7.3.0', NULL, NULL, NULL, 0, 'CF', '2021-03-03 19:46:24',
        '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-c++src', 'GNU G++20 11.2.0 (64 bit, winlibs)', 'GNU G++20 11.2.0 (64 bit, winlibs)', NULL, NULL, NULL,
        0, 'CF', '2021-03-03 19:46:24', '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-c++src', 'Microsoft Visual C++ 2017', 'Microsoft Visual C++ 2017', NULL, NULL, NULL, 0, 'CF',
        '2021-03-03 19:46:24', '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-csharp', 'C# Mono 6.8', 'C# Mono 6.8', NULL, NULL, NULL, 0, 'CF', '2021-03-03 19:46:24',
        '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-d', 'D DMD32 v2.091.0', 'D DMD32 v2.091.0', NULL, NULL, NULL, 0, 'CF', '2021-03-03 19:46:24',
        '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-go', 'Go 1.15.6', 'Go 1.15.6', NULL, NULL, NULL, 0, 'CF', '2021-03-03 19:46:24', '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-haskell', 'Haskell GHC 8.10.1', 'Haskell GHC 8.10.1', NULL, NULL, NULL, 0, 'CF', '2021-03-03 19:46:24',
        '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-java', 'Java 1.8.0_241', 'Java 1.8.0_241', NULL, NULL, NULL, 0, 'CF', '2021-03-03 19:46:24',
        '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-java', 'Kotlin 1.4.0', 'Kotlin 1.4.0', NULL, NULL, NULL, 0, 'CF', '2021-03-03 19:46:24',
        '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-ocaml', 'OCaml 4.02.1', 'OCaml 4.02.1', NULL, NULL, NULL, 0, 'CF', '2021-03-03 19:46:24',
        '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-pascal', 'Delphi 7', 'Delphi 7', NULL, NULL, NULL, 0, 'CF', '2021-03-03 19:46:24',
        '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-pascal', 'Free Pascal 3.0.2', 'Free Pascal 3.0.2', NULL, NULL, NULL, 0, 'CF', '2021-03-03 19:46:24',
        '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-pascal', 'PascalABC.NET 3.4.2', 'PascalABC.NET 3.4.2', NULL, NULL, NULL, 0, 'CF', '2021-03-03 19:46:24',
        '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-perl', 'Perl 5.20.1', 'Perl 5.20.1', NULL, NULL, NULL, 0, 'CF', '2021-03-03 19:46:24',
        '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-php', 'PHP 7.2.13', 'PHP 7.2.13', NULL, NULL, NULL, 0, 'CF', '2021-03-03 19:46:24',
        '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-python', 'Python 2.7.18', 'Python 2.7.18', NULL, NULL, NULL, 0, 'CF', '2021-03-03 19:46:24',
        '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-python', 'Python 3.9.1', 'Python 3.9.1', NULL, NULL, NULL, 0, 'CF', '2021-03-03 19:46:24',
        '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-python', 'PyPy 2.7 (7.3.0)', 'PyPy 2.7 (7.3.0)', NULL, NULL, NULL, 0, 'CF', '2021-03-03 19:46:24',
        '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-python', 'PyPy 3.7 (7.3.0)', 'PyPy 3.7 (7.3.0)', NULL, NULL, NULL, 0, 'CF', '2021-03-03 19:46:24',
        '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-ruby', 'Ruby 3.0.0', 'Ruby 3.0.0', NULL, NULL, NULL, 0, 'CF', '2021-03-03 19:46:24',
        '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-rustsrc', 'Rust 1.49.0', 'Rust 1.49.0', NULL, NULL, NULL, 0, 'CF', '2021-03-03 19:46:24',
        '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-scala', 'Scala 2.12.8', 'Scala 2.12.8', NULL, NULL, NULL, 0, 'CF', '2021-03-03 19:46:24',
        '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/javascript', 'JavaScript V8 4.8.0', 'JavaScript V8 4.8.0', NULL, NULL, NULL, 0, 'CF',
        '2021-03-03 19:46:24', '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/javascript', 'Node.js 12.6.3', 'Node.js 12.6.3', NULL, NULL, NULL, 0, 'CF', '2021-03-03 19:46:24',
        '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-csharp', 'C# 8, .NET Core 3.1', 'C# 8, .NET Core 3.1', NULL, NULL, NULL, 0, 'CF', '2021-03-25 21:17:39',
        '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-java', 'Java 11.0.6', 'Java 11.0.6', NULL, NULL, NULL, 0, 'CF', '2021-03-25 21:20:03',
        '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-c++src', 'G++', 'G++', NULL, NULL, NULL, 0, 'POJ', '2021-06-24 22:50:50', '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-csrc', 'GCC', 'GCC', NULL, NULL, NULL, 0, 'POJ', '2021-06-24 22:51:04', '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-java', 'Java', 'Java', NULL, NULL, NULL, 0, 'POJ', '2021-06-24 22:51:29', '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-pascal', 'Pascal', 'Pascal', NULL, NULL, NULL, 0, 'POJ', '2021-06-24 22:51:50', '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-c++src', 'C++', 'C++', NULL, NULL, NULL, 0, 'POJ', '2021-06-24 22:52:15', '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-csrc', 'C', 'C', NULL, NULL, NULL, 0, 'POJ', '2021-06-24 22:52:38', '2022-09-20 21:46:04');
INSERT INTO `language`(`content_type`, `description`, `name`, `compile_command`, `template`, `code_template`, `is_spj`,
                       `oj`, `gmt_create`, `gmt_modified`)
VALUES ('text/x-fortran', 'Fortran', 'Fortran', NULL, NULL, NULL, 0, 'POJ', '2021-06-24 22:55:15',
        '2022-09-20 21:46:04');
