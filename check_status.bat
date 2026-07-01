@echo off
REM 高德地图去推广 LSPosed 模块 - 状态检查脚本 (Windows)

echo === 检查高德地图去推广 LSPosed 模块状态 ===
echo.

REM 检查 adb
adb version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 未找到 adb，请先安装 Android SDK Platform Tools
    echo    下载地址: https://developer.android.com/tools/releases/platform-tools
    pause
    exit /b 1
)

echo ✅ adb 已安装
echo.

REM 检查设备连接
adb devices | findstr /r "device$" >nul
if %errorlevel% neq 0 (
    echo ❌ 未检测到设备，请确保：
    echo    1. 设备已连接并开启 USB 调试
    echo    2. 已授权此电脑的调试权限
    pause
    exit /b 1
)

echo ✅ 设备已连接
echo.

REM 检查高德地图是否安装
adb shell pm list packages | findstr "com.autonavi.minimap" >nul
if %errorlevel% neq 0 (
    echo ❌ 高德地图未安装
    pause
    exit /b 1
)

echo ✅ 高德地图已安装
echo.

REM 检查模块是否安装
adb shell pm list packages | findstr "com.miclaw.amapblocker" >nul
if %errorlevel% neq 0 (
    echo ❌ 模块未安装
    echo    请先运行 install.bat 安装模块
    pause
    exit /b 1
)

echo ✅ 模块已安装
echo.

REM 检查日志目录
adb shell "ls -la /data/data/com.autonavi.minimap/amap_blocker_logs/" >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ 日志目录存在
    echo.
    echo 日志文件列表：
    adb shell "ls -la /data/data/com.autonavi.minimap/amap_blocker_logs/"
) else (
    echo ⚠️  日志目录不存在或无法访问
    echo    可能原因：
    echo    1. 模块未启用
    echo    2. 高德地图未运行
    echo    3. 需要 root 权限
)

echo.
echo === 检查完成 ===
echo.
echo 如果模块未启用，请：
echo 1. 打开 LSPosed 管理器
echo 2. 进入「模块」页面
echo 3. 找到「高德去推广」模块
echo 4. 启用模块
echo 5. 选择作用域为「高德地图」
echo 6. 重启高德地图
echo.
pause
