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

        when (sender) {
            is Player -> {
                val enabled = ConfigManager.togglePlayerMonitoring(sender.uniqueId)
                val msg = if (enabled) "§a你已开启命令方块监控显示"  // §a 是绿色
                else "§c你已关闭命令方块监控显示"           // §c 是红色
                sender.sendMessage("§e[CBSpy] $msg")                 // §e 是黄色
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