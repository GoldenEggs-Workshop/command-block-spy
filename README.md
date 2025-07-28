# 🧱 CommandBlockSpy

一个专为管理员设计的命令方块监控与控制插件，支持聊天提醒、数据库记录、指令拦截、权限控制与配置项开关。

✅ 兼容 Minecraft 1.21.4，基于 Paper 构建。

## ✨ 插件特性

### ✅ 1. 命令方块执行监控（数据库记录）

- 自动记录命令方块每次执行的：
    - 执行次数
    - 最后执行时间
    - 所在坐标
    - 所执行命令内容
- 可通过指令查询历史记录

### ✅ 2. 实时聊天提醒

- 玩家和后台都可以通过命令开启或关闭是否接收命令方块的实时修改提示
- 支持权限控制

### ✅ 3. 正则表达式拦截指令（可配置）

- 在 `config.yml` 中配置拦截规则（支持正则）
- 可设置拦截提示消息

### ✅ 4. 拦截保持开启循环命令方块（可开关）

### ✅ 5. 禁用命令方块矿车（可开关）

### 🚫 6. 命令方块矿车功能支持（暂未实现）

- 后续将添加对命令方块矿车的监控、拦截与记录功能

## 🧪 指令一览

| 指令                       | 功能描述          | 所需权限           |
|--------------------------|---------------|----------------|
| `/cbs`                   | 切换是否显示聊天监控提示  | `cbspy.use`    |
| `/cbs reload`            | 重载插件配置        | `cbspy.reload` |
| `/cbs query <x> <y> <z>` | 查询指定位置的历史执行记录 | `cbspy.query`  |
| `/cbs latest <count>`    | 查询最近执行的 n 条记录 | `cbspy.query`  |

## ⚙️ 配置文件示例

```yaml
# CBS插件配置文件

# 是否启用数据库功能
database-logging-enabled: false

# 是否开启后台命令执行日志监控，开启后控制台会打印命令方块执行记录
backend-monitor-enabled: false

# 是否拦截循环命令方块保持开启（auto=true）的命令，拦截后会自动设置为红石控制
intercept-repeating-auto: false

# 是否禁止命令方块矿车，开启后玩家会无法放置命令方块矿车
ban-command-minecart: false

# 是否启用基于正则表达式的命令拦截功能，拦截匹配规则的指令并提示原因
regex-intercept-enabled: false

# 正则表达式拦截列表，格式：正则表达式: 拦截提示信息
# 支持常见正则表达式语法，例如：
regex-block-list:
  - pattern: ^say
    message: 禁止使用 say 命令！
```

## 📦 构建 & 安装

1. 克隆项目到本地：

   ```
   git clone https://github.com/GoldenEggs-Workshop/command-block-spy
   cd CommandBlockSpy
   ```

2. 构建插件：

   ```
   ./gradlew build
   ```

3. 将 `build/libs/commandblockspy-xxx.jar` 拷贝至服务器 `plugins/` 文件夹，重启服务器。

## 🔐 权限列表

| 权限节点           | 功能描述     |
|----------------|----------|
| `cbspy.use`    | 切换聊天提醒   |
| `cbspy.reload` | 重载插件配置   |
| `cbspy.query`  | 查询记录相关功能 |

## 💡 TODO 计划

* [ ] 支持命令方块矿车的完整功能（记录/拦截/禁用）
* [ ] 自定义数据库配置

## 📄 许可证

本项目使用 MIT 协议发布。你可以自由修改和使用本插件，商业部署请注明来源。
