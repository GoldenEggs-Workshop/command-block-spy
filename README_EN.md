# 🕵️‍♂️ CommandBlockSpy

[简体中文](./README.md) | [English](./README_EN.md)

[![Java Version](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.4-brightgreen.svg)](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-4)
[![Paper Version](https://img.shields.io/badge/Paper-1.21.4-blue.svg)](https://papermc.io/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Command Block Spy on Modrinth](https://img.shields.io/badge/Modrinth-Command%20Block%20Spy-brightgreen?logo=modrinth&logoColor=white)](https://modrinth.com/plugin/command-block-spy)
[![GitHub - command-block-spy](https://img.shields.io/badge/GitHub-Repository-0969da?logo=github&logoColor=white)](https://github.com/GoldenEggs-Workshop/command-block-spy)

📌 **CommandBlockSpy (CBS)** is a plugin designed for creative Minecraft servers with high degrees of freedom (command blocks enabled). It helps server admins manage command block activity by recording execution data and offering command control and security monitoring.

✅ Tested on **Minecraft 1.21.4**, built on **Paper**.


## ✨ Plugin Features

📝 **SQLite-Based Execution Logging**
Automatically records each command block’s execution count, command content, last execution time, and coordinates into a database.

🔔 **Real-Time Monitoring**
A toggleable real-time command monitoring system for both console and players.

🚫 **Dangerous Command Interception**
Supports custom regex-based interception rules, with customizable messages when blocked.

🔁 **Repeating Command Block Interception**
Optionally intercepts repeating command blocks that are always active (`auto=true`).

⚠️ **Disable Command Block Minecarts**
Optionally prevents players from placing command block minecarts.


## 🧪 Available Commands

| Command                        | Description                                       | Permission     |
|--------------------------------|---------------------------------------------------|----------------|
| `/cbspy`                       | Toggle chat monitoring prompts                    | `cbspy.use`    |
| `/cbspy reload`                | Reload plugin configuration                       | `cbspy.reload` |
| `/cbspy query loc <x> <y> <z>` | Query execution history of a specific location    | `cbspy.query`  |
| `/cbspy query recent <count>`  | Query the most recent `<count>` execution records | `cbspy.query`  |

---

## ⚙️ Configuration Example

> Note: All features are **disabled by default**. Please enable them as needed.

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


## 🚀 Quick Installation

1. **Download the plugin**:

    * [Modrinth Download](https://modrinth.com/plugin/command-block-spy)
    * [GitHub Releases](https://github.com/GoldenEggs-Workshop/command-block-spy/releases)

2. **Install to your server**:

    * Place the JAR file into your `plugins/` folder
    * Restart the server



## 📦 Build & Install from Source

Clone the project:

```bash
git clone https://github.com/GoldenEggs-Workshop/command-block-spy
cd command-block-spy
```

Build the plugin:

```bash
./gradlew build
```

Copy `build/libs/commandblockspy-xxx.jar` to your server's `plugins/` folder, then restart the server.



## 🔐 Permission Nodes

| Permission Node | Description                 |
|-----------------|-----------------------------|
| `cbspy.use`     | Toggle chat alerts          |
| `cbspy.reload`  | Reload plugin configuration |
| `cbspy.query`   | Use query-related features  |



## 💡 TODO Roadmap

* [ ] Full support for command block minecarts (logging/interception/disabling)
* [ ] Custom database configuration
* [ ] Multi-platform notification integration (Feishu / DingTalk / Discord)
* [ ] Visual query interface
* [x] i18n (internationalization) support


## 📄 **License**

This project is licensed under the **MIT License**. You are free to modify and use the plugin. For commercial deployment, please credit the original source.

