# zxy-manage

基于Spring Boot的企业级后台管理系统

## 项目概述

**zxy-manage** 是一个基于Spring Boot 2.7.18的企业级后台管理系统，集成了完整的用户认证、权限管理、路由配置和Clash订阅服务功能。项目采用现代化的技术栈，注重安全性和可扩展性，适用于企业级应用和SaaS平台的后台管理。

## 核心特性

### 🔐 安全认证
- **JWT双令牌机制**: Access Token (15分钟) + Refresh Token (7天)
- **RSA签名算法**: 非对称加密保护令牌安全
- **Spring Security集成**: 完整的认证授权框架
- **BCrypt密码加密**: 安全的密码存储
- **Redis令牌黑名单**: 支持令牌撤销和过期管理