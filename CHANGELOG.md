# 更新日志

本项目的所有重要更改都会记录在此文件中。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)，
版本号遵循 [语义化版本号](https://semver.org/lang/zh-CN/)。

## [未发布]

### 新增

- MTOP 网关 Hook 功能
  - 拦截所有包含 `recommend` / `relationrecommend` 的 API 请求
  - 记录请求参数和响应数据
  - 对特定推广 API 伪造空响应

- 内容系统 Hook 功能
  - Hook ContentWidgetRepository（内容卡片仓库）
  - Hook IndustryPromotionCardDataParser（推广卡片解析器）
  - Hook FetchCardDataUseCase（卡片数据获取）
  - Hook 首页 Presenter/ViewModel

- 广告过滤功能
  - 过滤 JSON 中的推广字段
  - 拦截推广卡片渲染
  - 移除列表中的推广项

- 日志系统
  - 异步写入日志文件
  - 支持按日期分文件
  - 线程安全的日志队列

- GitHub Actions 自动构建
  - 推送到 main/master 分支自动构建 APK
  - 支持手动触发构建

- 项目文档
  - README.md - 项目说明
  - ARCHITECTURE.md - 架构文档
  - CONTRIBUTING.md - 贡献指南
  - CHANGELOG.md - 更新日志

### 变更

- 无

### 修复

- 无

## [1.0.0] - 2025-07-17

### 新增

- 初始版本发布
- 基础 MTOP 网关 Hook
- 日志记录功能
- LSPosed 模块框架

[未发布]: https://github.com/your-username/amap-blocker/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/your-username/amap-blocker/releases/tag/v1.0.0
