@echo off
echo ==========================================
echo WMS后端生产环境打包脚本
echo ==========================================
echo.

REM 设置编码为UTF-8
chcp 65001 >nul

echo [1/6] 检查环境...
echo 检查Java版本:
java -version
if %errorlevel% neq 0 (
    echo 错误: Java未安装或未配置到PATH
    pause
    exit /b 1
)

echo.
echo 检查Maven版本:
mvn -version
if %errorlevel% neq 0 (
    echo 错误: Maven未安装或未配置到PATH
    pause
    exit /b 1
)

echo.
echo [2/6] 清理之前的构建...
call mvn clean
if %errorlevel% neq 0 (
    echo 错误: Maven清理失败
    pause
    exit /b 1
)

echo.
echo [3/6] 运行测试...
call mvn test
if %errorlevel% neq 0 (
    echo 警告: 测试失败，是否继续打包？(Y/N)
    set /p continue=
    if /i not "%continue%"=="Y" (
        echo 打包已取消
        pause
        exit /b 1
    )
)

echo.
echo [4/6] 编译并打包JAR文件...
call mvn package -DskipTests
if %errorlevel% neq 0 (
    echo 错误: Maven打包失败
    pause
    exit /b 1
)

echo.
echo [5/6] 检查打包结果...
if exist target\wms-backend-0.0.1-SNAPSHOT.jar (
    echo ✅ 打包成功！
    echo.
    echo JAR文件信息:
    dir target\wms-backend-0.0.1-SNAPSHOT.jar
    echo.
    
    REM 创建发布目录
    if not exist "release" mkdir release
    
    REM 复制JAR文件到发布目录
    copy target\wms-backend-0.0.1-SNAPSHOT.jar release\
    
    REM 复制配置文件
    copy src\main\resources\application-prod.yml release\
    copy start-prod-server.sh release\
    copy DEPLOYMENT.md release\
    
    echo.
    echo [6/6] 创建发布包...
    echo 发布文件已复制到 release\ 目录:
    dir release\
    
    echo.
    echo ==========================================
    echo 🎉 生产环境打包完成！
    echo ==========================================
    echo.
    echo 📁 发布文件位置: release\
    echo 📦 JAR文件: release\wms-backend-0.0.1-SNAPSHOT.jar
    echo 📋 配置文件: release\application-prod.yml
    echo 🚀 启动脚本: release\start-prod-server.sh
    echo 📖 部署文档: release\DEPLOYMENT.md
    echo.
    echo 下一步操作:
    echo 1. 将 release\ 目录下的文件上传到服务器
    echo 2. 在服务器上执行: chmod +x start-prod-server.sh
    echo 3. 在服务器上执行: ./start-prod-server.sh
    echo.
    
) else (
    echo ❌ 打包失败！JAR文件未生成
    echo 请检查错误信息并重试
)

echo.
pause
