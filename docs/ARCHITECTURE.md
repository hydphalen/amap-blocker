# 项目架构文档

## 概述

本项目是一个 LSPosed 模块，用于拦截高德地图中的推广/广告内容，并记录所有 MTOP API 调用。

## 核心组件

### 1. HookEntry.java - 入口类

**职责：**
- 模块初始化
- 加载各个 Hook 模块
- 管理日志系统

**关键方法：**
- `handleLoadPackage()` - Xposed 入口，当高德地图进程启动时被调用

### 2. MtopHook.java - MTOP 网关 Hook

**职责：**
- 拦截所有包含 `recommend` / `relationrecommend` 的 API 请求
- 记录请求参数和响应数据
- 对特定推广 API 伪造空响应

**Hook 点：**
- `mtopsdk.mtop.domain.MtopRequest.setApiName` - 记录 API 名称
- `mtopsdk.mtop.domain.MtopRequest.setData` - 记录请求数据
- `mtopsdk.mtop.domain.MtopResponse` 构造函数 - 记录响应数据

**拦截策略：**
1. 所有包含 `recommend` / `relationrecommend` 的 API → 全量记录
2. 命中 `BLOCKED_APIS` 列表的 API → 伪造空响应 `{}`
3. 其他 API → 不处理

### 3. ContentHook.java - 内容系统 Hook

**职责：**
- Hook 内容卡片仓库
- Hook 推广卡片解析器
- Hook 首页 Presenter/ViewModel

**Hook 点：**
- `ContentWidgetRepository` - 内容卡片仓库
- `IndustryPromotionCardDataParser` - 推广卡片解析器
- `FetchCardDataUseCase` - 卡片数据获取
- `MapHomePagePresenter` - 首页 Presenter
- `MapHomePageViewModel` - 首页 ViewModel

### 4. AdFilterHook.java - 广告过滤 Hook

**职责：**
- 过滤 JSON 中的推广字段
- 拦截推广卡片渲染
- 移除列表中的推广项

**Hook 点：**
- `org.json.JSONObject.put` - 检测推广字段
- 卡片渲染方法 - 拦截推广卡片
- 列表适配器方法 - 移除推广项

### 5. MtopLogger.java - 日志记录器

**职责：**
- 异步写入日志文件
- 支持按日期分文件
- 线程安全的日志队列

**日志格式：**
```
[HH:mm:ss.SSS] LEVEL: message
```

**日志级别：**
- `MTOP API` - API 调用记录
- `AD BLOCKED` - 推广内容拦截
- `HOME PAGE` - 首页数据加载
- `CONTENT_REPO` - 内容仓库操作
- `CARD_PARSER` - 卡片解析操作

## 数据流

```
高德地图启动
    ↓
Xposed 框架加载模块
    ↓
HookEntry.handleLoadPackage()
    ↓
├── MtopHook.hook()
│   ├── Hook MtopRequest.setApiName
│   ├── Hook MtopRequest.setData
│   └── Hook MtopResponse 构造函数
├── ContentHook.hook()
│   ├── Hook ContentWidgetRepository
│   ├── Hook IndustryPromotionCardDataParser
│   └── Hook MapHomePagePresenter
└── AdFilterHook.hook()
    ├── Hook JSONObject.put
    ├── Hook 卡片渲染
    └── Hook 列表适配器

API 请求流程：
    ↓
MtopRequest.setApiName("mtop.xxx.xxx")
    ↓
[记录日志] API: mtop.xxx.xxx
    ↓
MtopRequest.setData(data)
    ↓
[记录日志] Request: data
    ↓
检查是否需要拦截
    ↓
├── 需要拦截 → 伪造空响应 {}
└── 不需要 → 正常网络请求
    ↓
MtopResponse 构造函数
    ↓
[记录日志] Response: response_data
```

## 配置说明

### BLOCKED_APIS - 精确匹配

```java
private static final Set<String> BLOCKED_APIS = new HashSet<>(Arrays.asList(
    "mtop.relationrecommend.mtoprecommend.listRecommendMiniApps",
    // 添加更多需要拦截的 API
));
```

### BLOCK_KEYWORDS - 模糊匹配

```java
private static final String[] BLOCK_KEYWORDS = {
    "wallet",
    "finance",
    "carservice",
    // 添加更多需要拦截的关键词
};
```

### LOG_KEYWORDS - 日志记录

```java
private static final String[] LOG_KEYWORDS = {
    "recommend",
    "relationrecommend",
    "recommendstream",
    "listRecommendMiniApps",
};
```

## 日志文件

**位置：** `/data/data/com.autonavi.minimap/amap_blocker_logs/`

**文件名格式：** `mtop_log_YYYY-MM-DD.txt`

**拉取命令：**
```bash
adb shell "cat /data/data/com.autonavi.minimap/amap_blocker_logs/mtop_log_$(date +%Y-%m-%d).txt"
```

## 已知的推广 API

根据逆向分析，以下 API 可能包含推广内容：

1. `mtop.relationrecommend.mtoprecommend.recommendstream/1.0` - 推荐流
2. `mtop.relationrecommend.mtoprecommend.listRecommendMiniApps` - 小程序推荐
3. `mtop.autonavi.mp.*` - 高德地图相关 API
4. 包含 `recommend`、`promotion`、`advert` 等关键词的 API

## 扩展指南

### 添加新的 Hook 点

1. 在对应的 Hook 类中添加新方法
2. 使用 `XposedBridge.hookMethod` 进行 Hook
3. 在 `MtopLogger` 中添加日志记录
4. 测试并验证功能

### 添加新的拦截 API

1. 运行模块，查看日志
2. 找到需要拦截的 API 名称
3. 添加到 `BLOCKED_APIS` 或 `BLOCK_KEYWORDS`
4. 重新编译并测试

### 修改日志格式

1. 修改 `MtopLogger` 中的日志格式
2. 重新编译并测试

## 性能考虑

1. **异步日志写入** - 使用队列异步写入，避免阻塞主线程
2. **最小化 Hook** - 只 Hook 必要的方法
3. **条件判断** - 在 Hook 中添加条件判断，避免不必要的处理

## 安全注意事项

1. 本模块需要 root 权限
2. 日志文件可能包含敏感信息
3. 模块可能会影响高德地图的正常功能
4. 仅供学习研究使用

## 故障排查

### 模块未加载

1. 检查 LSPosed 是否正确安装
2. 检查模块是否在 LSPosed 中启用
3. 检查作用域是否选择「高德地图」
4. 重启高德地图

### 日志为空

1. 检查高德地图是否有网络请求
2. 检查日志目录权限
3. 检查模块是否正确 Hook

### 高德地图崩溃

1. 检查 Hook 点是否正确
2. 检查类名和方法名是否匹配
3. 查看 LSPosed 日志中的错误信息
