# IoT 系统全栈项目
功能相同的物联网水质检测平台，完整的vue3+springboot前后端开发，比ruoyi框架改编的版本更加精致

## 项目结构

```
IoT-System-Full
├── enterprise-backend    # 后端项目
│   └── SpringBoot        # 基于Spring Boot框架
├── enterprise-frontend   # 前端项目
│   └── Vue3 + Vite       # 基于Vue 3和Vite构建
```

后端服务层 (Backend Center)
后端基于 Spring Boot 生态与 MyBatis Plus 构建，专注于高并发数据处理与设备状态维护：
身份认证与权限控制：安全可靠的用户注册、登录校验与会话管理。
站点拓扑管理 ：多层级水质监测站点的建立、空间元数据配置与资产关联。
网关生命周期管理 ：IoT 网关节点的注册、接入认证、心跳保活与状态追踪。
传感器资产配置 ：水质探头（如 PH 值、溶解氧、浊度等）的参数下发、标定与实例映射。
遥测数据流转 ：高频时序采集数据的接收、解析、持久化与实时分发。
运维审计中心 ：设备运维链路日志追踪与异常操作回溯。
多维数据聚合：提供系统级运行状态与水质指标的统计聚合接口。

前端视图层 (Frontend UI)
前端基于 Vue 3 的 Composition API 与 Vite 构建，结合 Element Plus 打造丝滑的交互体验：
实时可视化看板 ：深度集成 ECharts，实现多维水质指标的动态折线图、仪表盘与时序监控，数据波动一目了然。
空间拓扑视图 ：站点-网关-传感器的层级关系可视化映射与直观调度。
无缝资产管理 ：采用现代化、极简风的 CRUD 交互设计，实现网关与传感器配置的低成本维护。
系统日志链路 ：清晰的列表化维护日志呈现，支持多条件组合检索。

🛠️ 技术栈 / Tech Stack
服务端 (Backend)
核心框架: Spring Boot 3.x / 2.x (按实际填写)
持久层框架: MyBatis Plus
数据库: MySQL 8.0
接口规范: RESTful API / JSON
客户端 (Frontend)
核心框架: Vue 3 (Composition API)
构建工具: Vite 
UI 组件库: Element Plus
网络请求: Axios
数据可视化: ECharts

## 安装指南

**后端安装**：
1. 创建MySQL数据库
2. 执行SQL脚本创建表结构
3. 修改`application.yml`配置数据库连接
4. 运行`EnterpriseBackendApplication.java`启动项目

**前端安装**：
```bash
# 进入项目目录
cd enterprise-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

## 开发者

该项目由万江云维护开发

## 许可证

该项目使用MIT开源许可证
