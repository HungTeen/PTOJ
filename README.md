# PT Online Judge（PTOJ）

[![Java](https://img.shields.io/badge/Java-11-informational)](http://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.12.RELEASE-success)](https://spring.io/projects/spring-boot)
[![SpringCloud Alibaba](https://img.shields.io/badge/Spring%20Cloud%20Alibaba-2021.0.4.0-success)](https://spring.io/projects/spring-cloud-alibaba)
[![MySQL](https://img.shields.io/badge/MySQL-8.2.0-blue)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7.2-red)](https://redis.io/)
[![Nacos](https://img.shields.io/badge/Nacos-2.3.0-%23267DF7)](https://github.com/alibaba/nacos)
[![Sentinel](https://img.shields.io/badge/Sentinel-1.8.7-00FFFF)](https://github.com/alibaba/Sentinel)
[![Dubbo](https://img.shields.io/badge/Dubbo-3.2.4-red)](https://github.com/apache/dubbo)
[![RocketMQ](https://img.shields.io/badge/RocketMQ-5.2.0-yellow)](https://github.com/apache/rocketmq)
[![Sa-Token](https://img.shields.io/badge/SaToken-5.2.0-00FF00)](https://github.com/dromara/Sa-Token)

简体中文 | [English](./README-EN.md)

## 特点：
- **基于Vue和Spring Boot、Spring Cloud Alibaba构建的前后端分离，微服务架构的在线代码评测系统。**
- **使用RocketMQ实现消息队列，使代码评测异步进行并解耦。**
- **Nacos实现服务注册与发现，Dubbo实现微服务之间的RPC调用。**
- **使用Redis实现分布式缓存、分布式锁以及过题排行榜。**
- **使用Sa-Token实现登录和鉴权。**
- **使用Docker进行服务镜像构建与部署。**
- **使用go-judge沙箱进行代码评测。**

