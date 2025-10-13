# WMS系统完整部署指南

## 📋 部署架构概览

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   用户浏览器     │    │   Nginx代理     │    │  Spring Boot    │
│                 │    │                 │    │    后端服务     │
│  https://domain │◄──►│  :80/:443       │◄──►│   :8080         │
│                 │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ▼
                       ┌─────────────────┐
                       │   静态文件      │
                       │   /var/www/     │
                       │   wms-web/      │
                       └─────────────────┘
```

## 🚀 快速部署步骤

### 第一步：后端服务部署

1. **上传后端文件到服务器**
```bash
# 上传release目录到服务器
scp -r wms-serve/release/ root@your-server:/root/workspace/serve/
```

2. **启动后端服务**
```bash
# 登录服务器
ssh root@your-server

# 进入应用目录
cd /root/workspace/serve

# 给启动脚本执行权限
chmod +x start-prod-server.sh

# 启动服务
./start-prod-server.sh

# 验证服务状态
curl http://localhost:8080/api/v1/actuator/health
```

### 第二步：Nginx安装和配置

1. **安装Nginx**
```bash
# CentOS/RHEL
sudo yum install -y epel-release nginx

# Ubuntu/Debian
sudo apt update && sudo apt install -y nginx
```

2. **部署Nginx配置**
```bash
# 复制配置文件
sudo cp wms-serve/nginx/nginx.conf /etc/nginx/nginx.conf

# 修改域名配置
sudo sed -i 's/your-domain.com/wms.yourcompany.com/g' /etc/nginx/nginx.conf
sudo sed -i 's/www.your-domain.com/www.wms.yourcompany.com/g' /etc/nginx/nginx.conf

# 测试配置
sudo nginx -t

# 启动Nginx
sudo systemctl start nginx
sudo systemctl enable nginx
```

### 第三步：SSL证书配置

1. **使用Let's Encrypt免费证书（推荐）**
```bash
# 安装Certbot
sudo yum install -y certbot python3-certbot-nginx  # CentOS
# 或
sudo apt install -y certbot python3-certbot-nginx  # Ubuntu

# 申请证书
sudo certbot --nginx -d wms.yourcompany.com -d www.wms.yourcompany.com

# 设置自动续期
echo "0 12 * * * /usr/bin/certbot renew --quiet" | sudo crontab -
```

2. **或使用SSL配置脚本**
```bash
# 给脚本执行权限
chmod +x wms-serve/nginx/ssl-setup.sh

# 申请Let's Encrypt证书
sudo ./wms-serve/nginx/ssl-setup.sh letsencrypt wms.yourcompany.com admin@yourcompany.com

# 或生成自签名证书（仅测试用）
sudo ./wms-serve/nginx/ssl-setup.sh self-signed wms.yourcompany.com
```

### 第四步：前端构建和部署

1. **本地构建前端**
```bash
# 进入前端目录
cd wms-web

# 给构建脚本执行权限
chmod +x build-prod.sh

# 构建并上传到服务器
./build-prod.sh -m production -s root@your-server -p /var/www/wms-web
```

2. **或使用Windows批处理脚本**
```cmd
# 在Windows命令行中执行
build-prod.bat -m production -s root@your-server -p /var/www/wms-web
```

3. **手动构建和上传**
```bash
# 构建前端
npm run build
# 或
pnpm build

# 上传到服务器
scp -r dist/* root@your-server:/var/www/wms-web/

# 设置权限
ssh root@your-server "chown -R nginx:nginx /var/www/wms-web && chmod -R 755 /var/www/wms-web"
```

## 🔧 配置说明

### 关键配置文件

1. **Nginx配置** (`/etc/nginx/nginx.conf`)
   - 前端静态文件服务
   - 后端API代理 (`/api/` → `http://127.0.0.1:8080`)
   - SSL/HTTPS配置
   - 安全头配置

2. **后端配置** (`/root/workspace/serve/application-prod.yml`)
   - 数据库连接配置
   - JWT配置
   - 日志配置

3. **前端配置** (构建时生成)
   - API基础URL: `/api/v1`
   - 环境变量配置

### 端口配置

- **80**: HTTP (重定向到HTTPS)
- **443**: HTTPS (主要访问端口)
- **8080**: 后端服务 (内部访问)

## 📊 验证部署

### 1. 检查服务状态
```bash
# 检查后端服务
ps aux | grep wms-backend
curl http://localhost:8080/api/v1/actuator/health

# 检查Nginx服务
sudo systemctl status nginx
sudo nginx -t

# 检查端口监听
sudo netstat -tlnp | grep -E ':(80|443|8080)'
```

### 2. 测试访问
```bash
# 测试HTTP重定向
curl -I http://wms.yourcompany.com

# 测试HTTPS访问
curl -I https://wms.yourcompany.com

# 测试API接口
curl https://wms.yourcompany.com/api/v1/actuator/health
```

### 3. 浏览器访问
- 打开浏览器访问: `https://wms.yourcompany.com`
- 检查页面是否正常加载
- 测试登录功能
- 检查API请求是否正常

## 🔄 更新部署

### 后端更新
```bash
# 1. 停止当前服务
kill $(cat /root/workspace/serve/app.pid)

# 2. 备份当前版本
cp /root/workspace/serve/wms-backend-0.0.1-SNAPSHOT.jar /root/workspace/serve/wms-backend-0.0.1-SNAPSHOT.jar.backup

# 3. 上传新版本
scp target/wms-backend-0.0.1-SNAPSHOT.jar root@your-server:/root/workspace/serve/

# 4. 启动新版本
cd /root/workspace/serve && ./start-prod-server.sh
```

### 前端更新
```bash
# 使用构建脚本自动更新
cd wms-web
./build-prod.sh -m production -s root@your-server -p /var/www/wms-web

# 或手动更新
npm run build
scp -r dist/* root@your-server:/var/www/wms-web/
```

### Nginx配置更新
```bash
# 1. 备份配置
sudo cp /etc/nginx/nginx.conf /etc/nginx/nginx.conf.backup

# 2. 更新配置
sudo cp new-nginx.conf /etc/nginx/nginx.conf

# 3. 测试配置
sudo nginx -t

# 4. 重载配置
sudo nginx -s reload
```

## 🚨 故障排除

### 常见问题及解决方案

#### 1. 后端服务无法启动
```bash
# 检查Java版本
java -version

# 检查端口占用
sudo netstat -tlnp | grep 8080

# 查看错误日志
tail -f /root/workspace/serve/logs/app.log

# 检查数据库连接
mysql -h 8.137.116.128 -u wms -p wms_db
```

#### 2. Nginx无法启动
```bash
# 检查配置文件语法
sudo nginx -t

# 查看错误日志
sudo tail -f /var/log/nginx/error.log

# 检查端口占用
sudo netstat -tlnp | grep -E ':(80|443)'

# 检查SSL证书
sudo ls -la /etc/nginx/ssl/
sudo openssl x509 -in /etc/nginx/ssl/your-domain.com.crt -text -noout
```

#### 3. 前端页面无法访问
```bash
# 检查静态文件
ls -la /var/www/wms-web/

# 检查文件权限
sudo chown -R nginx:nginx /var/www/wms-web
sudo chmod -R 755 /var/www/wms-web

# 检查Nginx配置
sudo nginx -t
sudo nginx -s reload
```

#### 4. API请求失败
```bash
# 检查后端服务状态
curl http://localhost:8080/api/v1/actuator/health

# 检查Nginx代理配置
sudo grep -A 10 "location /api/" /etc/nginx/nginx.conf

# 查看Nginx访问日志
sudo tail -f /var/log/nginx/access.log
```

## 📈 性能优化

### 1. Nginx优化
```nginx
# 在nginx.conf中添加
worker_processes auto;
worker_connections 1024;

# 启用gzip压缩
gzip on;
gzip_vary on;
gzip_min_length 1024;
gzip_comp_level 6;

# 启用缓存
location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}
```

### 2. 后端优化
```yaml
# 在application-prod.yml中调整
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
```

### 3. 前端优化
```bash
# 使用生产环境构建
npm run build

# 启用代码分割和压缩
# 已在vite.config.prod.ts中配置
```

## 🔐 安全配置

### 1. 防火墙配置
```bash
# 开放必要端口
sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --reload
```

### 2. SSL安全配置
```nginx
# 在nginx.conf中配置
ssl_protocols TLSv1.2 TLSv1.3;
ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384;
ssl_prefer_server_ciphers on;
add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
```

### 3. 访问控制
```nginx
# 限制管理接口访问
location /api/v1/admin/ {
    allow 192.168.1.0/24;
    deny all;
}
```

## 📞 技术支持

### 日志文件位置
- **后端日志**: `/root/workspace/serve/logs/app.log`
- **Nginx访问日志**: `/var/log/nginx/access.log`
- **Nginx错误日志**: `/var/log/nginx/error.log`
- **系统日志**: `/var/log/messages` 或 `/var/log/syslog`

### 监控命令
```bash
# 查看系统资源使用
top
free -h
df -h

# 查看服务状态
sudo systemctl status nginx
ps aux | grep wms-backend

# 查看网络连接
sudo netstat -tlnp
sudo ss -tlnp
```

### 联系信息
如果遇到问题，请提供：
1. 错误日志内容
2. 系统环境信息
3. 配置文件名和内容
4. 问题复现步骤

---

**部署完成后，您的WMS系统将通过以下地址访问：**
- **前端**: `https://wms.yourcompany.com`
- **后端API**: `https://wms.yourcompany.com/api/v1`
- **健康检查**: `https://wms.yourcompany.com/api/v1/actuator/health`

🎉 **恭喜！WMS系统部署完成！**
