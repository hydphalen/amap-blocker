#!/bin/bash

# 高德地图去推广 LSPosed 模块 - 日志拉取脚本

echo "=== 拉取高德地图去推广模块日志 ==="
echo ""

# 检查 adb
if ! command -v adb &> /dev/null; then
    echo "❌ 未找到 adb，请先安装 Android SDK Platform Tools"
    echo "   下载地址: https://developer.android.com/tools/releases/platform-tools"
    exit 1
fi

echo "✅ adb 已安装"
echo ""

# 创建本地日志目录
mkdir -p logs

# 获取当前日期
DATE=$(date +%Y-%m-%d)
TIME=$(date +%H%M%S)

echo "正在拉取日志..."
echo ""

# 拉取今天的日志
adb shell "cat /data/data/com.autonavi.minimap/amap_blocker_logs/mtop_log_${DATE}.txt" > "logs/mtop_log_${DATE}_${TIME}.txt" 2>/dev/null

# 检查是否成功
if [ $? -eq 0 ] && [ -s "logs/mtop_log_${DATE}_${TIME}.txt" ]; then
    echo "✅ 日志已保存到: logs/mtop_log_${DATE}_${TIME}.txt"
    echo "   文件大小: $(wc -c < "logs/mtop_log_${DATE}_${TIME}.txt") 字节"
else
    echo "⚠️  无法拉取日志文件"
    echo "   可能原因："
    echo "   1. 设备未连接或未授权"
    echo "   2. 高德地图未安装或未运行"
    echo "   3. 模块未启用"
    echo "   4. 日志文件不存在"
    rm -f "logs/mtop_log_${DATE}_${TIME}.txt"
fi

echo ""
echo "=== 完成 ==="
