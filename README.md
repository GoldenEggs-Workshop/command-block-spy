# 🕵️‍♂️ CommandBlockSpy

[简体中文](./README.md) | [English](./README_EN.md)

[![Java Version](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.4-brightgreen.svg)](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-4)
[![Paper Version](https://img.shields.io/badge/Paper-1.21.4-blue.svg)](https://papermc.io/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Command Block Spy on Modrinth](https://img.shields.io/badge/Modrinth-Command%20Block%20Spy-brightgreen?logo=modrinth&logoColor=white)](https://modrinth.com/plugin/command-block-spy)
[![GitHub - command-block-spy](https://img.shields.io/badge/GitHub-Repository-0969da?logo=github&logoColor=white)](https://github.com/GoldenEggs-Workshop/command-block-spy)

📌 创必查(CBS) 是一个专为较高自由度的创造服（开放命令方块）设计的插件，为便于管理，记录命令方块的执行情况，并提供命令控制与安全监控功能。

✅ 在 Minecraft 1.21.4 通过测试，基于 Paper 构建。

## ✨ 插件特性

📝 **基于SQLite的执行记录** 自动将每个命令方块的执行次数、命令内容、最后执行时间和坐标保存至数据库

🔔 **实时提醒** 后台及玩家可自由开关的实时命令监控系统

🚫 **危险命令拦截** 可自定义正则规则拦截命令，并在触发时提示拦截原因

🔁 **循环命令方块拦截** 可配置是否拦截保持开启的循环命令方块

⚠️ **禁用命令方块矿车** 可配置是否禁用命令方块矿车

## 🧪 指令一览

| 指令                             | 功能描述                  | 所需权限           |
|--------------------------------|-----------------------|----------------|
| `/cbspy`                       | 切换是否显示聊天监控提示          | `cbspy.use`    |
| `/cbspy reload`                | 重载插件配置                | `cbspy.reload` |
| `/cbspy query loc <x> <y> <z>` | 查询指定位置的历史执行记录         | `cbspy.query`  |
| `/cbspy query recent <count>`  | 查询最近执行的 `<count>` 条记录 | `cbspy.query`  |

## ⚙️ 配置文件示例

> 注意: 所有功能默认 **关闭**，请根据需要自行开启。

```yaml
# CBS插件配置文件
# CBS Plugin Configuration

# 插件语言，支持中文（zh_CN）和英文（en_US）
# Plugin language, supports Chinese (zh_CN) and English (en_US)
language: zh_CN

# 是否启用数据库功能
# Enable database logging
database-logging-enabled: false

# 是否开启后台命令执行日志监控，开启后控制台会打印命令方块执行记录
# Enable backend monitoring (logs to console)
backend-monitor-enabled: false

# 是否拦截循环命令方块保持开启（auto=true）的命令，拦截后会自动设置为红石控制
# Intercept always-on repeating command blocks (auto=true)
intercept-repeating-auto: false

# 是否禁止命令方块矿车，开启后玩家会无法放置命令方块矿车
# Disable command block minecarts
ban-command-minecart: false

# 是否启用基于正则表达式的命令拦截功能，拦截匹配规则的指令并提示原因
# Enable regex-based command interception
regex-intercept-enabled: false

# 正则表达式拦截列表，格式：正则表达式: 拦截提示信息
# Regex interception list
# 支持常见正则表达式语法，例如：
# Format: regex pattern : block message
regex-block-list:
  - pattern: ^say
    message: 禁止使用 say 命令！ # The 'say' command is not allowed!
```

## 🚀 快速安装

1. **下载插件**：
    - [Modrinth下载](https://modrinth.com/plugin/command-block-spy/versions)
    - [GitHub Releases](https://github.com/GoldenEggs-Workshop/command-block-spy/releases)

2. **安装到服务器**：
    - 将JAR文件放入`plugins/`文件夹
    - 重启服务器

## 📦 构建 & 安装

克隆项目到本地：

   ```bash
   git clone https://github.com/GoldenEggs-Workshop/command-block-spy
   cd command-block-spy
   ```

构建插件：

   ```bash
   ./gradlew build
   ```

将 `build/libs/commandblockspy-xxx.jar` 拷贝至服务器 `plugins/` 文件夹，重启服务器。

## 🔐 权限节点

| 权限节点              | 功能描述     |
|-------------------|----------|
| `cbspy.use`       | 切换聊天提醒   |
| `cbspy.reload`    | 重载插件配置   |
| `cbspy.query`     | 查询记录相关功能 |
| `cbspy.intercept` | 接收拦截消息   |

## 💡 TODO 计划

* [ ] 支持命令方块矿车的完整功能（记录/拦截/禁用）
* [ ] 自定义数据库配置
* [ ] 多平台通知集成（飞书/钉钉/Discord）
* [ ] 可视化查询界面
* [x] i18n支持

## 📄 许可证

本项目使用 MIT 协议发布。你可以自由修改和使用本插件，商业部署请注明来源。
