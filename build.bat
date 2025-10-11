@echo off
echo 开始打包WMS后端服务...
echo.

echo 清理之前的构建...
call mvn clean

echo.
echo 编译并打包JAR文件...
call mvn package -DskipTests

echo.
if exist target\wms-backend-0.0.1-SNAPSHOT.jar (
    echo 打包成功！
    echo JAR文件位置: target\wms-backend-0.0.1-SNAPSHOT.jar
    echo 文件大小:
    dir target\wms-backend-0.0.1-SNAPSHOT.jar
    echo.
    echo 可以使用以下命令启动服务:
    echo   java -jar target\wms-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
    echo 或者直接运行 start.bat
) else (
    echo 打包失败！请检查错误信息。
)

echo.
pause
