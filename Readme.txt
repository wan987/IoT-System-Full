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

## 后端功能模块

后端采用Spring Boot + MyBatis Plus开发，包含以下核心模块：

- **用户管理**：用户登录、注册功能
- **站点管理**：监测站点的增删改查
- **网关管理**：IoT网关设备的全生命周期管理
- **传感器管理**：传感器设备的增删改查
- **数据监测**：实时监测传感器采集的数据
- **维护日志**：记录设备维护历史
- **统计分析**：系统数据的可视化展示

## 前端功能模块

前端采用Vue 3 + Element Plus开发，包含以下核心模块：

- **数据监测**：实时数据显示
- **站点管理**：监测站点的可视化管理
- **网关管理**：IoT网关设备管理
- **传感器管理**：传感器设备管理
- **日志管理**：维护日志记录
- **用户登录**：系统用户认证

## 技术栈

**后端技术栈**：
- Spring Boot
- MyBatis Plus
- MySQL
- RESTful API

**前端技术栈**：
- Vue 3
- Vite
- Element Plus
- Axios
- ECharts

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
