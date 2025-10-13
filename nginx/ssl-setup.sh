#!/bin/bash

# WMS系统SSL证书配置脚本
# 支持Let's Encrypt和自签名证书两种方式

set -e

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查是否为root用户
check_root() {
    if [[ $EUID -ne 0 ]]; then
        log_error "此脚本需要root权限运行"
        exit 1
    fi
}

# 检查系统类型
check_system() {
    if [[ -f /etc/redhat-release ]]; then
        OS="centos"
        log_info "检测到CentOS/RHEL系统"
    elif [[ -f /etc/debian_version ]]; then
        OS="ubuntu"
        log_info "检测到Ubuntu/Debian系统"
    else
        log_error "不支持的操作系统"
        exit 1
    fi
}

# 安装Certbot
install_certbot() {
    log_info "安装Certbot..."
    
    if [[ $OS == "centos" ]]; then
        yum install -y epel-release
        yum install -y certbot python3-certbot-nginx
    elif [[ $OS == "ubuntu" ]]; then
        apt update
        apt install -y certbot python3-certbot-nginx
    fi
    
    log_success "Certbot安装完成"
}

# 申请Let's Encrypt证书
setup_letsencrypt() {
    local domain=$1
    local email=$2
    
    log_info "开始申请Let's Encrypt证书..."
    log_info "域名: $domain"
    log_info "邮箱: $email"
    
    # 检查域名解析
    log_info "检查域名解析..."
    if ! nslookup $domain > /dev/null 2>&1; then
        log_error "域名 $domain 解析失败，请检查DNS配置"
        exit 1
    fi
    
    # 申请证书
    certbot --nginx -d $domain --email $email --agree-tos --non-interactive
    
    # 设置自动续期
    log_info "设置证书自动续期..."
    (crontab -l 2>/dev/null; echo "0 12 * * * /usr/bin/certbot renew --quiet") | crontab -
    
    log_success "Let's Encrypt证书配置完成"
    log_info "证书位置: /etc/letsencrypt/live/$domain/"
}

# 生成自签名证书
setup_self_signed() {
    local domain=$1
    
    log_info "生成自签名证书..."
    log_warning "自签名证书仅用于测试环境，生产环境请使用正式证书"
    
    # 创建SSL目录
    mkdir -p /etc/nginx/ssl
    
    # 生成私钥
    log_info "生成私钥..."
    openssl genrsa -out /etc/nginx/ssl/$domain.key 2048
    
    # 生成证书签名请求
    log_info "生成证书签名请求..."
    openssl req -new -key /etc/nginx/ssl/$domain.key -out /etc/nginx/ssl/$domain.csr -subj "/C=CN/ST=Beijing/L=Beijing/O=WMS/OU=IT/CN=$domain"
    
    # 生成自签名证书
    log_info "生成自签名证书..."
    openssl x509 -req -days 365 -in /etc/nginx/ssl/$domain.csr -signkey /etc/nginx/ssl/$domain.key -out /etc/nginx/ssl/$domain.crt
    
    # 设置权限
    chmod 600 /etc/nginx/ssl/$domain.key
    chmod 644 /etc/nginx/ssl/$domain.crt
    
    # 清理CSR文件
    rm /etc/nginx/ssl/$domain.csr
    
    log_success "自签名证书生成完成"
    log_info "证书位置: /etc/nginx/ssl/$domain.crt"
    log_info "私钥位置: /etc/nginx/ssl/$domain.key"
}

# 验证证书
verify_certificate() {
    local domain=$1
    local cert_path=$2
    
    log_info "验证证书..."
    
    if [[ -f $cert_path ]]; then
        # 显示证书信息
        openssl x509 -in $cert_path -text -noout | grep -E "(Subject:|Not Before|Not After|Issuer:)"
        
        # 检查证书有效期
        local expiry_date=$(openssl x509 -in $cert_path -enddate -noout | cut -d= -f2)
        log_info "证书有效期至: $expiry_date"
        
        log_success "证书验证通过"
    else
        log_error "证书文件不存在: $cert_path"
        exit 1
    fi
}

# 更新Nginx配置
update_nginx_config() {
    local domain=$1
    local cert_type=$2
    
    log_info "更新Nginx配置..."
    
    # 备份原配置
    cp /etc/nginx/nginx.conf /etc/nginx/nginx.conf.backup.$(date +%Y%m%d_%H%M%S)
    
    # 根据证书类型更新配置
    if [[ $cert_type == "letsencrypt" ]]; then
        # Let's Encrypt证书路径
        local cert_path="/etc/letsencrypt/live/$domain/fullchain.pem"
        local key_path="/etc/letsencrypt/live/$domain/privkey.pem"
    else
        # 自签名证书路径
        local cert_path="/etc/nginx/ssl/$domain.crt"
        local key_path="/etc/nginx/ssl/$domain.key"
    fi
    
    # 更新配置文件中的证书路径
    sed -i "s|/etc/nginx/ssl/your-domain.com.crt|$cert_path|g" /etc/nginx/nginx.conf
    sed -i "s|/etc/nginx/ssl/your-domain.com.key|$key_path|g" /etc/nginx/nginx.conf
    sed -i "s|your-domain.com|$domain|g" /etc/nginx/nginx.conf
    sed -i "s|www.your-domain.com|www.$domain|g" /etc/nginx/nginx.conf
    
    # 测试配置
    if nginx -t; then
        log_success "Nginx配置更新成功"
    else
        log_error "Nginx配置有误，请检查"
        exit 1
    fi
}

# 重启Nginx
restart_nginx() {
    log_info "重启Nginx服务..."
    
    systemctl restart nginx
    
    if systemctl is-active --quiet nginx; then
        log_success "Nginx重启成功"
    else
        log_error "Nginx重启失败"
        exit 1
    fi
}

# 显示使用说明
show_usage() {
    echo "WMS系统SSL证书配置脚本"
    echo ""
    echo "用法:"
    echo "  $0 letsencrypt <域名> <邮箱>     # 申请Let's Encrypt证书"
    echo "  $0 self-signed <域名>           # 生成自签名证书"
    echo ""
    echo "示例:"
    echo "  $0 letsencrypt wms.example.com admin@example.com"
    echo "  $0 self-signed wms.example.com"
    echo ""
}

# 主函数
main() {
    echo "=========================================="
    echo "    WMS系统SSL证书配置脚本"
    echo "=========================================="
    echo ""
    
    # 检查参数
    if [[ $# -lt 2 ]]; then
        show_usage
        exit 1
    fi
    
    local cert_type=$1
    local domain=$2
    local email=$3
    
    # 检查root权限
    check_root
    
    # 检查系统类型
    check_system
    
    # 根据证书类型执行相应操作
    case $cert_type in
        "letsencrypt")
            if [[ -z $email ]]; then
                log_error "Let's Encrypt证书需要提供邮箱地址"
                show_usage
                exit 1
            fi
            
            install_certbot
            setup_letsencrypt $domain $email
            update_nginx_config $domain "letsencrypt"
            verify_certificate $domain "/etc/letsencrypt/live/$domain/fullchain.pem"
            ;;
        "self-signed")
            setup_self_signed $domain
            update_nginx_config $domain "self-signed"
            verify_certificate $domain "/etc/nginx/ssl/$domain.crt"
            ;;
        *)
            log_error "不支持的证书类型: $cert_type"
            show_usage
            exit 1
            ;;
    esac
    
    # 重启Nginx
    restart_nginx
    
    echo ""
    log_success "SSL证书配置完成！"
    echo ""
    echo "访问地址:"
    echo "  HTTP:  http://$domain"
    echo "  HTTPS: https://$domain"
    echo ""
    echo "证书信息:"
    if [[ $cert_type == "letsencrypt" ]]; then
        echo "  类型: Let's Encrypt"
        echo "  位置: /etc/letsencrypt/live/$domain/"
        echo "  自动续期: 已配置"
    else
        echo "  类型: 自签名证书"
        echo "  位置: /etc/nginx/ssl/$domain.crt"
        echo "  注意: 浏览器会显示安全警告"
    fi
    echo ""
}

# 执行主函数
main "$@"
