# WMS 仓库管理系统后端

一个基于Spring Boot的完整仓库管理系统后端项目，专为前端开发者设计，提供了详细的项目结构和配置说明。

## 🚀 项目特性

- **完整的Spring Boot项目结构** - 标准的Maven项目布局
- **分层架构设计** - Controller、Service、Repository三层架构
- **数据库支持** - 支持H2（开发）和MySQL（生产）
- **RESTful API** - 完整的CRUD操作接口
- **数据验证** - 使用Bean Validation进行参数验证
- **异常处理** - 全局异常处理机制
- **跨域支持** - 配置CORS支持前端调用
- **安全配置** - Spring Security基础配置
- **分页查询** - 支持分页和排序
- **日志记录** - 完整的日志配置
- **Spring Boot 3.x兼容** - 使用最新的Spring Boot 3.4.1和Jakarta EE规范

## 📁 项目结构

```
src/
├── main/
│   ├── java/com/bj/wms/
│   │   ├── WmsApplication.java          # 主启动类
│   │   ├── config/                      # 配置类
│   │   │   ├── AppConfig.java           # 应用配置
│   │   │   ├── DatabaseConfig.java      # 数据库配置
│   │   │   ├── SecurityConfig.java      # 安全配置
│   │   │   └── WebConfig.java           # Web配置
│   │   ├── controller/                  # 控制器层
│   │   │   ├── HomeController.java      # 首页控制器
│   │   │   ├── UserController.java      # 用户控制器
│   │   │   ├── ProductController.java   # 商品控制器
│   │   │   └── WarehouseController.java # 仓库控制器
│   │   ├── entity/                      # 实体类
│   │   │   ├── BaseEntity.java          # 基础实体
│   │   │   ├── User.java                # 用户实体
│   │   │   ├── Product.java             # 商品实体
│   │   │   └── Warehouse.java           # 仓库实体
│   │   ├── exception/                   # 异常处理
│   │   │   └── GlobalExceptionHandler.java # 全局异常处理器
│   │   ├── repository/                  # 数据访问层
│   │   │   ├── UserRepository.java      # 用户Repository
│   │   │   ├── ProductRepository.java   # 商品Repository
│   │   │   └── WarehouseRepository.java # 仓库Repository
│   │   ├── service/                     # 业务逻辑层
│   │   │   ├── UserService.java         # 用户服务
│   │   │   ├── ProductService.java      # 商品服务
│   │   │   └── WarehouseService.java    # 仓库服务
│   │   └── util/                        # 工具类
│   │       ├── PageUtil.java            # 分页工具
│   │       └── ResponseUtil.java        # 响应工具
│   └── resources/
│       ├── application.yml              # 主配置文件
│       ├── application-dev.yml          # 开发环境配置
│       ├── application-prod.yml         # 生产环境配置
│       └── data.sql                     # 初始化数据
└── test/
    └── java/com/bj/wms/
        └── WmsApplicationTests.java     # 测试类
```

## 🛠️ 技术栈

- **Java 21** - 编程语言
- **Spring Boot 3.4.1** - 应用框架
- **Spring Data JPA** - 数据访问
- **Spring Security** - 安全框架
- **H2 Database** - 开发数据库
- **MySQL** - 生产数据库
- **Maven 3.9.11** - 项目管理
- **Lombok** - 代码简化
- **Bean Validation** - 数据验证

## 📋 环境要求

- JDK 21.0.8 或更高版本
- Maven 3.9.11 或更高版本
- MySQL 8.0（生产环境）

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone <your-repo-url>
cd wms-backend
```

### 2. 配置数据库

#### 开发环境（使用H2内存数据库）
无需额外配置，项目已配置H2内存数据库。

#### 生产环境（使用MySQL）
1. 安装MySQL 8.0
2. 创建数据库：
```sql
CREATE DATABASE wms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
3. 修改 `application-prod.yml` 中的数据库连接信息

### 3. 运行项目

```bash
# 开发环境运行
mvn spring-boot:run

# 或者指定开发环境配置
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 生产环境运行
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### 4. 访问应用

- 应用地址：http://localhost:8080/api/v1
- 健康检查：http://localhost:8080/api/v1/health
- H2控制台：http://localhost:8080/h2-console（仅开发环境）

## 📚 API文档

### 基础接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/v1/` | 系统首页 |
| GET | `/api/v1/health` | 健康检查 |

### 用户管理

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/v1/users` | 创建用户 |
| GET | `/api/v1/users` | 获取用户列表 |
| GET | `/api/v1/users/{id}` | 获取用户详情 |
| GET | `/api/v1/users/username/{username}` | 根据用户名获取用户 |
| GET | `/api/v1/users/search` | 搜索用户 |
| GET | `/api/v1/users/role/{role}` | 根据角色获取用户 |
| PUT | `/api/v1/users/{id}` | 更新用户信息 |
| PUT | `/api/v1/users/{id}/password` | 修改密码 |
| DELETE | `/api/v1/users/{id}` | 删除用户 |

### 商品管理

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/v1/products` | 创建商品 |
| GET | `/api/v1/products` | 获取商品列表 |
| GET | `/api/v1/products/{id}` | 获取商品详情 |
| GET | `/api/v1/products/code/{productCode}` | 根据编码获取商品 |
| GET | `/api/v1/products/search` | 搜索商品 |
| GET | `/api/v1/products/category/{category}` | 根据分类获取商品 |
| GET | `/api/v1/products/brand/{brand}` | 根据品牌获取商品 |
| GET | `/api/v1/products/low-stock` | 获取库存不足商品 |
| GET | `/api/v1/products/over-stock` | 获取库存过多商品 |
| PUT | `/api/v1/products/{id}` | 更新商品信息 |
| PUT | `/api/v1/products/{id}/stock` | 更新库存 |
| PUT | `/api/v1/products/{id}/stock/increase` | 增加库存 |
| PUT | `/api/v1/products/{id}/stock/decrease` | 减少库存 |
| DELETE | `/api/v1/products/{id}` | 删除商品 |

### 仓库管理

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/v1/warehouses` | 创建仓库 |
| GET | `/api/v1/warehouses` | 获取仓库列表 |
| GET | `/api/v1/warehouses/{id}` | 获取仓库详情 |
| GET | `/api/v1/warehouses/code/{warehouseCode}` | 根据编码获取仓库 |
| GET | `/api/v1/warehouses/search` | 搜索仓库 |
| GET | `/api/v1/warehouses/active` | 获取启用仓库 |
| GET | `/api/v1/warehouses/high-usage` | 获取高使用率仓库 |
| PUT | `/api/v1/warehouses/{id}` | 更新仓库信息 |
| PUT | `/api/v1/warehouses/{id}/capacity` | 更新使用容量 |
| GET | `/api/v1/warehouses/{id}/usage-rate` | 计算使用率 |
| DELETE | `/api/v1/warehouses/{id}` | 删除仓库 |

## 🔧 配置说明

### 分页参数

- `page`: 页码（从0开始，默认0）
- `size`: 页大小（默认10，最大100）
- `sortBy`: 排序字段（默认id）
- `sortDir`: 排序方向（asc/desc，默认desc）

### 用户角色

- `ADMIN`: 管理员
- `MANAGER`: 仓库管理员
- `OPERATOR`: 操作员
- `VIEWER`: 查看者

### 状态码

- `0`: 禁用/下架
- `1`: 启用/上架

## 🧪 测试数据

项目启动时会自动插入测试数据：

### 测试用户
- 用户名：`admin`，密码：`123456`，角色：管理员
- 用户名：`manager1`，密码：`123456`，角色：仓库管理员
- 用户名：`operator1`，密码：`123456`，角色：操作员
- 用户名：`viewer1`，密码：`123456`，角色：查看者

### 测试仓库
- WH001: 北京总仓
- WH002: 上海分仓
- WH003: 广州分仓

### 测试商品
- P001: iPhone 15 Pro
- P002: MacBook Pro
- P003: AirPods Pro
- P004: iPad Air
- P005: Samsung Galaxy S24

## 🔒 安全配置

项目使用Spring Security进行安全配置：

- 密码使用BCrypt加密
- 支持跨域请求
- 暂时开放所有API接口（可根据需要添加认证）

## 📝 开发建议

### 作为前端开发者，你需要了解：

1. **API调用方式**
   - 所有API都有统一的响应格式
   - 支持分页查询
   - 错误信息统一处理

2. **数据验证**
   - 后端会验证所有输入数据
   - 验证失败会返回详细错误信息

3. **跨域配置**
   - 已配置支持前端跨域调用
   - 支持所有HTTP方法

4. **分页处理**
   - 使用标准的分页参数
   - 响应包含分页信息

## 🚀 部署

### 开发环境
```bash
mvn spring-boot:run
```

### 生产环境
```bash
# 打包
mvn clean package

# 运行
java -jar target/wms-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## 📞 支持

如果你在开发过程中遇到问题，可以：

1. 查看日志文件了解详细错误信息
2. 检查数据库连接配置
3. 确认Java版本和Maven版本
4. 查看API文档确认请求格式

## 🔄 版本升级说明

### Spring Boot 3.x 升级要点

本项目已升级到Spring Boot 3.4.1，主要变更包括：

1. **Java版本要求**: 最低要求Java 17，推荐使用Java 21
2. **Jakarta EE**: 所有`javax.*`包已迁移到`jakarta.*`包
3. **Spring Security**: 配置方式已更新为新的Lambda DSL风格
4. **JWT依赖**: 升级到最新版本0.12.6以兼容Spring Boot 3.x

### 主要变更

- `javax.persistence.*` → `jakarta.persistence.*`
- `javax.validation.*` → `jakarta.validation.*`
- Spring Security配置使用新的Lambda DSL
- JWT库升级到0.12.6版本

## 📄 许可证

MIT License

---

**注意**: 这是一个学习项目，生产环境使用前请根据实际需求进行安全加固和性能优化。
