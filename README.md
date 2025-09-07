# Markdown 编辑器应用

[![Build Status](https://github.com/yourusername/markdown/workflows/build/badge.svg)](https://github.com/yourusername/markdown/actions)

一个功能强大的Android Markdown编辑器应用，支持Markdown文件的下载、编辑、预览和保存功能。

## 功能特点

- 从URL下载Markdown文件
- 编辑Markdown内容
- 实时预览Markdown渲染效果
- 保存Markdown文件到本地存储
- 打开本地Markdown文件
- 自动更新应用程序
- 用户协议同意流程

## 技术栈

- Android原生开发
- Markwon库用于Markdown渲染
- 自定义网络下载实现
- 自动更新功能

## 系统要求

- Android 8.0 (API 24) 或更高版本
- 网络连接（用于下载功能）
- 存储权限（用于文件操作）

## 构建与安装

### 前提条件

- Android Studio Arctic Fox或更高版本
- JDK 11或更高版本
- Android SDK 35

### 构建步骤

1. 克隆仓库：
   ```
   git clone https://github.com/yourusername/markdown.git
   ```

2. 使用Android Studio打开项目

3. 同步Gradle文件

4. 构建项目：
   ```
   ./gradlew build
   ```

5. 安装到设备：
   ```
   ./gradlew installDebug
   ```

## 产品风味

应用提供多种产品风味以支持不同的CPU架构：

- universal：支持所有CPU架构（armeabi-v7a, arm64-v8a, x86, x86_64）
- armeabiV7a：仅支持armeabi-v7a架构
- arm64V8a：仅支持arm64-v8a架构
- x86：仅支持x86架构
- x86_64：仅支持x86_64架构

## 使用方法

1. 启动应用并同意用户协议
2. 输入Markdown文件的URL并点击下载按钮
3. 编辑下载的内容或直接创建新内容
4. 点击预览按钮查看渲染效果
5. 输入文件名并点击保存按钮保存到本地
6. 使用打开按钮选择本地Markdown文件进行编辑

## 贡献指南

1. Fork项目
2. 创建功能分支：`git checkout -b feature/amazing-feature`
3. 提交更改：`git commit -m 'Add some amazing feature'`
4. 推送到分支：`git push origin feature/amazing-feature`
5. 提交Pull Request

## 许可证

本项目采用MIT许可证 - 详情请参阅[LICENSE](LICENSE)文件

## 联系方式

项目维护者 - [your-email@example.com](mailto:your-email@example.com)

---

© 2023 Your Company Name. All rights reserved.