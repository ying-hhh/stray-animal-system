# 🐾 校园动物学友图鉴与动态打卡系统

基于 **Spring Boot 3 + Vue 3** 的校园动物管理平台，支持动物图鉴浏览、打卡动态发布、社交互动等功能。
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0.2-brightgreen)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.x-4FC08D)](https://vuejs.org/)

---

## ✨ 功能特色

### 📚 动物图鉴管理（管理员）
- 录入动物档案（名字、类型、常驻区域、描述 + 封面图片）
- 按名字模糊检索、按类型筛选、分页展示
- 编辑动物信息（含更换封面图片）
- 软删除动物档案

### 📝 打卡动态（用户）
- 发布打卡（文字描述 + 可选图片）
- 动态时间轴（按时间倒序排列）
- 删除自己的打卡记录
- 打卡点赞/取消点赞

### 💬 社交互动
- 打卡回复功能
- 回复删除（仅限本人）

### 👤 个人中心
- 修改昵称
- 更换头像
- 浏览历史记录

---

## 🛠️ 技术栈

### 后端
| 技术 | 版本 |
|------|------|
| Java | 17 |
| Spring Boot | 3.0.2 |
| MyBatis-Plus | 3.5.3.2 |
| SQL Server | 2022 |
| JWT | 0.9.1 |
| Lombok | 1.18.30 |
| Maven | 3.8+ |

### 前端
| 技术 | 版本 |
|------|------|
| Vue | 3.x |
| Vue Router | 4.x |
| Pinia | 2.x |
| Element Plus | 2.3.x |
| Axios | 1.x |
| Vite | 4.x |

---


### 📁 项目结构
```
stray-animal-system/
│
├── backend/ # Spring Boot 后端
│ ├── src/main/java/
│ │ └── org/example/animalsystem/
│ │ ├── controller/ # 控制器层（处理 HTTP 请求）
│ │ ├── service/ # 业务逻辑层
│ │ ├── mapper/ # 数据访问层（MyBatis-Plus）
│ │ ├── entity/ # 实体类
│ │ ├── dto/ # 数据传输对象
│ │ ├── vo/ # 视图对象
│ │ ├── config/ # 配置类
│ │ ├── utils/ # 工具类
│ │ └── AnimalSystemApplication.java
│ │
│ ├── src/main/resources/
│ │ ├── application.yml # 配置文件（含敏感信息，不上传）
│ │ ├── application-example.yml # 配置示例（上传）
│ │ └── static/
│ │
│ └── pom.xml # Maven 依赖配置
│
├── frontend/ # Vue 3 前端
│ ├── src/
│ │ ├── api/ # API 请求模块
│ │ │ ├── request.js
│ │ │ ├── animal.js
│ │ │ └── auth.js
│ │ │
│ │ ├── views/ # 页面组件
│ │ │ ├── Login.vue # 登录页面
│ │ │ ├── Home.vue # 动物图鉴首页
│ │ │ ├── AnimalDetail.vue # 动物详情 + 打卡时间轴
│ │ │ ├── Admin.vue # 管理后台
│ │ │ └── UserCenter.vue # 个人中心
│ │ │
│ │ ├── router/
│ │ │ └── index.js # 路由配置
│ │ │
│ │ ├── stores/ # Pinia 状态管理
│ │ ├── App.vue # 根组件
│ │ └── main.js # 入口文件
│ │
│ ├── package.json # npm 依赖配置
│ ├── vite.config.js # Vite 构建配置
│ └── index.html # HTML 入口
│
├── database/ # 数据库脚本
│ └── schema.sql # 建表脚本
│
├── docs/ # 文档
│ └── 课程设计报告.md
│
├── README.md # 项目说明
├── LICENSE # 开源协议
└── .gitignore # Git 忽略配置
```
---

## 🚀 快速开始

### 环境要求
- JDK 17+
- SQL Server 2022+
- Node.js 18+
- Maven 3.8+

### 1. 克隆项目

```bash
git clone https://github.com/你的用户名/stray-animal-system.git
cd stray-animal-system
```

### 2. 配置数据库
- 在 SQL Server 中执行 database/schema.sql
- 复制 application-example.yml 为 application.yml
- 修改数据库密码和 JWT 密钥

### 3. 启动后端
```bash
cd backend
mvn spring-boot:run
```

### 4. 启动前端
```bash
cd frontend
npm install
npm run dev
```

### 5. 测试账号
| 角色	| 用户名	| 密码 |
|------|------|------|
| 管理员 |	admin	| 123456 |
| 普通用户	| zhangsan | 123456 |
