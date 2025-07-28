package gold.eggs.commandblockspy

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerCommandEvent


class CommandBlockListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun commandBlockMonitor(event: ServerCommandEvent) {
        val info = extractCommandBlockInfo(event) ?: return

        DatabaseManager.recordCommandExecution(
            info.command,
            info.location,
            info.timestamp
        )

        // 如果启用了后台监控，则输出日志
        if (ConfigManager.backendMonitorEnabled) {
            MainPlugin.instance.logger.info("[执行] ${info.type} | ${info.worldName},${info.x},${info.y},${info.z} | ${info.command}")
        }

        // 如果玩家开启了监控，则发送消息
        for (player in Bukkit.getOnlinePlayers()) {
            if (ConfigManager.isPlayerMonitoring(player.uniqueId)) {
                player.sendMessage(
                    Component.text("§e[CBSpy] §6${info.type} §f| ")
                        .append(
                            Component.text("§a${info.worldName}§f,§a${info.x}§f,§a${info.y}§f,§a${info.z}")
                                .clickEvent(ClickEvent.runCommand("/tp @s ${info.x} ${info.y} ${info.z}"))
                                .hoverEvent(HoverEvent.showText(Component.text("点击传送到命令方块")))
                        )
                        .append(Component.text(" §f| §7${info.command}"))
                )
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun commandBlockInterceptor(event: ServerCommandEvent) {
        val info = extractCommandBlockInfo(event) ?: return
        if (ConfigManager.interceptRepeatingAuto && info.type == "循环") {
            if (info.commandBlock.auto == true) {
                info.commandBlock.auto = false
                event.isCancelled = true
                event.sender.server.sendMessage(
                    Component.text("§e[CBSpy] §c已拦截 ")
                        .append(
                            Component.text("§a${info.worldName}§f,§a${info.x}§f,§a${info.y}§f,§a${info.z}")
                                .clickEvent(ClickEvent.runCommand("/tp @s ${info.x} ${info.y} ${info.z}"))
                                .hoverEvent(HoverEvent.showText(Component.text("点击传送到命令方块")))
                        )
                        .append(Component.text(" §c保持开启循环命令方块"))
                )
            }
        }
    }
}