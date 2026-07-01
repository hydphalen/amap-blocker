@echo off
REM 高德地图去推广 LSPosed 模块 - 日志拉取脚本 (Windows)

echo === 拉取高德地图去推广模块日志 ===
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

REM 创建本地日志目录
if not exist "logs" mkdir logs

REM 获取当前日期
for /f "tokens=2 delims==" %%I in ('wmic os get localdatetime /value') do set datetime=%%I
set datestr=%datetime:~0,4%%datetime:~4,2%%datetime:~6,2%
set timestr=%datetime:~8,2%%datetime:~10,2%%datetime:~12,2%

echo 正在拉取日志...
echo.

REM 拉取今天的日志
adb shell "cat /data/data/com.autonavi.minimap/amap_blocker_logs/mtop_log_%datestr:~0,4%-%datestr:~4,2%-%datestr:~6,2%.txt" > "logs\mtop_log_%datestr%_%timestr%.txt" 2>nul

REM 检查是否成功
if %errorlevel% equ 0 (
    echo ✅ 日志已保存到: logs\mtop_log_%datestr%_%timestr%.txt
) else (
    echo ⚠️  无法拉取日志文件
    echo    可能原因：
    echo    1. 设备未连接或未授权
    echo    2. 高德地图未安装或未运行
    echo    3. 模块未启用
    echo    4. 日志文件不存在
)

echo.
echo === 完成 ===
echo.
pause
