# Contributing to WeatherReportofYou

感谢您考虑为 WeatherReportofYou 做出贡献！🎉

## 行为准则

本项目遵循 [Contributor Covenant](https://www.contributor-covenant.org/) 行为准则。参与此项目即表示您同意遵守其条款。

## 如何贡献

### 报告 Bug

在创建 bug 报告之前，请检查现有的 issues 以避免重复。如果您发现一个 bug，请：

1. 使用清晰描述性的标题
2. 提供详细的复现步骤
3. 包含设备和版本信息
4. 如果可能，提供截图或日志

### 建议功能

我们欢迎新功能建议！请：

1. 检查是否已有相似的建议
2. 详细描述功能需求
3. 解释这个功能的价值
4. 考虑实现的可行性

### Pull Requests

1. **Fork 仓库**
2. **创建功能分支** (`git checkout -b feature/AmazingFeature`)
3. **编写代码**
   - 遵循现有的代码风格
   - 添加适当的注释
   - 确保代码通过测试
4. **提交更改** (`git commit -m 'Add some AmazingFeature'`)
5. **推送到分支** (`git push origin feature/AmazingFeature`)
6. **创建 Pull Request**

### 代码风格

- 使用 4 个空格缩进
- 类名使用 PascalCase
- 方法和变量使用 camelCase
- 常量使用 SCREAMING_SNAKE_CASE
- 添加适当的 JavaDoc 注释

### 提交信息格式

使用清晰的提交信息：

```
type(scope): description

[optional body]

[optional footer]
```

类型：
- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档更新
- `style`: 代码格式化
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建工具或辅助工具的变动

### 开发环境设置

1. 安装 Android Studio
2. 克隆仓库
3. 配置 API key
4. 运行 `./gradlew assembleDebug`

## 许可证

通过贡献，您同意您的贡献将在 AGPL v3 许可证下授权。
