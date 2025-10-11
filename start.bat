@echo off
echo 启动WMS后端服务...
echo 使用开发环境配置
java -jar target\wms-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
pause
