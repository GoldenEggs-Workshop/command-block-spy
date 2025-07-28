package gold.eggs.commandblockspy

import net.kyori.adventure.text.format.NamedTextColor
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
        if (!sender.hasPermission("cbspy.use")) {
            sender.sendMessage("§c你没有权限使用此命令。")
            return true
        }

        if (args.isNotEmpty() && args[0].equals("reload", ignoreCase = true)) {
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

        return true
    }
}
