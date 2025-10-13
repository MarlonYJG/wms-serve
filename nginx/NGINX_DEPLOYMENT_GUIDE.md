# WMS系统Nginx部署完整指南

## 📋 部署前准备

### 1. 服务器环境要求
- [ ] **操作系统**: Linux (推荐 CentOS 7+/Ubuntu 18+)
- [ ] **Nginx版本**: 1.18+ (支持HTTP/2)
- [ ] **内存**: 最少 1GB，推荐 2GB+
- [ ] **磁盘空间**: 最少 5GB 可用空间
- [ ] **网络**: 能访问后端服务 (127.0.0.1:8080)

### 2. 域名和SSL证书准备
- [ ] 准备域名 (如: wms.yourcompany.com)
- [ ] 申请SSL证书 (推荐使用Let's Encrypt免费证书)
- [ ] 确保域名已解析到服务器IP

## 🚀 Nginx安装和配置

### 步骤1: 安装Nginx

#### CentOS/RHEL系统
```bash
# 安装EPEL仓库
sudo yum install -y epel-release

# 安装Nginx
sudo yum install -y nginx

# 启动并设置开机自启
sudo systemctl start nginx
sudo systemctl enable nginx
```

#### Ubuntu/Debian系统
```bash
# 更新包列表
sudo apt update

# 安装Nginx
sudo apt install -y nginx

# 启动并设置开机自启
sudo systemctl start nginx
sudo systemctl enable nginx
```

### 步骤2: 创建目录结构
```bash
# 创建WMS相关目录
sudo mkdir -p /var/www/wms-web
sudo mkdir -p /etc/nginx/ssl
sudo mkdir -p /var/log/nginx

# 设置权限
sudo chown -R nginx:nginx /var/www/wms-web
sudo chmod -R 755 /var/www/wms-web
```

### 步骤3: 配置SSL证书

#### 方法一: 使用Let's Encrypt免费证书（推荐）
```bash
# 安装Certbot
# CentOS/RHEL
sudo yum install -y certbot python3-certbot-nginx

# Ubuntu/Debian
sudo apt install -y certbot python3-certbot-nginx

# 申请证书（替换为你的域名）
sudo certbot --nginx -d your-domain.com -d www.your-domain.com

# 设置自动续期
sudo crontab -e
# 添加以下行
0 12 * * * /usr/bin/certbot renew --quiet
```

#### 方法二: 使用自签名证书（仅用于测试）
```bash
# 生成私钥
sudo openssl genrsa -out /etc/nginx/ssl/your-domain.com.key 2048

# 生成证书签名请求
sudo openssl req -new -key /etc/nginx/ssl/your-domain.com.key -out /etc/nginx/ssl/your-domain.com.csr

# 生成自签名证书
sudo openssl x509 -req -days 365 -in /etc/nginx/ssl/your-domain.com.csr -signkey /etc/nginx/ssl/your-domain.com.key -out /etc/nginx/ssl/your-domain.com.crt

# 设置权限
sudo chmod 600 /etc/nginx/ssl/your-domain.com.key
sudo chmod 644 /etc/nginx/ssl/your-domain.com.crt
```

### 步骤4: 部署Nginx配置

#### 备份原配置
```bash
# 备份原配置文件
sudo cp /etc/nginx/nginx.conf /etc/nginx/nginx.conf.backup
```

#### 部署新配置
```bash
# 复制WMS配置文件
sudo cp nginx/nginx.conf /etc/nginx/nginx.conf

# 或者创建站点配置文件
sudo cp nginx/nginx.conf /etc/nginx/sites-available/wms
sudo ln -s /etc/nginx/sites-available/wms /etc/nginx/sites-enabled/
```

#### 修改配置中的域名
```bash
# 编辑配置文件
sudo nano /etc/nginx/nginx.conf

# 将以下内容替换为你的实际域名：
# your-domain.com -> wms.yourcompany.com
# www.your-domain.com -> www.wms.yourcompany.com
```

### 步骤5: 部署前端静态文件

#### 构建前端项目
```bash
# 进入前端项目目录
cd wms-web

# 安装依赖
npm install
# 或使用pnpm
pnpm install

# 构建生产版本
npm run build
# 或
pnpm build
```

#### 上传静态文件
```bash
# 上传构建后的文件到服务器
scp -r dist/* root@your-server:/var/www/wms-web/

# 或者使用rsync
rsync -avz dist/ root@your-server:/var/www/wms-web/
```

### 步骤6: 测试和启动Nginx

#### 测试配置文件
```bash
# 测试Nginx配置语法
sudo nginx -t

# 如果配置正确，应该看到：
# nginx: the configuration file /etc/nginx/nginx.conf syntax is ok
# nginx: configuration file /etc/nginx/nginx.conf test is successful
```

#### 重启Nginx服务
```bash
# 重启Nginx
sudo systemctl restart nginx

# 检查服务状态
sudo systemctl status nginx

# 查看Nginx进程
ps aux | grep nginx
```

## 🔧 配置说明

### 主要配置项说明

#### 1. 上游服务器配置
```nginx
upstream wms_backend {
    server 127.0.0.1:8080;  # 后端服务地址
    keepalive 32;           # 保持连接数
}
```

#### 2. 前端静态文件配置
```nginx
location / {
    try_files $uri $uri/ /index.html;  # Vue Router History模式支持
}
```

#### 3. API代理配置
```nginx
location /api/ {
    proxy_pass http://wms_backend;     # 代理到后端
    proxy_set_header Host $host;       # 传递Host头
    proxy_set_header X-Real-IP $remote_addr;  # 传递真实IP
}
```

#### 4. SSL安全配置
```nginx
ssl_protocols TLSv1.2 TLSv1.3;        # 支持的SSL协议
ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:...;  # 加密套件
```

## 📊 监控和维护

### 日志管理
```bash
# 查看访问日志
sudo tail -f /var/log/nginx/access.log

# 查看错误日志
sudo tail -f /var/log/nginx/error.log

# 查看特定域名的访问日志
sudo grep "your-domain.com" /var/log/nginx/access.log
```

### 性能监控
```bash
# 查看Nginx状态
sudo systemctl status nginx

# 查看连接数
sudo netstat -an | grep :80 | wc -l
sudo netstat -an | grep :443 | wc -l

# 查看进程信息
sudo ps aux | grep nginx
```

### 配置重载
```bash
# 重载配置（不中断服务）
sudo nginx -s reload

# 测试配置后重载
sudo nginx -t && sudo nginx -s reload
```

## 🚨 故障排除

### 常见问题及解决方案

#### 1. Nginx启动失败
```bash
# 检查配置文件语法
sudo nginx -t

# 查看错误日志
sudo tail -n 50 /var/log/nginx/error.log

# 检查端口占用
sudo netstat -tlnp | grep :80
sudo netstat -tlnp | grep :443
```

#### 2. SSL证书问题
```bash
# 检查证书文件
sudo ls -la /etc/nginx/ssl/

# 验证证书
sudo openssl x509 -in /etc/nginx/ssl/your-domain.com.crt -text -noout

# 检查证书有效期
sudo openssl x509 -in /etc/nginx/ssl/your-domain.com.crt -dates -noout
```

#### 3. 后端连接失败
```bash
# 测试后端服务
curl http://127.0.0.1:8080/api/v1/actuator/health

# 检查后端服务状态
ps aux | grep wms-backend

# 查看后端日志
tail -f /root/workspace/serve/logs/app.log
```

#### 4. 前端页面无法访问
```bash
# 检查静态文件
ls -la /var/www/wms-web/

# 检查文件权限
sudo chown -R nginx:nginx /var/www/wms-web
sudo chmod -R 755 /var/www/wms-web

# 测试静态文件访问
curl -I http://your-domain.com/
```

## 🔄 更新部署

### 前端更新流程
```bash
# 1. 构建新版本
cd wms-web
npm run build

# 2. 备份当前版本
sudo cp -r /var/www/wms-web /var/www/wms-web.backup

# 3. 上传新版本
scp -r dist/* root@your-server:/var/www/wms-web/

# 4. 重载Nginx配置
sudo nginx -s reload
```

### 后端更新流程
```bash
# 1. 更新后端服务（参考后端部署文档）
# 2. 重载Nginx配置
sudo nginx -s reload
```

## 📞 技术支持

如果遇到问题，请提供以下信息：
1. Nginx错误日志 (`/var/log/nginx/error.log`)
2. 系统信息 (`uname -a`, `nginx -v`)
3. 配置文件内容 (`/etc/nginx/nginx.conf`)
4. 网络配置 (`netstat -tlnp`)

---

**部署完成后，您的WMS系统将通过以下地址访问：**
- **前端**: `https://your-domain.com`
- **后端API**: `https://your-domain.com/api/v1`

## 🔐 安全建议

1. **定期更新SSL证书**
2. **配置防火墙规则**
3. **启用访问日志监控**
4. **定期备份配置文件**
5. **使用强密码和密钥**
6. **限制管理端口访问**
