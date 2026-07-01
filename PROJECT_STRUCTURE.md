# 项目结构

本项目是一个 LSPosed 模块，用于拦截高德地图中的推广/广告内容。

## 目录结构

```
amap-blocker/
├── .github/
│   └── workflows/
│       └── build.yml              # GitHub Actions 自动构建配置
├── app/
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml    # 模块清单文件
│           ├── assets/
│           │   └── xposed_init        # Xposed 初始化文件
│           ├── java/
│           │   └── com/
│           │       └── miclaw/
│           │           └── amapblocker/
│           │               ├── HookEntry.java          # 模块入口类
│           │               ├── MtopHook.java           # MTOP 网关 Hook
│           │               ├── ContentHook.java        # 内容系统 Hook
│           │               ├── AdFilterHook.java       # 广告过滤 Hook
│           │               └── MtopLogger.java         # 日志记录器
│           └── res/
│               └── values/
│                   └── xposed_scope.xml    # 作用域配置
├── docs/
│   └── ARCHITECTURE.md            # 架构文档
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties  # Gradle Wrapper 配置
├── .gitignore                     # Git 忽略文件
├── build.gradle                   # 项目级构建配置
├── settings.gradle                # 项目设置
├── gradle.properties              # Gradle 属性
├── gradlew                        # Gradle Wrapper 脚本 (Linux/macOS)
├── gradlew.bat                    # Gradle Wrapper 脚本 (Windows)
├── setup.sh                       # 项目初始化脚本 (Linux/macOS)
├── setup.bat                      # 项目初始化脚本 (Windows)
├── install.sh                     # 模块安装脚本 (Linux/macOS)
├── install.bat                    # 模块安装脚本 (Windows)
├── pull_logs.sh                   # 日志拉取脚本 (Linux/macOS)
├── pull_logs.bat                  # 日志拉取脚本 (Windows)
├── README.md                      # 项目说明
├── CONTRIBUTING.md                # 贡献指南
├── CHANGELOG.md                   # 更新日志
├── LICENSE                        # 许可证
└── PROJECT_STRUCTURE.md           # 本文件
```

## 文件说明

### 核心代码

| 文件 | 说明 |
|------|------|
| `HookEntry.java` | 模块入口类，负责初始化和加载各个 Hook 模块 |
| `MtopHook.java` | MTOP 网关 Hook，拦截 API 请求并记录日志 |
| `ContentHook.java` | 内容系统 Hook，Hook 内容卡片仓库和解析器 |
| `AdFilterHook.java` | 广告过滤 Hook，过滤推广内容 |
| `MtopLogger.java` | 日志记录器，异步写入日志文件 |

### 配置文件

| 文件 | 说明 |
|------|------|
| `AndroidManifest.xml` | 模块清单文件，定义模块信息 |
| `xposed_init` | Xposed 初始化文件，指定入口类 |
| `xposed_scope.xml` | 作用域配置，指定模块作用范围 |
| `build.gradle` | 构建配置，定义依赖和编译选项 |
| `settings.gradle` | 项目设置，定义模块结构 |
| `gradle.properties` | Gradle 属性，定义构建参数 |

### 脚本文件

| 文件 | 说明 |
|------|------|
| `gradlew` / `gradlew.bat` | Gradle Wrapper 脚本，用于构建项目 |
| `setup.sh` / `setup.bat` | 项目初始化脚本，生成 Gradle Wrapper |
| `install.sh` / `install.bat` | 模块安装脚本，安装 APK 到设备 |
| `pull_logs.sh` / `pull_logs.bat` | 日志拉取脚本，从设备拉取日志 |

### 文档文件

| 文件 | 说明 |
|------|------|
| `README.md` | 项目说明，包含快速开始和使用方法 |
| `ARCHITECTURE.md` | 架构文档，详细说明项目结构和数据流 |
| `CONTRIBUTING.md` | 贡献指南，指导如何参与项目开发 |
| `CHANGELOG.md` | 更新日志，记录项目的重要更改 |
| `LICENSE` | 许可证，定义项目的使用条款 |

### CI/CD 配置

| 文件 | 说明 |
|------|------|
| `build.yml` | GitHub Actions 工作流，自动构建 APK |

## 构建产物

构建完成后，APK 文件位于：

```
app/build/outputs/apk/debug/app-debug.apk
```

## 日志文件

模块运行时产生的日志文件位于设备上：

```
/data/data/com.autonavi.minimap/amap_blocker_logs/mtop_log_YYYY-MM-DD.txt
```

可以使用 `pull_logs.sh` 或 `pull_logs.bat` 脚本拉取日志。

## 开发流程

1. **环境准备**
   - 安装 Android Studio
   - 安装 JDK 17
   - 安装 Android SDK

2. **项目设置**
   - 克隆仓库
   - 使用 Android Studio 打开项目
   - 等待 Gradle 同步完成

3. **开发**
   - 修改代码
   - 编译测试
   - 提交代码

4. **测试**
   - 安装到设备
   - 启用 LSPosed 模块
   - 测试功能

5. **发布**
   - 推送到 GitHub
   - 自动构建 APK
   - 创建 Release

## 技术栈

- **语言**: Java
- **框架**: LSPosed / Xposed
- **构建**: Gradle
- **CI/CD**: GitHub Actions
- **目标应用**: 高德地图 (com.autonavi.minimap)

## 依赖项

### 编译依赖

- `de.robv.android.xposed:api:82` - LSPosed API

### 运行时依赖

- LSPosed 框架
- 高德地图 (com.autonavi.minimap)
- Android 7.0+ (API 24+)

## 许可证

本项目采用 MIT 许可证，详见 [LICENSE](LICENSE) 文件。
