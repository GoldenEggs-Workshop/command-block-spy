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

| Command                      | Description                                       | Permission     |
|------------------------------|---------------------------------------------------|----------------|
| `/cbs`                       | Toggle chat monitoring prompts                    | `cbspy.use`    |
| `/cbs reload`                | Reload plugin configuration                       | `cbspy.reload` |
| `/cbs query loc <x> <y> <z>` | Query execution history of a specific location    | `cbspy.query`  |
| `/cbs query recent <count>`  | Query the most recent `<count>` execution records | `cbspy.query`  |

---

## ⚙️ Configuration Example

> Note: All features are **disabled by default**. Please enable them as needed.

```yaml
# CBS Plugin Configuration

# Enable database logging
database-logging-enabled: false

# Enable backend monitoring (logs to console)
backend-monitor-enabled: false

# Intercept always-on repeating command blocks (auto=true)
intercept-repeating-auto: false

# Disable command block minecarts
ban-command-minecart: false

# Enable regex-based command interception
regex-intercept-enabled: false

# Regex interception list
# Format: regex pattern : block message
regex-block-list:
  - pattern: ^say
    message: The 'say' command is not allowed!
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
* [ ] i18n (internationalization) support


## 📄 **License**

This project is licensed under the **MIT License**. You are free to modify and use the plugin. For commercial deployment, please credit the original source.

