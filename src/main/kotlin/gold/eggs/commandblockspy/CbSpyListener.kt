package gold.eggs.commandblockspy

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.entity.minecart.CommandMinecart
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerCommandEvent
import org.bukkit.event.vehicle.VehicleCreateEvent


class CbSpyListener : Listener {

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
                            Component.text("§a${info.worldName}§f, §a${info.x}§f, §a${info.y}§f, §a${info.z}")
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
                    Component.text("§e[CBSpy] §c已拦截保持开启循环命令方块: ")
                        .append(
                            Component.text("§a${info.worldName}, ${info.x}, ${info.y}, ${info.z}")
                                .clickEvent(ClickEvent.runCommand("/tp @s ${info.x} ${info.y} ${info.z}"))
                                .hoverEvent(HoverEvent.showText(Component.text("点击传送到命令方块")))
                        )
                )
                MainPlugin.instance.logger.info("[拦截] 循环命令方块 | ${info.worldName},${info.x},${info.y},${info.z} | ${info.command}")
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onMinecartCreate(event: VehicleCreateEvent) {
        if (!ConfigManager.banCommandMinecart) return

        val entity = event.vehicle
        if (entity is CommandMinecart) {
            event.isCancelled = true
            Bukkit.broadcast(Component.text("§e[CBSpy] §c已拦截命令方块矿车: §a${entity.location.world.name}, ${entity.location.blockX}, ${entity.location.blockY}, ${entity.location.blockZ}"))
            MainPlugin.instance.logger.info("[拦截] 命令方块矿车 | ${entity.location.world},${entity.location.blockX},${entity.location.blockY},${entity.location.blockZ}")
        }
    }
}