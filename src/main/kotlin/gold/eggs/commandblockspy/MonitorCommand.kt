package gold.eggs.commandblockspy

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
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
                sender.sendMessage(I18nManager.get("command.no_permission"))
                return true
            }

            when (sender) {
                is Player -> {
                    val enabled = ConfigManager.togglePlayerMonitoring(sender.uniqueId)
                    val msg = if (enabled) I18nManager.get("command.cbspy.on.player")
                    else I18nManager.get("command.cbspy.off.player")
                    sender.sendMessage(I18nManager.get("prefix.cbspy.player", mapOf("msg" to msg)))
                }

                is ConsoleCommandSender -> {
                    ConfigManager.backendMonitorEnabled = !ConfigManager.backendMonitorEnabled
                    ConfigManager.save()
                    val msg = if (ConfigManager.backendMonitorEnabled) I18nManager.get("command.cbspy.on.console")
                    else I18nManager.get("command.cbspy.off.console")
                    sender.sendMessage(I18nManager.get("prefix.cbspy.console", mapOf("msg" to msg)))
                }

                else -> {
                    sender.sendMessage(I18nManager.get("command.invalid_sender"))
                }
            }
        }

        if (args.isNotEmpty()) {
            if (args[0].equals("reload", ignoreCase = true)) {
                // 处理 /cbs reload 子命令
                if (!sender.hasPermission("cbspy.reload")) {
                    sender.sendMessage(I18nManager.get("command.no_permission"))
                    return true
                }

                val plugin = MainPlugin.instance
                plugin.reloadConfig()
                ConfigManager.load(plugin.config)
                I18nManager.init(ConfigManager.language, plugin)
                val msg = I18nManager.get("command.cbspy.reload.success")
                sender.sendMessage(I18nManager.get("prefix.cbspy.player", mapOf("msg" to msg)))
                return true
            }

            if(ConfigManager.databaseLoggingEnabled){
                // /cbs query recent <条数>
                if (args.size >= 2 && args[0].equals("query", ignoreCase = true) && args[1].equals(
                        "recent",
                        ignoreCase = true
                    )
                ) {
                    if (!sender.hasPermission("cbspy.query")) {
                        sender.sendMessage(I18nManager.get("command.no_permission"))
                        return true
                    }

                    val limit = args.getOrNull(2)?.toIntOrNull() ?: 5
                    val results = DatabaseManager.queryRecentExecutions(limit)

                    val msg = I18nManager.get("command.cbspy.query.recent", mapOf("limit" to limit.toString()))
                    sender.sendMessage(I18nManager.get("prefix.cbspy.player", mapOf("msg" to msg)))
                    for ((i, row) in results.withIndex()) {
                        sender.sendMessage(
                            Component.text("§7${i + 1}. §f${row.command} ")
                                .append(
                                    Component.text("§7@ (${row.x},${row.y},${row.z})")
                                        .clickEvent(ClickEvent.runCommand("/tp @s ${row.x} ${row.y} ${row.z}"))
                                        .hoverEvent(HoverEvent.showText(Component.text(I18nManager.get("tp.hover_event"))))
                                )
                                .append(Component.text(" §8${row.lastExecution}"))
                        )
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
                        sender.sendMessage(I18nManager.get("command.no_permission"))
                        return true
                    }

                    val x = parseIntArg(args[2], "X", sender) ?: return true
                    val y = parseIntArg(args[3], "Y", sender) ?: return true
                    val z = parseIntArg(args[4], "Z", sender) ?: return true

                    val world = Bukkit.getWorld("world")
                    val loc = Location(world, x.toDouble(), y.toDouble(), z.toDouble())

                    val record = DatabaseManager.queryByLocation(loc)
                    if (record != null) {
                        val msg = I18nManager.get("command.cbspy.query.loc.msg")
                        sender.sendMessage(I18nManager.get("prefix.cbspy.player", mapOf("msg" to msg)))
                        sender.sendMessage(I18nManager.get("command.cbspy.query.loc.command", mapOf("command" to record.command)))
                        sender.sendMessage(I18nManager.get("command.cbspy.query.loc.execution_count", mapOf("executionCount" to record.executionCount.toString())))
                        sender.sendMessage(I18nManager.get("command.cbspy.query.loc.last_execution", mapOf("lastExecution" to record.lastExecution)))
                    } else {
                        val msg= I18nManager.get("command.cbspy.query.loc.not_found")
                        sender.sendMessage(I18nManager.get("prefix.cbspy.player", mapOf("msg" to msg)))
                    }

                    return true
                }
            }

            // 处理未知子命令
            sender.sendMessage(I18nManager.get("command.cbspy.unknown"))

        }

        return true
    }

    fun parseIntArg(arg: String, name: String, sender: CommandSender): Int? {
        return arg.toIntOrNull() ?: run {
            sender.sendMessage(I18nManager.get("command.cbspy.query.loc.invalid_format", mapOf("name" to name)))
            null
        }
    }
}
