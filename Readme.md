# EAS Crown 快速开发平台

[![Java 21](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot 4](https://img.shields.io/badge/Spring%20Boot-4.0.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue 3](https://img.shields.io/badge/Vue-3.5-green.svg)](https://vuejs.org/)

> 一个为**生产落地、快速部署、简化运维**而设计的快速开发平台

## 🎯 项目定位

EAS 是一个面向企业级应用的快速开发平台，但我们更关注：

- ✅ **真正落地** - 不是简历驱动开发，每个功能都经过生产验证
- ✅ **易部署** - 单 JAR 包部署，支持 H2 零配置启动
- ✅ **易维护** - 严格的编码规范，清晰的模块划分

## 🛠️ 技术栈

**后端框架**
- Spring Boot 4.0.2 + Spring Data JPA
- Java 21 + Lombok
- Quartz 4.1（任务调度）
- Caffeine（本地缓存）

**前端框架**
- Vue 3.5 + TypeScript 5.8
- Element Plus 2.11（UI 组件）
- Vite 6.2（构建工具）
- Pinia 3.0（状态管理）

**数据库**
- MySQL 8+（生产环境）
- H2 2.2（开发环境，零配置启动）

**开发工具**
- Knife4j 4.5（API 文档）
- EasyExcel 4.0（Excel 处理）
- Maven 前端插件（自动构建前端）

## 🗺️ 功能路线图

### CORE - 核心域

**用户与权限**

- ✅ 登陆与认证
- ✅ 用户管理
- ✅ 用户组
- ✅ 在线用户管理
- ✅ 权限节点
- ⭕ 岗位管理
- ⭕ 个人中心
- ⭕ 部门数据权限（全部/本部门/下级部门/本人/指定部门）

**系统管理**

- ✅ 菜单管理
- ✅ 端点管理
- ✅ 组织架构管理（企业与部门）
- ✅ 统一文件存储
- ✅ 配置管理
- ✅ 维护中心（数据初始化与重置）
- ✅ 通知中心
- ✅ 注册表
- ⭕ 账号安全策略（注册表的前端）
- ⭕ 服务监控运维面板（JVM 监控）

### AUDIT - 审计域

- ✅ 登陆审计日志
- ✅ 系统内部错误日志
- ⭕ 操作审计日志

### COMMON - 通用域

- ✅ 请求日志（SLF4J）
- ⭕ 防重提交
- ⭕ 接口限流
- ⭕ 敏感词管理/过滤模块
- ⭕ IP 地址定位

### OPEN - 开放平台域

- ⭕ 应用管理
- ⭕ 应用授权管理
- ⭕ 签名验签网关
- ⭕ 开放接口日志

### QT(QuickTask) - 任务调度域

- ✅ 定时任务分类
- ⭕ 定时任务管理
- ⭕ 定时任务日志

> ✅ 已完成 &nbsp;&nbsp;|&nbsp;&nbsp; ⭕ 规划中

## 📄 许可证

本项目为私有项目。

---

**开发团队**: KspTooi  
**版本**: 1.0.0  
**更新**: 2026-02
