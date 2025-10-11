# 🚀 WMS后端快速部署指南

## 📦 一键打包发布

### Windows环境
```cmd
# 1. 进入项目目录
cd wms-serve

# 2. 执行打包脚本
build-prod.bat

# 3. 查看发布文件
dir release\
```

### Linux/Mac环境
```bash
# 1. 进入项目目录
cd wms-serve

# 2. 给脚本执行权限
chmod +x build-prod.sh

# 3. 执行打包脚本
./build-prod.sh

# 4. 查看发布文件
ls -la release/
```

## 🏃‍♂️ 服务器部署

### 上传文件到服务器
```bash
# 方法1: 使用SCP上传整个release目录
scp -r release/ root@your-server:/root/workspace/serve/

# 方法2: 使用rsync同步
rsync -avz release/ root@your-server:/root/workspace/serve/
```

### 在服务器上启动
```bash
# 1. 登录服务器
ssh root@your-server

# 2. 进入应用目录
cd /root/workspace/serve

# 3. 设置权限
chmod +x start-prod-server.sh

# 4. 启动应用
./start-prod-server.sh
```

## ✅ 验证部署

### 检查应用状态
```bash
# 查看进程
ps aux | grep wms-backend

# 查看端口
netstat -tlnp | grep 8080

# 健康检查
curl http://localhost:8080/api/v1/actuator/health
```

### 查看日志
```bash
# 实时查看日志
tail -f logs/app.log

# 查看错误日志
grep ERROR logs/app.log
```

## 🔧 常用命令

### 应用管理
```bash
# 停止应用
kill $(cat app.pid)

# 重启应用
./start-prod-server.sh

# 查看应用状态
ps aux | grep wms-backend
```

### 日志管理
```bash
# 查看最新日志
tail -n 100 logs/app.log

# 搜索特定错误
grep -i "error\|exception" logs/app.log

# 查看启动日志
head -n 50 logs/app.log
```

## 📋 部署检查清单

### 部署前检查
- [ ] Java 21 已安装
- [ ] MySQL数据库已准备就绪
- [ ] 网络连通性正常
- [ ] 服务器资源充足（内存≥2GB）

### 部署后验证
- [ ] 应用进程正在运行
- [ ] 8080端口已监听
- [ ] 健康检查接口正常
- [ ] 数据库连接正常
- [ ] 日志无错误信息

## 🚨 故障排除

### 常见问题
1. **应用启动失败** → 检查日志文件 `logs/app.log`
2. **数据库连接失败** → 检查MySQL服务和网络连通性
3. **端口被占用** → 使用 `netstat -tlnp | grep 8080` 检查
4. **内存不足** → 调整JVM参数或增加服务器内存

### 紧急回滚
```bash
# 1. 停止当前应用
kill $(cat app.pid)

# 2. 恢复备份版本
cp wms-backend-0.0.1-SNAPSHOT.jar.backup wms-backend-0.0.1-SNAPSHOT.jar

# 3. 重新启动
./start-prod-server.sh
```

---

**🎉 部署完成后，您的WMS后端服务将在 `http://your-server:8080/api/v1` 提供服务！**
