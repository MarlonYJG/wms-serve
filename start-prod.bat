@echo off
echo 启动WMS后端服务...
echo 使用生产环境配置
java -jar target\wms-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
pause
