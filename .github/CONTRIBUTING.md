# Contributing to WeatherReportofYou

感谢您考虑为 WeatherReportofYou 做出贡献！🎉

## 行为准则

本项目遵循 [Contributor Covenant](https://www.contributor-covenant.org/) 行为准则。参与此项目即表示您同意遵守其条款。

## 如何贡献

### 报告 Bug

[Bug issue 模版](ISSUE_TEMPLATE\bug_report.md)


### 建议功能

[Feature Request 模版](ISSUE_TEMPLATE\feature_request.md)

### Pull Requests

1. **Fork 仓库**
2. **创建功能分支** (`git checkout -b issue-123-fix`)(不建议直接在dev上开发)
3. **编写代码**
   - 遵循现有的代码风格
   - 添加适当的注释
   - 确保代码通过测试
4. **提交更改** (`git commit -m '<提交信息格式>')
5. **如果有其他人更改仓库内容** (`git remote add upstream https://github.com/Sakurapainting/WeatherReportofYou.git` )
6. **获取他人的更改** `git fetch upstream`
7. **建议使用rebase** `git rebase upstream/dev`(主仓库的默认分支是dev，release pr的时候才会合到main里)
8. **推送到分支** (`git push origin issue-123-fix`)
9. **创建 Pull Request**

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
- `perf`: 优化性能

参考：https://www.conventionalcommits.org/zh-hans/v1.0.0/

### 开发环境设置

1. 安装 Android Studio
2. 克隆仓库
3. 配置 API key
4. 运行 `./gradlew assembleDebug`

## 许可证

通过贡献，您同意您的贡献将在 AGPL v3 许可证下授权。
