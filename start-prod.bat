@echo off
echo 启动WMS后端服务...
echo 使用生产环境配置（MySQL数据库）
echo.
echo 请确保：
echo 1. MySQL数据库已启动
echo 2. 数据库 wms_db 已创建
echo 3. 用户 wms 有相应权限
echo.
java -jar target\wms-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
pause
