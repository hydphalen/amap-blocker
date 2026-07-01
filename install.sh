#!/bin/bash

# 高德地图去推广 LSPosed 模块 - 安装脚本

echo "=== 安装高德地图去推广 LSPosed 模块 ==="
echo ""

# 检查 adb
if ! command -v adb &> /dev/null; then
    echo "❌ 未找到 adb，请先安装 Android SDK Platform Tools"
    echo "   下载地址: https://developer.android.com/tools/releases/platform-tools"
    exit 1
fi

echo "✅ adb 已安装"
echo ""

# 检查设备连接
if ! adb devices | grep -q "device$"; then
    echo "❌ 未检测到设备，请确保："
    echo "   1. 设备已连接并开启 USB 调试"
    echo "   2. 已授权此电脑的调试权限"
    exit 1
fi

echo "✅ 设备已连接"
echo ""

# 查找 APK 文件
APK_PATH=$(find app/build/outputs/apk/debug -name "*.apk" 2>/dev/null | head -1)

if [ -z "$APK_PATH" ]; then
    echo "❌ 未找到 APK 文件，请先编译项目"
    echo "   或者使用 Android Studio 打开项目并编译"
    exit 1
fi

echo "✅ 找到 APK: $APK_PATH"
echo ""

# 安装 APK
echo "正在安装 APK..."
adb install -r "$APK_PATH"
if [ $? -ne 0 ]; then
    echo "❌ 安装失败"
    exit 1
fi

echo "✅ APK 安装成功"
echo ""

# 提示用户启用模块
echo "=== 下一步操作 ==="
echo ""
echo "1. 打开 LSPosed 管理器"
echo "2. 进入「模块」页面"
echo "3. 找到「高德去推广」模块"
echo "4. 启用模块"
echo "5. 选择作用域为「高德地图」"
echo "6. 重启高德地图"
echo ""
echo "=== 完成 ==="
