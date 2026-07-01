# 部署文档

本文档说明了如何部署高德地图去推广 LSPosed 模块。

## 部署概述

本模块的部署包括开发环境部署、测试环境部署和生产环境部署。

## 部署环境

### 开发环境

**目的：** 开发和调试

**要求：**
- Android Studio
- JDK 17
- Android SDK 34
- ADB

**部署步骤：**
1. 克隆仓库
2. 使用 Android Studio 打开项目
3. 编译并安装到设备

### 测试环境

**目的：** 测试模块功能

**要求：**
- Android 设备（已 root）
- LSPosed 框架
- 高德地图

**部署步骤：**
1. 编译生成 APK
2. 安装到测试设备
3. 启用模块并测试

### 生产环境

**目的：** 正式使用

**要求：**
- Android 设备（已 root）
- LSPosed 框架
- 高德地图

**部署步骤：**
1. 从 GitHub Releases 下载 APK
2. 安装到设备
3. 启用模块

## 部署方式

### 1. 本地部署

**适用场景：** 开发和调试

**步骤：**

1. **克隆仓库**
   ```bash
   git clone https://github.com/your-username/amap-blocker.git
   cd amap-blocker
   ```

2. **使用 Android Studio 打开项目**
   - 打开 Android Studio
   - 选择 "Open an existing project"
   - 选择项目目录

3. **等待 Gradle 同步**
   - Android Studio 会自动同步 Gradle
   - 等待同步完成

4. **编译项目**
   - 点击 "Build" -> "Make Project"
   - 或使用命令行：`./gradlew assembleDebug`

5. **安装到设备**
   - 连接设备并启用 USB 调试
   - 点击 "Run" -> "Run 'app'"
   - 或使用命令行：`adb install -r app/build/outputs/apk/debug/app-debug.apk`

### 2. GitHub Actions 部署

**适用场景：** 自动构建和分发

**步骤：**

1. **推送代码到 GitHub**
   ```bash
   git add .
   git commit -m "Initial commit"
   git remote add origin https://github.com/your-username/amap-blocker.git
   git push -u origin main
   ```

2. **等待自动构建**
   - GitHub Actions 会自动构建 APK
   - 构建完成后会生成 Artifact

3. **下载 APK**
   - 访问 GitHub 仓库的 "Actions" 页面
   - 点击最新的构建任务
   - 在 "Artifacts" 部分下载 APK

### 3. 手动部署

**适用场景：** 分发给其他用户

**步骤：**

1. **编译生成 APK**
   ```bash
   ./gradlew assembleRelease
   ```

2. **签名 APK**
   ```bash
   jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore release.keystore app/build/outputs/apk/release/app-release.apk alias_name
   ```

3. **对齐 APK**
   ```bash
   zipalign -v 4 app-release-signed.apk amap-blocker-release.apk
   ```

4. **分发 APK**
   - 上传到 GitHub Releases
   - 或通过其他方式分发

## 部署配置

### 1. 环境变量

```bash
# Android SDK 路径
export ANDROID_HOME=/path/to/android/sdk

# Java 路径
export JAVA_HOME=/path/to/java

# Gradle 路径
export GRADLE_HOME=/path/to/gradle
```

### 2. Gradle 配置

**gradle.properties：**
```properties
# JVM 内存配置
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8

# AndroidX 启用
android.useAndroidX=true

# 启用 R8 优化
android.enableR8=true
```

**app/build.gradle：**
```groovy
android {
    compileSdk 34
    
    defaultConfig {
        applicationId "com.miclaw.amapblocker"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0.0"
    }
    
    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

### 3. ProGuard 配置

**proguard-rules.pro：**
```proguard
# 保留 LSPosed 入口类
-keep class com.miclaw.amapblocker.HookEntry { *; }

# 保留 Hook 类
-keep class com.miclaw.amapblocker.MtopHook { *; }
-keep class com.miclaw.amapblocker.ContentHook { *; }
-keep class com.miclaw.amapblocker.AdFilterHook { *; }

# 保留日志类
-keep class com.miclaw.amapblocker.MtopLogger { *; }
```

## 部署验证

### 1. 功能验证

**检查项：**
- [ ] 模块安装成功
- [ ] 模块启用成功
- [ ] 作用域配置正确
- [ ] 高德地图正常启动
- [ ] 日志文件生成
- [ ] API 记录正常
- [ ] API 拦截正常

**验证命令：**
```bash
# 检查模块安装
adb shell pm list packages | grep amapblocker

# 检查日志目录
adb shell "ls -la /data/data/com.autonavi.minimap/amap_blocker_logs/"

# 拉取日志
adb shell "cat /data/data/com.autonavi.minimap/amap_blocker_logs/mtop_log_$(date +%Y-%m-%d).txt"
```

### 2. 性能验证

**检查项：**
- [ ] 启动时间增加 < 0.5 秒
- [ ] 内存使用增加 < 10 MB
- [ ] CPU 使用增加 < 2%
- [ ] 无明显卡顿

**验证命令：**
```bash
# 启动时间
adb shell am start -W com.autonavi.minimap/.activity.SplashActivity

# 内存使用
adb shell dumpsys meminfo com.autonavi.minimap

# CPU 使用
adb shell top -p $(adb shell pidof com.autonavi.minimap)
```

### 3. 稳定性验证

**检查项：**
- [ ] 无崩溃
- [ ] 无 ANR
- [ ] 无内存泄漏
- [ ] 长时间运行稳定

**验证方法：**
1. 连续运行 24 小时
2. 监控崩溃日志
3. 检查内存使用趋势

## 部署回滚

### 回滚条件

1. 模块导致高德地图崩溃
2. 模块导致系统不稳定
3. 模块功能异常

### 回滚步骤

1. **禁用模块**
   - 打开 LSPosed Manager
   - 禁用「高德去推广」模块

2. **卸载模块**
   ```bash
   adb uninstall com.miclaw.amapblocker
   ```

3. **重启高德地图**
   ```bash
   adb shell am force-stop com.autonavi.minimap
   adb shell am start com.autonavi.minimap/.activity.SplashActivity
   ```

4. **清理数据**
   ```bash
   adb shell "rm -rf /data/data/com.autonavi.minimap/amap_blocker_logs/"
   ```

## 部署监控

### 1. 日志监控

```bash
# 实时监控日志
adb logcat -s AMapBlocker

# 监控 LSPosed 日志
adb logcat -s LSPosed

# 监控高德地图日志
adb logcat -s AMap
```

### 2. 性能监控

```bash
# 监控内存使用
adb shell dumpsys meminfo com.autonavi.minimap

# 监控 CPU 使用
adb shell top -p $(adb shell pidof com.autonavi.minimap)

# 监控网络请求
adb shell cat /proc/net/tcp
```

### 3. 崩溃监控

```bash
# 监控崩溃日志
adb logcat -s AndroidRuntime

# 查看崩溃报告
adb shell "ls -la /data/tombstones/"
```

## 部署文档

### 1. 用户指南

**安装步骤：**
1. 下载 APK 文件
2. 安装到设备
3. 打开 LSPosed Manager
4. 启用模块
5. 选择作用域为「高德地图」
6. 重启高德地图

**使用说明：**
- 模块会自动拦截推广内容
- 日志文件位于 `/data/data/com.autonavi.minimap/amap_blocker_logs/`
- 可以使用 `pull_logs.sh` 脚本拉取日志

### 2. 开发者指南

**开发环境：**
- Android Studio Hedgehog+
- JDK 17
- Android SDK 34

**构建步骤：**
1. 克隆仓库
2. 使用 Android Studio 打开项目
3. 等待 Gradle 同步
4. 编译生成 APK

**测试步骤：**
1. 安装到测试设备
2. 启用模块
3. 测试功能
4. 检查日志

### 3. 贡献者指南

**贡献流程：**
1. Fork 仓库
2. 创建特性分支
3. 提交更改
4. 创建 Pull Request

**代码规范：**
- 遵循 Google Java Style Guide
- 添加必要的注释
- 编写单元测试

## 部署最佳实践

### 1. 版本管理

- 使用语义化版本号
- 维护更新日志
- 创建 Git 标签

### 2. 质量保证

- 编写单元测试
- 进行代码审查
- 测试关键功能

### 3. 文档维护

- 更新 README
- 维护 API 文档
- 编写部署指南

### 4. 安全考虑

- 签名 APK
- 验证完整性
- 限制权限

## 部署资源

- [Android 部署指南](https://developer.android.com/distribute)
- [LSPosed 部署文档](https://lsposed.org/docs/deployment)
- [GitHub Actions 文档](https://docs.github.com/en/actions)
