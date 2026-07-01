@echo off
REM 高德地图去推广 LSPosed 模块 - 安装脚本 (Windows)

echo === 安装高德地图去推广 LSPosed 模块 ===
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

REM 查找 APK 文件
set APK_PATH=
for /r "app\build\outputs\apk\debug" %%i in (*.apk) do (
    set APK_PATH=%%i
    goto :found_apk
)

echo ❌ 未找到 APK 文件，请先编译项目
echo    或者使用 Android Studio 打开项目并编译
pause
exit /b 1

:found_apk
echo ✅ 找到 APK: %APK_PATH%
echo.

REM 安装 APK
echo 正在安装 APK...
adb install -r "%APK_PATH%"
if %errorlevel% neq 0 (
    echo ❌ 安装失败
    pause
    exit /b 1
)

echo ✅ APK 安装成功
echo.

REM 提示用户启用模块
echo === 下一步操作 ===
echo.
echo 1. 打开 LSPosed 管理器
echo 2. 进入「模块」页面
echo 3. 找到「高德去推广」模块
echo 4. 启用模块
echo 5. 选择作用域为「高德地图」
echo 6. 重启高德地图
echo.
echo === 完成 ===
echo.
pause
