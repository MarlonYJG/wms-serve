# WMS后端生产环境部署指南

## 部署前准备

### 1. 数据库准备
确保MySQL数据库已准备就绪：
```sql
-- 创建数据库
CREATE DATABASE wms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户并授权
CREATE USER 'wms'@'%' IDENTIFIED BY 'Wms!Passw0rd';
GRANT ALL PRIVILEGES ON wms_db.* TO 'wms'@'%';
FLUSH PRIVILEGES;
```

### 2. 服务器环境
- Java 21 已安装
- MySQL 8.0+ 已安装并运行
- 网络连通性正常

## 部署步骤

### 1. 打包项目

#### 方法一：使用生产构建脚本（推荐）
```bash
# Linux/Mac
./build-prod.sh

# Windows
build-prod.bat
```

#### 方法二：手动打包（不推荐）
```bash
# 1. 先修改 pom.xml 中的版本号，移除 -SNAPSHOT 后缀
# 2. 然后执行：
mvn clean package -DskipTests -Dspring.profiles.active=prod
```

**注意：手动打包容易出错，强烈建议使用方法一的生产构建脚本**

### 2. 上传JAR文件
将 `target/wms-backend-1.0.0.jar` 或 `release/wms-backend-1.0.0.jar` 上传到服务器

### 3. 启动应用

#### 方法一：使用启动脚本（推荐）
```bash
# 给脚本执行权限
chmod +x start-prod-server.sh

# 启动应用
./start-prod-server.sh
```

#### 方法二：直接使用命令
```bash
# 创建日志目录
mkdir -p /root/workspace/serve/logs

# 启动应用
nohup java -Xms512m -Xmx1024m -jar wms-backend-1.0.0.jar --spring.profiles.active=prod > /root/workspace/serve/logs/app.log 2>&1 &
```

## 重要说明

### 生产环境要求
- **版本号**：必须使用正式版本号（如 1.0.0），不能使用 SNAPSHOT 版本
- **数据库**：使用MySQL数据库，通过 `application-prod.yml` 配置
- **配置激活**：启动时必须指定 `--spring.profiles.active=prod` 参数
- **依赖排除**：生产包不应包含开发工具依赖（如 devtools）

### 环境配置对比
| 环境 | 版本号 | 数据库 | 配置文件 | 依赖 |
|------|--------|--------|----------|------|
| 开发环境 | 0.0.1-SNAPSHOT | H2内存数据库 | application.yml | 包含devtools |
| 生产环境 | 1.0.0 | MySQL数据库 | application-prod.yml | 排除devtools |

### 日志配置
- 应用日志：`/root/workspace/serve/logs/app.log`
- Spring Boot日志：`logs/wms-backend.log`（由application-prod.yml配置）

### 健康检查
```bash
# 检查应用是否启动
curl http://localhost:8080/api/v1/actuator/health

# 查看应用日志
tail -f /root/workspace/serve/logs/app.log

# 查看进程
ps aux | grep wms-backend
```

### 停止应用
```bash
# 查找进程ID
ps aux | grep wms-backend

# 停止应用
kill <PID>
```

## 常见问题

### 1. 版本号错误
**问题**：使用了 SNAPSHOT 版本部署到生产环境
**解决**：
- 修改 `pom.xml` 中的版本号，移除 `-SNAPSHOT` 后缀
- 重新打包：`./build-prod.sh` 或 `mvn clean package -DskipTests`

### 2. 数据库连接失败
- 检查MySQL服务是否启动
- 检查数据库连接参数
- 检查网络连通性
- 确认使用了 `--spring.profiles.active=prod` 参数

### 3. 端口占用
- 检查8080端口是否被占用：`netstat -tlnp | grep 8080`
- 修改端口：`--server.port=8081`

### 4. 内存不足
- 调整JVM参数：`-Xms256m -Xmx512m`

### 5. 配置文件问题
**问题**：启动时没有指定生产环境配置
**解决**：确保启动命令包含 `--spring.profiles.active=prod`

## 监控和维护

### 日志轮转
建议配置logrotate进行日志轮转：
```bash
# /etc/logrotate.d/wms-backend
/root/workspace/serve/logs/app.log {
    daily
    rotate 7
    compress
    delaycompress
    missingok
    notifempty
    copytruncate
}
```
