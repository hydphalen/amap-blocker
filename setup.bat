@echo off
REM 高德地图去推广 LSPosed 模块 - 项目初始化脚本 (Windows)

echo === 高德地图去推广 LSPosed 模块 ===
echo.

REM 检查 Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 未找到 Java，请先安装 JDK 17
    pause
    exit /b 1
)

echo ✅ Java 已安装
java -version 2>&1 | findstr /i "version"

REM 检查 Android SDK
if defined ANDROID_HOME (
    echo ✅ ANDROID_HOME: %ANDROID_HOME%
) else if defined ANDROID_SDK_ROOT (
    echo ✅ ANDROID_SDK_ROOT: %ANDROID_SDK_ROOT%
) else (
    echo ⚠️  未设置 ANDROID_HOME 环境变量
    echo    请确保已安装 Android SDK
)

REM 生成 Gradle Wrapper
echo.
echo 正在生成 Gradle Wrapper...
gradle wrapper >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Gradle Wrapper 已生成
) else (
    echo ⚠️  未找到 gradle 命令
    echo    请安装 Gradle 或使用 Android Studio 打开项目
)

echo.
echo === 项目初始化完成 ===
echo.
echo 下一步：
echo 1. 使用 Android Studio 打开此项目
echo 2. 等待 Gradle 同步完成
echo 3. 编译生成 APK
echo 4. 安装到设备并启用 LSPosed 模块
echo.
echo 或者推送到 GitHub 使用自动构建：
echo   git init
echo   git add .
echo   git commit -m "Initial commit"
echo   git remote add origin ^<your-repo-url^>
echo   git push -u origin main
echo.
pause
