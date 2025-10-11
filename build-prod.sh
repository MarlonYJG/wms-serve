#!/bin/bash

# WMS后端生产环境打包脚本
# 使用方法: ./build-prod.sh

set -e  # 遇到错误立即退出

echo "=========================================="
echo "WMS后端生产环境打包脚本"
echo "=========================================="
echo

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印带颜色的消息
print_step() {
    echo -e "${BLUE}[$1]${NC} $2"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

# 检查环境
print_step "1/6" "检查环境..."

# 检查Java
if ! command -v java &> /dev/null; then
    print_error "Java未安装或未配置到PATH"
    exit 1
fi
echo "Java版本: $(java -version 2>&1 | head -n 1)"

# 检查Maven
if ! command -v mvn &> /dev/null; then
    print_error "Maven未安装或未配置到PATH"
    exit 1
fi
echo "Maven版本: $(mvn -version | head -n 1)"

echo

# 清理之前的构建
print_step "2/6" "清理之前的构建..."
mvn clean

echo

# 运行测试
print_step "3/6" "运行测试..."
if ! mvn test; then
    print_warning "测试失败，是否继续打包？(y/N)"
    read -r continue
    if [[ ! "$continue" =~ ^[Yy]$ ]]; then
        print_error "打包已取消"
        exit 1
    fi
fi

echo

# 编译并打包
print_step "4/6" "编译并打包JAR文件..."
mvn package -DskipTests

echo

# 检查打包结果
print_step "5/6" "检查打包结果..."
if [ -f "target/wms-backend-0.0.1-SNAPSHOT.jar" ]; then
    print_success "打包成功！"
    echo
    echo "JAR文件信息:"
    ls -lh target/wms-backend-0.0.1-SNAPSHOT.jar
    
    # 创建发布目录
    print_step "6/6" "创建发布包..."
    mkdir -p release
    
    # 复制文件到发布目录
    cp target/wms-backend-0.0.1-SNAPSHOT.jar release/
    cp src/main/resources/application-prod.yml release/
    cp start-prod-server.sh release/
    cp DEPLOYMENT.md release/
    
    # 设置脚本执行权限
    chmod +x release/start-prod-server.sh
    
    echo
    echo "发布文件已复制到 release/ 目录:"
    ls -la release/
    
    echo
    echo "=========================================="
    echo -e "${GREEN}🎉 生产环境打包完成！${NC}"
    echo "=========================================="
    echo
    echo -e "${BLUE}📁 发布文件位置:${NC} release/"
    echo -e "${BLUE}📦 JAR文件:${NC} release/wms-backend-0.0.1-SNAPSHOT.jar"
    echo -e "${BLUE}📋 配置文件:${NC} release/application-prod.yml"
    echo -e "${BLUE}🚀 启动脚本:${NC} release/start-prod-server.sh"
    echo -e "${BLUE}📖 部署文档:${NC} release/DEPLOYMENT.md"
    echo
    echo -e "${YELLOW}下一步操作:${NC}"
    echo "1. 将 release/ 目录下的文件上传到服务器"
    echo "2. 在服务器上执行: ./start-prod-server.sh"
    echo
    echo -e "${GREEN}打包完成时间:${NC} $(date)"
    
else
    print_error "打包失败！JAR文件未生成"
    echo "请检查错误信息并重试"
    exit 1
fi
