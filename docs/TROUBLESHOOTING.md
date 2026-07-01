# 故障排查指南

本文档帮助你解决在使用高德地图去推广 LSPosed 模块时可能遇到的问题。

## 常见问题

### 1. 模块未加载

**症状：**
- 高德地图启动后没有日志输出
- LSPosed 中模块状态显示为未激活

**可能原因：**
1. LSPosed 框架未正确安装
2. 模块未在 LSPosed 中启用
3. 作用域未选择「高德地图」
4. 高德地图未重启

**解决方法：**
1. 检查 LSPosed 是否正确安装
   ```bash
   adb shell pm list packages | grep lsposed
   ```

2. 打开 LSPosed 管理器，检查模块状态

3. 确保模块已启用，且作用域包含「高德地图」

4. 强制停止高德地图并重新打开

### 2. 日志为空

**症状：**
- 模块已启用但日志文件不存在或为空

**可能原因：**
1. 高德地图没有网络请求
2. 日志目录权限问题
3. 模块 Hook 失败

**解决方法：**
1. 确保高德地图有网络请求（打开首页、搜索等）

2. 检查日志目录权限
   ```bash
   adb shell "ls -la /data/data/com.autonavi.minimap/amap_blocker_logs/"
   ```

3. 查看 LSPosed 日志中的错误信息
   ```bash
   adb logcat -s LSPosed
   ```

### 3. 高德地图崩溃

**症状：**
- 打开高德地图后立即崩溃
- 出现 Force Close 对话框

**可能原因：**
1. Hook 点类名或方法名不匹配
2. 高德地图版本更新导致类结构变化
3. 与其他模块冲突

**解决方法：**
1. 检查 LSPosed 日志中的错误信息
   ```bash
   adb logcat -s LSPosed | grep -i "error\|exception"
   ```

2. 确认高德地图版本是否与模块兼容

3. 尝试禁用其他 LSPosed 模块，排除冲突

4. 如果是版本不兼容，需要更新 Hook 点

### 4. 模块安装失败

**症状：**
- `adb install` 命令返回错误

**可能原因：**
1. 设备未连接或未授权
2. 已存在同包名应用
3. 签名冲突

**解决方法：**
1. 检查设备连接状态
   ```bash
   adb devices
   ```

2. 如果已安装旧版本，先卸载
   ```bash
   adb uninstall com.miclaw.amapblocker
   ```

3. 使用 `-r` 参数覆盖安装
   ```bash
   adb install -r app-debug.apk
   ```

### 5. 日志拉取失败

**症状：**
- `adb shell cat` 命令返回错误或空内容

**可能原因：**
1. 设备未 root
2. 日志文件不存在
3. SELinux 限制

**解决方法：**
1. 确保设备已 root
   ```bash
   adb shell su -c "id"
   ```

2. 使用 root 权限拉取日志
   ```bash
   adb shell su -c "cat /data/data/com.autonavi.minimap/amap_blocker_logs/mtop_log_$(date +%Y-%m-%d).txt"
   ```

3. 临时关闭 SELinux（可选）
   ```bash
   adb shell su -c "setenforce 0"
   ```

### 6. 拦截不生效

**症状：**
- 日志中有 API 记录但没有拦截
- 推广内容仍然显示

**可能原因：**
1. API 名称不在拦截列表中
2. 拦截逻辑未正确执行
3. 推广内容通过其他途径加载

**解决方法：**
1. 查看日志，确认需要拦截的 API 名称

2. 将 API 名称添加到 `BLOCKED_APIS` 或 `BLOCK_KEYWORDS`

3. 重新编译并安装模块

4. 检查是否有其他加载途径（如 WebView、Native 等）

## 调试技巧

### 1. 查看 LSPosed 日志

```bash
# 查看 LSPosed 相关日志
adb logcat -s LSPosed

# 查看模块日志
adb logcat -s AMapBlocker

# 过滤错误信息
adb logcat -s LSPosed | grep -i "error\|exception\|fail"
```

### 2. 检查模块状态

```bash
# 检查模块是否安装
adb shell pm list packages | grep amapblocker

# 检查模块数据目录
adb shell "ls -la /data/data/com.miclaw.amapblocker/"

# 检查日志目录
adb shell "ls -la /data/data/com.autonavi.minimap/amap_blocker_logs/"
```

### 3. 手动触发日志

1. 打开高德地图
2. 执行一些操作（搜索、查看首页等）
3. 等待几秒钟
4. 拉取日志文件

### 4. 使用 Android Studio 调试

1. 在 Android Studio 中打开项目
2. 连接设备并启用 USB 调试
3. 使用 Debug 模式运行
4. 设置断点并调试

## 版本兼容性

### 支持的 Android 版本

- Android 7.0 (API 24) 及以上

### 支持的 LSPosed 版本

- LSPosed 1.9.0 及以上

### 支持的高德地图版本

- 本模块基于高德地图某个特定版本逆向开发
- 如果高德地图更新，可能需要调整 Hook 点

## 获取帮助

如果以上方法都无法解决问题：

1. 查看 GitHub Issues 中是否有类似问题
2. 提交新的 Issue，包含：
   - 设备型号和系统版本
   - 高德地图版本
   - LSPosed 版本
   - 错误日志
   - 复现步骤

## 相关资源

- [LSPosed 官方文档](https://lsposed.org/)
- [Xposed Framework](https://xposed.info/)
- [高德地图开放平台](https://lbs.amap.com/)
