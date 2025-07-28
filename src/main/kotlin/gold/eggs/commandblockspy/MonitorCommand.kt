package gold.eggs.commandblockspy

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class MonitorCommand : CommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (args.isEmpty()) {
            if (!sender.hasPermission("cbspy.use")) {
                sender.sendMessage("§c你没有权限使用此命令。")
                return true
            }

            when (sender) {
                is Player -> {
                    val enabled = ConfigManager.togglePlayerMonitoring(sender.uniqueId)
                    val msg = if (enabled) "§a你已开启命令方块监控显示"
                    else "§c你已关闭命令方块监控显示"
                    sender.sendMessage("§e[CBSpy] $msg")
                }

                is ConsoleCommandSender -> {
                    ConfigManager.backendMonitorEnabled = !ConfigManager.backendMonitorEnabled
                    ConfigManager.save()
                    val msg = if (ConfigManager.backendMonitorEnabled) "后台监控日志已开启"
                    else "后台监控日志已关闭"
                    sender.sendMessage("[CBSpy] $msg")
                }

                else -> {
                    sender.sendMessage("§c该命令仅限玩家或控制台使用")
                }
            }
        }

        if (args.isNotEmpty()) {
            if (args[0].equals("reload", ignoreCase = true)) {
                // 处理 /cbs reload 子命令
                if (!sender.hasPermission("cbspy.reload")) {
                    sender.sendMessage("§c你没有权限执行此操作。")
                    return true
                }

                val plugin = MainPlugin.instance
                plugin.reloadConfig()
                ConfigManager.load(plugin.config)
                sender.sendMessage("§e[CBSpy] §a配置已重新加载。")
                return true
            }

            // /cbs query recent <条数>
            if (args.size >= 2 && args[0].equals("query", ignoreCase = true) && args[1].equals(
                    "recent",
                    ignoreCase = true
                )
            ) {
                if (!sender.hasPermission("cbspy.query")) {
                    sender.sendMessage("§c你没有权限执行此查询。")
                    return true
                }

                val limit = args.getOrNull(2)?.toIntOrNull() ?: 5
                val results = DatabaseManager.queryRecentExecutions(limit)

                sender.sendMessage("§e[CBSpy] §a最近执行的命令（$limit 条）：")
                for ((i, row) in results.withIndex()) {
                    sender.sendMessage("§7${i + 1}. §f${row.command} §7@ (${row.x},${row.y},${row.z}) §8${row.lastExecution}")
                }
                return true
            }

            // /cbs query loc <x> <y> <z>
            if (args.size == 5 && args[0].equals("query", ignoreCase = true) && args[1].equals(
                    "loc",
                    ignoreCase = true
                )
            ) {
                if (!sender.hasPermission("cbspy.query")) {
                    sender.sendMessage("§c你没有权限执行此查询。")
                    return true
                }

                val x = parseIntArg(args[2], "X", sender) ?: return true
                val y = parseIntArg(args[3], "Y", sender) ?: return true
                val z = parseIntArg(args[4], "Z", sender) ?: return true

                val world = Bukkit.getWorld("world")
                val loc = Location(world, x.toDouble(), y.toDouble(), z.toDouble())

                val record = DatabaseManager.queryByLocation(loc)
                if (record != null) {
                    sender.sendMessage("§e[CBSpy] §a该位置记录：")
                    sender.sendMessage("§7命令：§f${record.command}")
                    sender.sendMessage("§7执行次数：§f${record.executionCount}")
                    sender.sendMessage("§7最后执行时间：§f${record.lastExecution}")
                } else {
                    sender.sendMessage("§e[CBSpy] §c没有找到该位置的记录。")
                }

                return true
            }

            // 处理未知子命令
            sender.sendMessage("§c未知子命令或参数。")

        }

        return true
    }

    fun parseIntArg(arg: String, name: String, sender: CommandSender): Int? {
        return arg.toIntOrNull() ?: run {
            sender.sendMessage("§c$name 坐标格式不正确，请输入整数。")
            null
        }
    }
}
