#!/bin/bash

# WMS后端生产环境启动脚本
# 使用方法: ./start-prod-server.sh

echo "=========================================="
echo "启动WMS后端服务 - 生产环境"
echo "=========================================="

# 检查JAR文件是否存在
JAR_FILE="wms-backend-0.0.1-SNAPSHOT.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "错误: JAR文件 $JAR_FILE 不存在！"
    echo "请先执行 mvn clean package 打包项目"
    exit 1
fi

# 创建日志目录
LOG_DIR="/root/workspace/serve/logs"
mkdir -p "$LOG_DIR"

# 设置JVM参数
JVM_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps"

# 设置环境变量
export SPRING_PROFILES_ACTIVE=prod

echo "JAR文件: $JAR_FILE"
echo "日志目录: $LOG_DIR"
echo "环境配置: $SPRING_PROFILES_ACTIVE"
echo "JVM参数: $JVM_OPTS"
echo ""

# 启动应用
echo "正在启动应用..."
nohup java $JVM_OPTS -jar "$JAR_FILE" --spring.profiles.active=prod > "$LOG_DIR/app.log" 2>&1 &

# 获取进程ID
PID=$!
echo "应用已启动，进程ID: $PID"
echo "日志文件: $LOG_DIR/app.log"

# 等待几秒钟检查启动状态
sleep 5

# 检查进程是否还在运行
if ps -p $PID > /dev/null; then
    echo "✅ 应用启动成功！"
    echo "进程ID: $PID"
    echo "查看日志: tail -f $LOG_DIR/app.log"
    echo "停止应用: kill $PID"
else
    echo "❌ 应用启动失败！"
    echo "请查看日志文件: $LOG_DIR/app.log"
    exit 1
fi

echo "=========================================="
