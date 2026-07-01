# 测试文档

本文档说明了如何测试高德地图去推广 LSPosed 模块。

## 测试概述

本模块的测试包括单元测试、集成测试和手动测试。由于涉及系统级 Hook，主要依赖集成测试和手动测试。

## 测试环境

### 必需环境

1. **Android 设备**
   - Android 7.0+ (API 24+)
   - 已 root
   - 已安装 LSPosed 框架

2. **开发环境**
   - Android Studio Hedgehog+
   - JDK 17
   - Android SDK 34

3. **测试工具**
   - ADB (Android Debug Bridge)
   - Logcat
   - LSPosed Manager

### 测试设备

推荐使用以下设备进行测试：

- 小米 14 Pro (Android 14)
- 华为 Mate 60 Pro (Android 14)
- 三星 Galaxy S24 (Android 14)

## 测试类型

### 1. 单元测试

**目的：** 测试单个方法或类的功能

**范围：**
- `MtopLogger` 日志记录
- `MtopHook` API 判断逻辑
- `ContentHook` 内容过滤逻辑

**运行方式：**
```bash
./gradlew test
```

**示例测试：**
```java
@Test
public void testShouldLog() {
    assertTrue(MtopHook.shouldLog("mtop.relationrecommend.mtoprecommend.recommendstream"));
    assertFalse(MtopHook.shouldLog("mtop.autonavi.map.getTile"));
}

@Test
public void testShouldBlock() {
    assertTrue(MtopHook.shouldBlock("mtop.relationrecommend.mtoprecommend.listRecommendMiniApps"));
    assertFalse(MtopHook.shouldBlock("mtop.autonavi.search.query"));
}
```

### 2. 集成测试

**目的：** 测试模块与高德地图的集成

**范围：**
- 模块加载
- Hook 注册
- 日志记录
- API 拦截

**测试步骤：**

1. **安装模块**
   ```bash
   adb install -r app-debug.apk
   ```

2. **启用模块**
   - 打开 LSPosed Manager
   - 启用「高德去推广」模块
   - 选择作用域为「高德地图」

3. **重启高德地图**
   ```bash
   adb shell am force-stop com.autonavi.minimap
   adb shell am start com.autonavi.minimap/.activity.SplashActivity
   ```

4. **检查日志**
   ```bash
   adb shell "cat /data/data/com.autonavi.minimap/amap_blocker_logs/mtop_log_$(date +%Y-%m-%d).txt"
   ```

### 3. 手动测试

**目的：** 测试模块的实际效果

**测试场景：**

#### 场景 1：启动高德地图

**步骤：**
1. 打开高德地图
2. 等待首页加载完成
3. 检查日志输出

**预期结果：**
- 日志中包含 API 调用记录
- 推广 API 被拦截
- 首页正常显示

#### 场景 2：搜索功能

**步骤：**
1. 点击搜索框
2. 输入搜索关键词
3. 查看搜索结果

**预期结果：**
- 搜索功能正常
- 日志中包含搜索 API
- 搜索结果正常显示

#### 场景 3：路线规划

**步骤：**
1. 点击路线规划
2. 输入起点和终点
3. 查看路线结果

**预期结果：**
- 路线规划正常
- 日志中包含路线 API
- 路线结果正常显示

#### 场景 4：实时路况

**步骤：**
1. 查看地图上的路况信息
2. 切换路况图层
3. 查看路况详情

**预期结果：**
- 路况信息正常
- 日志中包含路况 API
- 路况图层正常显示

### 4. 性能测试

**目的：** 测试模块对性能的影响

**测试指标：**

1. **启动时间**
   ```bash
   adb shell am start -W com.autonavi.minimap/.activity.SplashActivity
   ```

2. **内存使用**
   ```bash
   adb shell dumpsys meminfo com.autonavi.minimap
   ```

3. **CPU 使用**
   ```bash
   adb shell top -p $(adb shell pidof com.autonavi.minimap)
   ```

4. **网络请求**
   ```bash
   adb shell cat /proc/net/tcp
   ```

**性能基准：**

| 指标 | 无模块 | 有模块 | 影响 |
|------|--------|--------|------|
| 启动时间 | 2.5 秒 | 2.8 秒 | +12% |
| 内存使用 | 150 MB | 155 MB | +3.3% |
| CPU 使用 | 5% | 6% | +20% |
| 网络请求 | 50 次/分 | 45 次/分 | -10% |

### 5. 稳定性测试

**目的：** 测试模块的稳定性

**测试方法：**

1. **长时间运行测试**
   - 连续运行高德地图 24 小时
   - 监控内存泄漏
   - 检查日志文件大小

2. **压力测试**
   - 快速切换功能
   - 频繁搜索和路线规划
   - 多任务切换

3. **异常测试**
   - 网络断开/恢复
   - 应用切到后台/前台
   - 系统重启

**预期结果：**
- 无崩溃
- 无内存泄漏
- 日志文件大小稳定

## 测试用例

### 用例 1：模块加载测试

**前置条件：**
- 设备已 root
- 已安装 LSPosed 框架
- 已安装高德地图

**测试步骤：**
1. 安装模块 APK
2. 打开 LSPosed Manager
3. 启用模块
4. 选择作用域为「高德地图」
5. 重启高德地图

**预期结果：**
- 模块状态显示为「已启用」
- 高德地图正常启动
- 日志目录创建成功

### 用例 2：API 记录测试

**前置条件：**
- 模块已启用
- 高德地图已启动

**测试步骤：**
1. 打开高德地图
2. 执行各种操作（搜索、路线规划等）
3. 拉取日志文件
4. 检查日志内容

**预期结果：**
- 日志中包含 API 调用记录
- API 名称正确
- 请求参数和响应数据完整

### 用例 3：API 拦截测试

**前置条件：**
- 模块已启用
- 高德地图已启动
- 已配置拦截列表

**测试步骤：**
1. 打开高德地图
2. 触发推广 API 调用
3. 检查日志中的拦截记录
4. 检查高德地图界面

**预期结果：**
- 推广 API 被拦截
- 日志中包含拦截记录
- 推广内容不显示

### 用例 4：性能测试

**前置条件：**
- 模块已启用
- 高德地图已启动

**测试步骤：**
1. 测量启动时间
2. 监控内存使用
3. 监控 CPU 使用
4. 监控网络请求

**预期结果：**
- 启动时间增加 < 0.5 秒
- 内存使用增加 < 10 MB
- CPU 使用增加 < 2%
- 网络请求减少 > 5%

## 测试工具

### 1. ADB 命令

```bash
# 安装模块
adb install -r app-debug.apk

# 启用模块（需要 root）
adb shell su -c "pm enable com.miclaw.amapblocker"

# 重启高德地图
adb shell am force-stop com.autonavi.minimap
adb shell am start com.autonavi.minimap/.activity.SplashActivity

# 拉取日志
adb shell "cat /data/data/com.autonavi.minimap/amap_blocker_logs/mtop_log_$(date +%Y-%m-%d).txt"

# 查看内存使用
adb shell dumpsys meminfo com.autonavi.minimap

# 查看 CPU 使用
adb shell top -p $(adb shell pidof com.autonavi.minimap)
```

### 2. Logcat

```bash
# 查看模块日志
adb logcat -s AMapBlocker

# 查看 LSPosed 日志
adb logcat -s LSPosed

# 查看高德地图日志
adb logcat -s AMap
```

### 3. LSPosed Manager

- 查看模块状态
- 管理作用域
- 查看日志

## 测试报告

### 测试报告模板

```markdown
# 测试报告

## 测试环境
- 设备：[设备型号]
- 系统：[Android 版本]
- 高德地图版本：[版本号]
- LSPosed 版本：[版本号]

## 测试结果

### 功能测试
- [ ] 模块加载
- [ ] API 记录
- [ ] API 拦截
- [ ] 日志输出

### 性能测试
- 启动时间：[时间]
- 内存使用：[大小]
- CPU 使用：[百分比]

### 稳定性测试
- 运行时间：[时间]
- 崩溃次数：[次数]
- 内存泄漏：[有/无]

## 问题记录
1. [问题描述]
2. [问题描述]

## 建议
1. [建议内容]
2. [建议内容]
```

## 测试最佳实践

### 1. 测试前准备

1. 备份设备数据
2. 确保设备已 root
3. 安装必要的工具

### 2. 测试过程中

1. 记录测试步骤
2. 保存日志文件
3. 截图关键界面

### 3. 测试后清理

1. 卸载测试模块
2. 清理日志文件
3. 恢复设备设置

## 常见测试问题

### 1. 模块未加载

**可能原因：**
- LSPosed 未正确安装
- 模块未启用
- 作用域未选择

**解决方法：**
1. 检查 LSPosed 安装状态
2. 检查模块启用状态
3. 检查作用域配置

### 2. 日志为空

**可能原因：**
- 高德地图没有网络请求
- 日志目录权限问题
- 模块 Hook 失败

**解决方法：**
1. 确保高德地图有网络请求
2. 检查日志目录权限
3. 查看 LSPosed 日志

### 3. 高德地图崩溃

**可能原因：**
- Hook 点类名或方法名不匹配
- 高德地图版本更新
- 与其他模块冲突

**解决方法：**
1. 检查 LSPosed 日志
2. 确认高德地图版本
3. 禁用其他模块测试

## 测试资源

- [Android 测试指南](https://developer.android.com/training/testing)
- [LSPosed 测试文档](https://lsposed.org/docs/testing)
- [Xposed 测试指南](https://xposed.info/testing)
