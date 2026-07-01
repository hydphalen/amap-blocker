# 高德地图去推广 LSPosed 模块

## 功能说明

本模块用于拦截高德地图中的推广/广告内容，并记录所有 MTOP API 调用。

### 主要功能

1. **MTOP 网关 Hook**
   - 拦截所有 MTOP API 请求
   - 记录请求参数和响应数据
   - 识别推广相关 API

2. **内容/卡片系统 Hook**
   - Hook ContentWidgetRepository（内容卡片仓库）
   - Hook IndustryPromotionCardDataParser（推广卡片解析器）
   - Hook FetchCardDataUseCase（卡片数据获取）
   - Hook 首页 Presenter/ViewModel

3. **广告过滤系统**
   - 过滤 JSON 中的推广字段
   - 拦截推广卡片渲染
   - 移除列表中的推广项

## 安装步骤

### 前置要求

1. 已 root 的 Android 设备
2. 已安装 LSPosed 框架
3. 已安装高德地图

### 编译安装

1. 使用 Android Studio 打开项目
2. 编译生成 APK
3. 安装 APK 到设备
4. 在 LSPosed 中启用模块
5. 选择作用域为「高德地图」
6. 重启高德地图

### 使用方法

1. 启用模块后，打开高德地图
2. 模块会自动记录所有 MTOP API 调用
3. 日志保存在 `/data/data/com.autonavi.minimap/amap_blocker_logs/` 目录
4. 可以通过 `adb pull` 命令导出日志

## 日志说明

### 日志文件

- 文件名格式：`mtop_log_YYYY-MM-DD.txt`
- 保存位置：`/data/data/com.autonavi.minimap/amap_blocker_logs/`

### 日志内容

1. **MTOP API 调用**
   ```
   [HH:mm:ss.SSS] MTOP API: api_name
     Version: version
     Request: request_data
     Response: response_data
   ```

2. **推广内容拦截**
   ```
   [HH:mm:ss.SSS] AD BLOCKED: type
     Detail: detail
   ```

3. **首页数据加载**
   ```
   [HH:mm:ss.SSS] HOME PAGE: stage
     Info: info
   ```

## 已知的推广 API

根据逆向分析，以下 API 可能包含推广内容：

- `mtop.relationrecommend.mtoprecommend.recommendstream/1.0` - 推荐流
- `mtop.autonavi.mp.*` - 高德地图相关 API
- 包含 `recommend`、`promotion`、`advert` 等关键词的 API

## 注意事项

1. 本模块需要 LSPosed 框架支持
2. 部分功能可能需要 root 权限
3. 日志文件可能较大，建议定期清理
4. 模块可能会影响高德地图的正常功能

## 技术细节

### Hook 点

1. **MTOP 网关**
   - `mtopsdk.mtop.domain.MtopRequest.setData`
   - `mtopsdk.mtop.domain.MtopResponse` 构造函数
   - `com.autonavi.minimap.AMapMTopService`

2. **内容系统**
   - `ContentWidgetRepository` 所有方法
   - `IndustryPromotionCardDataParser` 解析方法
   - `FetchCardDataUseCase` 执行方法

3. **广告过滤**
   - `org.json.JSONObject.put`
   - 卡片渲染方法
   - 列表适配器方法

## 开发说明

### 项目结构

```
amap-blocker/
├── app/
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── assets/
│       │   └── xposed_init
│       ├── java/com/miclaw/amapblocker/
│       │   ├── HookEntry.java          # 入口类
│       │   ├── MtopHook.java           # MTOP 网关 Hook
│       │   ├── ContentHook.java        # 内容系统 Hook
│       │   ├── AdFilterHook.java       # 广告过滤 Hook
│       │   └── MtopLogger.java         # 日志记录器
│       └── res/values/
│           └── xposed_scope.xml        # 作用域配置
├── build.gradle
├── settings.gradle
└── README.md
```

### 扩展开发

如需添加新的 Hook 点：

1. 在对应的 Hook 类中添加新方法
2. 使用 `XposedBridge.hookMethod` 进行 Hook
3. 在 `MtopLogger` 中添加日志记录
4. 测试并验证功能

## 免责声明

本模块仅供学习研究使用，不得用于商业用途。使用本模块造成的任何后果由用户自行承担。
