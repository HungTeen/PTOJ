# PT Online Judge（PTOJ）

![logo](docs/images/ptoj-logo.png)

[![Java](https://img.shields.io/badge/Java-11-informational)](http://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.12.RELEASE-success)](https://spring.io/projects/spring-boot)
[![SpringCloud Alibaba](https://img.shields.io/badge/Spring%20Cloud%20Alibaba-2021.0.4.0-success)](https://spring.io/projects/spring-cloud-alibaba)
[![MySQL](https://img.shields.io/badge/MySQL-8.2.0-blue)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7.2-red)](https://redis.io/)
[![Nacos](https://img.shields.io/badge/Nacos-2.3.0-%23267DF7)](https://github.com/alibaba/nacos)
[![Sentinel](https://img.shields.io/badge/Sentinel-1.8.7-00FFFF)](https://github.com/alibaba/Sentinel)
[![Dubbo](https://img.shields.io/badge/Dubbo-3.2.4-red)](https://github.com/apache/dubbo)
[![RocketMQ](https://img.shields.io/badge/RocketMQ-5.2.0-yellow)](https://github.com/apache/rocketmq)
[![Sa-Token](https://img.shields.io/badge/SaToken-1.37.0-00FF00)](https://github.com/dromara/Sa-Token)

简体中文 | [English](./README-EN.md)

## 介绍

- **基于Vue和Spring Boot、Spring Cloud Alibaba构建的前后端分离，微服务架构的在线代码评测系统。**
- **使用RocketMQ实现消息队列，使代码评测异步进行并解耦。**
- **使用Nacos实现服务注册与发现，Dubbo实现微服务之间的RPC调用。**
- **使用Redis实现分布式缓存、分布式锁以及过题排行榜等功能。**
- **使用Sa-Token实现用户登录和鉴权。**
- **使用Docker进行服务镜像构建与部署。**
- **使用go-judge沙箱进行代码评测。**
- **使用Sentinel进行服务限流、熔断和降级。**

## 部署

本项目基于docker-compose进行部署，在线Demo见[PTOJ](https://119.91.130.251/home)（**目前公安备案审核中，暂时不能用域名**）。

|  用户名  |       密码       |
|:-----:|:--------------:|
| user1 | user1_password |

|                      部署参考                       |
|:-----------------------------------------------:|
|   [本地开发部署容器](ptoj-deploy/docker-compose.yml)    |
| [项目上线部署](ptoj-deploy/remote/docker-compose.yml) |


## 压测

本项目使用Jmeter进行压力测试，测试结果如下：

|    方案     | 平均响应时间 | 并发量  |
|:---------:|:------:|:----:|
| MySQL直接查询 | 1800ms |  3   |
| MySQL缓存查询 | 170ms  |  90  |
| Redis缓存结果 |  30ms  | 2000 |

其中各项环境参数如下：
* MySQL排行榜的表的数据量有约20k行。
* 服务器CPU配置为10核16线程，剩余可用内存16G。
* MySQL直接查询将innodb_buffer_pool_size = 0M（虽然默认最低设置为约512KB)。
* MySQL缓存查询将innodb_buffer_pool_size配置注释，走默认值。

## 架构
![architecture](docs/images/architecture.png)
