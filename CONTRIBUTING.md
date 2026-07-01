# 贡献指南

感谢你对本项目的关注！本文档将指导你如何参与项目开发。

## 如何贡献

### 报告问题

如果你发现了 bug 或者有功能建议，请通过以下方式报告：

1. 在 GitHub Issues 中创建新 issue
2. 提供详细的问题描述
3. 提供复现步骤
4. 提供设备信息和系统版本

### 提交代码

1. Fork 本仓库
2. 创建你的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交你的更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建一个 Pull Request

## 开发环境

### 前置要求

- Android Studio Hedgehog 或更高版本
- JDK 17
- Android SDK 34
- LSPosed 框架（用于测试）

### 项目设置

1. 克隆仓库
   ```bash
   git clone https://github.com/your-username/amap-blocker.git
   cd amap-blocker
   ```

2. 使用 Android Studio 打开项目

3. 等待 Gradle 同步完成

4. 编译并安装到设备

## 代码规范

### Java 代码规范

1. 遵循 Google Java Style Guide
2. 使用 4 个空格缩进
3. 方法和变量名使用 camelCase
4. 类名使用 PascalCase
5. 常量使用 UPPER_SNAKE_CASE

### 注释规范

1. 所有公共方法必须有 Javadoc 注释
2. 复杂逻辑必须有行内注释
3. TODO 注释必须包含负责人和日期

```java
/**
 * Hook MTOP 网关
 * 
 * @param classLoader 高德地图的 ClassLoader
 */
public static void hook(ClassLoader classLoader) {
    // TODO: 添加更多 Hook 点 (MiClaw, 2025-07-17)
}
```

### 日志规范

1. 使用 `MtopLogger` 记录日志
2. 日志级别要准确
3. 日志内容要清晰

```java
// 正确
MtopLogger.logMtopCall(apiName, version, data, response);

// 错误
Log.d("TAG", "something happened");
```

## 提交规范

### Commit Message 格式

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type 类型

- `feat`: 新功能
- `fix`: 修复 bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建/工具相关

### 示例

```
feat(hook): 添加新的 API 拦截规则

添加了对 mtop.relationrecommend.mtoprecommend.listRecommendMiniApps 的拦截

Closes #123
```

## 测试

### 单元测试

```bash
./gradlew test
```

### 集成测试

1. 安装模块到设备
2. 启用 LSPosed 模块
3. 重启高德地图
4. 检查日志输出

### 测试设备

- 推荐使用 Android 10+ 设备
- 需要 root 权限
- 需要安装 LSPosed 框架

## 文档

### 更新文档

如果修改了功能，请同步更新相关文档：

1. `README.md` - 项目说明
2. `docs/ARCHITECTURE.md` - 架构文档
3. 代码注释

### 文档规范

1. 使用 Markdown 格式
2. 中文编写
3. 代码示例要可运行

## 发布

### 版本号

遵循语义化版本号：

- 主版本号：不兼容的 API 修改
- 次版本号：向下兼容的功能性新增
- 修订号：向下兼容的问题修正

### 发布流程

1. 更新版本号
2. 更新 CHANGELOG.md
3. 创建 Git tag
4. 推送到 GitHub
5. 在 GitHub Releases 中创建新版本

## 行为准则

### 我们的要求

1. 使用友善和包容的语言
2. 尊重不同的观点和经验
3. 接受建设性的批评
4. 关注对社区最有利的事情
5. 对其他社区成员表示同理心

### 不可接受的行为

1. 使用性暗示的语言或图像
2. 恶意评论、人身攻击或政治攻击
3. 公开或私下的骚扰
4. 未经明确许可发布他人的私人信息
5. 其他在专业环境中被合理认为不当的行为

## 许可证

贡献的代码将遵循 MIT 许可证。

## 联系方式

如有任何问题，请通过 GitHub Issues 联系我们。
