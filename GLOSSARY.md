# 术语表

本文档定义了高德地图去推广 LSPosed 模块中使用的所有术语。

## A

### API (Application Programming Interface)
**应用编程接口**

应用程序之间通信的接口。在本项目中，特指高德地图与服务器之间的通信接口。

### APK (Android Package Kit)
**Android 安装包**

Android 应用程序的安装文件格式。本项目编译生成的 APK 文件可以安装到 Android 设备上。

### ADB (Android Debug Bridge)
**Android 调试桥**

用于与 Android 设备通信的命令行工具。可以用来安装应用、查看日志、执行命令等。

## B

### Block
**拦截**

阻止特定的 API 请求或响应。在本项目中，指拦截高德地图的推广 API。

### Build
**构建**

将源代码编译成可执行文件的过程。在本项目中，指将 Java 代码编译成 APK 文件。

## C

### ClassLoader
**类加载器**

Java 虚拟机中用于加载类的机制。在本项目中，指高德地图的类加载器，用于加载 Hook 点类。

### Content Provider
**内容提供者**

Android 中用于跨应用共享数据的机制。在本项目中，指高德地图的内容卡片仓库。

### CI/CD (Continuous Integration/Continuous Deployment)
**持续集成/持续部署**

自动化软件开发实践，包括代码集成、测试和部署。本项目使用 GitHub Actions 实现 CI/CD。

## D

### Debug
**调试**

查找和修复程序错误的过程。在本项目中，指使用 Logcat 和 Android Studio 进行调试。

### Dex
**Dalvik Executable**

Android 平台上的可执行文件格式。高德地图的代码编译成 Dex 文件运行。

## E

### Edxposed
**Edxposed**

基于 Xposed 框架的 Android 模块框架，支持 Android 8.0+ 设备。本项目基于 Edxposed 开发。

### Exception
**异常**

程序运行时发生的错误。在本项目中，指 Hook 过程中可能发生的异常。

## F

### Fork
**分叉**

从现有代码库创建一个新的独立副本。在开源项目中，指复制项目到自己的账户下进行修改。

### Framework
**框架**

提供基础功能和结构的软件库。在本项目中，指 LSPosed/Xposed 框架。

## G

### Gradle
**Gradle**

Android 项目的构建工具，用于管理依赖、编译代码、打包应用等。

### GitHub Actions
**GitHub Actions**

GitHub 提供的 CI/CD 服务，可以自动化构建、测试和部署流程。

## H

### Hook
**钩子**

在程序执行过程中插入自定义代码的技术。在本项目中，指拦截高德地图的方法调用。

### HTTP (Hypertext Transfer Protocol)
**超文本传输协议**

用于 Web 通信的协议。高德地图的 API 请求使用 HTTP/HTTPS 协议。

## I

### IDE (Integrated Development Environment)
**集成开发环境**

提供代码编辑、编译、调试等功能的软件。本项目使用 Android Studio 作为 IDE。

### Interface
**接口**

定义类之间通信的契约。在本项目中，指 Xposed 模块需要实现的接口。

### IXposedHookLoadPackage
**Xposed 加载包钩子接口**

Xposed 模块需要实现的接口，用于在应用加载时执行 Hook 操作。

## J

### Java
**Java**

Android 应用的主要编程语言。本项目使用 Java 编写。

### JDK (Java Development Kit)
**Java 开发工具包**

用于开发 Java 应用的工具集。本项目需要 JDK 17。

### JSON (JavaScript Object Notation)
**JavaScript 对象表示法**

轻量级的数据交换格式。高德地图的 API 请求和响应使用 JSON 格式。

## K

### Kotlin
**Kotlin**

Android 官方支持的编程语言。本项目目前使用 Java，但可以迁移到 Kotlin。

## L

### LSPosed
**LSPosed**

基于 Edxposed 的 Android 模块框架，提供更强大的功能和更好的兼容性。

### Log
**日志**

记录程序运行信息的文件。在本项目中，指记录 API 调用信息的日志文件。

### Logcat
**Logcat**

Android 提供的日志查看工具，可以查看系统和应用的日志输出。

### LRU Cache (Least Recently Used Cache)
**最近最少使用缓存**

一种缓存淘汰策略，优先淘汰最近最少使用的数据。本项目使用 LRU Cache 缓存 API 判断结果。

## M

### MTOP (Mobile Taobao Open Platform)
**移动淘宝开放平台**

阿里巴巴的移动开放平台，提供 API 网关服务。高德地图使用 MTOP 作为 API 网关。

### Module
**模块**

LSPosed/Xposed 框架中的功能单元，可以 Hook 应用的方法。本项目就是一个 LSPosed 模块。

## N

### Native
**原生**

指直接在设备硬件上运行的代码，与 Java 代码相对。高德地图包含 Native 代码。

## O

### Open Source
**开源**

源代码公开的软件，允许任何人查看、修改和分发。本项目采用 MIT 开源协议。

## P

### Package
**包**

Android 应用的唯一标识符。高德地图的包名是 `com.autonavi.minimap`。

### Presenter
**呈现器**

MVP 架构中的组件，负责处理用户交互和更新视图。高德地图首页使用 Presenter 模式。

### ProGuard
**ProGuard**

Android 的代码压缩和混淆工具，可以减小 APK 体积并保护代码。

## R

### Release
**发布**

将软件正式发布给用户的过程。在本项目中，指发布 APK 文件到 GitHub Releases。

### Repository
**仓库**

存储代码和项目文件的地方。本项目托管在 GitHub 仓库中。

### Root
**Root**

获取 Android 设备最高权限的过程。本项目需要 Root 权限才能运行。

## S

### SDK (Software Development Kit)
**软件开发工具包**

用于开发特定平台应用的工具集。本项目使用 Android SDK。

### Shell
**Shell**

命令行解释器，用于执行命令。本项目提供 Shell 脚本简化操作。

### Signature
**签名**

用于验证 APK 文件完整性和来源的数字证书。发布 APK 需要签名。

## T

### Thread
**线程**

程序执行的基本单位。本项目使用多线程处理日志写入。

### Token
**令牌**

用于身份验证的凭证。高德地图的 API 请求可能包含 Token。

## U

### UI (User Interface)
**用户界面**

用户与程序交互的界面。本项目目前没有图形界面，未来版本会添加。

## V

### ViewModel
**视图模型**

MVVM 架构中的组件，负责管理 UI 数据。高德地图首页使用 ViewModel 模式。

### Version
**版本**

软件的迭代版本。本项目使用语义化版本号（如 v1.0.0）。

## W

### Workflow
**工作流**

自动化流程的定义。本项目使用 GitHub Actions 工作流进行 CI/CD。

## X

### Xposed
**Xposed**

Android 平台的模块框架，允许修改系统和应用的行为。LSPosed 基于 Xposed 框架。

### Xposed Framework
**Xposed 框架**

提供 Hook 功能的 Android 框架。本项目基于 Xposed 框架开发。

## Y

### YAML (YAML Ain't Markup Language)
**YAML**

一种数据序列化格式，常用于配置文件。GitHub Actions 工作流使用 YAML 格式。

## Z

### ZipAlign
**ZipAlign**

Android 的 APK 对齐工具，可以优化 APK 文件的内存使用。

---

## 缩写对照

| 缩写 | 全称 | 中文 |
|------|------|------|
| API | Application Programming Interface | 应用编程接口 |
| APK | Android Package Kit | Android 安装包 |
| ADB | Android Debug Bridge | Android 调试桥 |
| CI/CD | Continuous Integration/Continuous Deployment | 持续集成/持续部署 |
| IDE | Integrated Development Environment | 集成开发环境 |
| JDK | Java Development Kit | Java 开发工具包 |
| JSON | JavaScript Object Notation | JavaScript 对象表示法 |
| LRU | Least Recently Used | 最近最少使用 |
| MTOP | Mobile Taobao Open Platform | 移动淘宝开放平台 |
| SDK | Software Development Kit | 软件开发工具包 |
| UI | User Interface | 用户界面 |
| YAML | YAML Ain't Markup Language | YAML |

---

**最后更新**: 2025-07-17
