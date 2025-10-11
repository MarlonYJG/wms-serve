# WMS后端生产环境部署完整指南

## 📋 部署前准备清单

### 1. 服务器环境要求
- [ ] **操作系统**: Linux (推荐 CentOS 7+/Ubuntu 18+)
- [ ] **Java版本**: OpenJDK 21 或 Oracle JDK 21
- [ ] **内存**: 最少 2GB，推荐 4GB+
- [ ] **磁盘空间**: 最少 10GB 可用空间
- [ ] **网络**: 能访问MySQL数据库服务器

### 2. 数据库准备
```sql
-- 1. 创建数据库
CREATE DATABASE wms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. 创建用户并授权
CREATE USER 'wms'@'%' IDENTIFIED BY 'Wms!Passw0rd';
GRANT ALL PRIVILEGES ON wms_db.* TO 'wms'@'%';
FLUSH PRIVILEGES;

-- 3. 验证连接
SHOW DATABASES;
SELECT USER(), DATABASE();
```

### 3. 网络配置
- [ ] 确保服务器能访问MySQL数据库 (8.137.116.128:3306)
- [ ] 确保8080端口对外开放（或修改为其他端口）
- [ ] 配置防火墙规则

## 🚀 打包发布流程

### 步骤1: 本地打包

#### Windows环境
```cmd
# 进入项目目录
cd wms-serve

# 执行打包脚本
build-prod.bat
```

#### Linux/Mac环境
```bash
# 进入项目目录
cd wms-serve

# 给脚本执行权限
chmod +x build-prod.sh

# 执行打包脚本
./build-prod.sh
```

#### 手动打包（可选）
```bash
# 清理并打包
mvn clean package -DskipTests

# 检查生成的JAR文件
ls -la target/wms-backend-0.0.1-SNAPSHOT.jar
```

### 步骤2: 上传文件到服务器

#### 方法一: 使用SCP
```bash
# 上传整个release目录
scp -r release/ root@your-server:/root/workspace/serve/

# 或者只上传必要文件
scp target/wms-backend-0.0.1-SNAPSHOT.jar root@your-server:/root/workspace/serve/
scp src/main/resources/application-prod.yml root@your-server:/root/workspace/serve/
scp start-prod-server.sh root@your-server:/root/workspace/serve/
```

#### 方法二: 使用SFTP
```bash
sftp root@your-server
put target/wms-backend-0.0.1-SNAPSHOT.jar /root/workspace/serve/
put src/main/resources/application-prod.yml /root/workspace/serve/
put start-prod-server.sh /root/workspace/serve/
quit
```

#### 方法三: 使用rsync
```bash
rsync -avz release/ root@your-server:/root/workspace/serve/
```

## 🏃‍♂️ 服务器部署步骤

### 步骤1: 登录服务器
```bash
ssh root@your-server
```

### 步骤2: 创建目录结构
```bash
# 创建应用目录
mkdir -p /root/workspace/serve
mkdir -p /root/workspace/serve/logs

# 进入应用目录
cd /root/workspace/serve
```

### 步骤3: 上传文件（如果还没上传）
```bash
# 使用wget下载（如果有HTTP服务器）
# wget http://your-build-server/wms-backend-0.0.1-SNAPSHOT.jar

# 或者使用scp从本地上传
# scp user@local-machine:/path/to/wms-backend-0.0.1-SNAPSHOT.jar .
```

### 步骤4: 设置权限
```bash
# 给启动脚本执行权限
chmod +x start-prod-server.sh

# 检查文件权限
ls -la
```

### 步骤5: 启动应用

#### 方法一: 使用启动脚本（推荐）
```bash
./start-prod-server.sh
```

#### 方法二: 直接使用命令
```bash
# 设置环境变量
export SPRING_PROFILES_ACTIVE=prod

# 启动应用
nohup java -Xms512m -Xmx1024m -jar wms-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod > logs/app.log 2>&1 &

# 获取进程ID
echo $! > app.pid
```

### 步骤6: 验证部署

#### 检查应用状态
```bash
# 查看进程
ps aux | grep wms-backend

# 查看端口
netstat -tlnp | grep 8080

# 查看日志
tail -f logs/app.log
```

#### 健康检查
```bash
# 检查应用健康状态
curl http://localhost:8080/api/v1/actuator/health

# 或者使用wget
wget -qO- http://localhost:8080/api/v1/actuator/health
```

## 🔧 配置说明

### 生产环境配置 (application-prod.yml)
```yaml
spring:
  datasource:
    url: jdbc:mysql://8.137.116.128:3306/wms_db?useUnicode=true&characterEncoding=utf8&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=Asia/Shanghai
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: wms
    password: Wms!Passw0rd
    
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  jpa:
    hibernate:
      ddl-auto: validate  # 生产环境只验证表结构
    show-sql: false       # 生产环境不显示SQL
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect

logging:
  level:
    root: WARN
    com.bj.wms: INFO
  file:
    name: logs/wms-backend.log
```

### JVM参数说明
- `-Xms512m`: 初始堆内存512MB
- `-Xmx1024m`: 最大堆内存1GB
- `-XX:+UseG1GC`: 使用G1垃圾收集器
- `-XX:+PrintGCDetails`: 打印GC详情

## 📊 监控和维护

### 日志管理
```bash
# 查看实时日志
tail -f logs/app.log

# 查看错误日志
grep ERROR logs/app.log

# 查看最近的日志
tail -n 100 logs/app.log

# 日志轮转（推荐配置logrotate）
```

### 性能监控
```bash
# 查看内存使用
free -h

# 查看CPU使用
top -p $(cat app.pid)

# 查看磁盘使用
df -h

# 查看网络连接
netstat -an | grep 8080
```

### 应用管理
```bash
# 停止应用
kill $(cat app.pid)

# 重启应用
./start-prod-server.sh

# 查看应用状态
systemctl status wms-backend  # 如果配置了systemd服务
```

## 🚨 故障排除

### 常见问题及解决方案

#### 1. 应用启动失败
```bash
# 查看详细错误日志
tail -n 50 logs/app.log

# 检查Java版本
java -version

# 检查端口占用
netstat -tlnp | grep 8080
```

#### 2. 数据库连接失败
```bash
# 测试数据库连接
mysql -h 8.137.116.128 -u wms -p wms_db

# 检查网络连通性
ping 8.137.116.128
telnet 8.137.116.128 3306
```

#### 3. 内存不足
```bash
# 调整JVM参数
java -Xms256m -Xmx512m -jar wms-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

#### 4. 端口冲突
```bash
# 修改端口启动
java -jar wms-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod --server.port=8081
```

## 🔄 更新部署

### 滚动更新流程
```bash
# 1. 备份当前版本
cp wms-backend-0.0.1-SNAPSHOT.jar wms-backend-0.0.1-SNAPSHOT.jar.backup

# 2. 停止当前应用
kill $(cat app.pid)

# 3. 替换JAR文件
cp new-wms-backend-0.0.1-SNAPSHOT.jar wms-backend-0.0.1-SNAPSHOT.jar

# 4. 启动新版本
./start-prod-server.sh

# 5. 验证新版本
curl http://localhost:8080/api/v1/actuator/health
```

## 📞 技术支持

如果遇到问题，请提供以下信息：
1. 错误日志 (`logs/app.log`)
2. 系统信息 (`uname -a`, `java -version`)
3. 网络配置 (`netstat -tlnp`)
4. 数据库连接测试结果

---

**部署完成后，您的WMS后端服务将在 `http://your-server:8080/api/v1` 提供服务！**
