package gold.eggs.commandblockspy

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
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

        if (!event.isCancelled && ConfigManager.databaseLoggingEnabled) {
            DatabaseManager.recordCommandExecution(
                info.command,
                info.location
            )
        }

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
                                .hoverEvent(HoverEvent.showText(Component.text("点击传送到该命令方块")))
                        )
                        .append(Component.text(" §f| §7${info.command}"))
                )
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun commandBlockInterceptor(event: ServerCommandEvent) {

        // 如果配置中禁止命令方块矿车，并且发送者是命令方块矿车，则拦截并移除
        if (ConfigManager.banCommandMinecart && event.sender is CommandMinecart) {
            val sender = event.sender as Entity
            event.isCancelled = true
            sender.remove()
            MainPlugin.instance.logger.info("[拦截] 命令方块矿车 | ${sender.location.world},${sender.location.blockX},${sender.location.blockY},${sender.location.blockZ}")
            return
        }

        // 提取命令方块信息，如果无法提取则返回
        val info = extractCommandBlockInfo(event) ?: return

        // 如果配置中拦截重复命令方块，并且命令方块是循环类型，则拦截并关闭
        if (ConfigManager.interceptRepeatingAuto && info.type == "循环") {
            if (info.commandBlock.auto == true) {
                info.commandBlock.auto = false
                event.isCancelled = true

                for (player in Bukkit.getOnlinePlayers()) {
                    player.sendMessage(
                        Component.text("§e[CBSpy] §c已拦截循环保持开启命令方块 ")
                            .append(
                                Component.text("§a${info.worldName}, ${info.x}, ${info.y}, ${info.z}")
                                    .clickEvent(ClickEvent.runCommand("/tp @s ${info.x} ${info.y} ${info.z}"))
                                    .hoverEvent(HoverEvent.showText(Component.text("点击传送到该命令方块")))
                            )
                    )
                }

                MainPlugin.instance.logger.info("[拦截] 循环命令方块 | ${info.worldName},${info.x},${info.y},${info.z} | ${info.command}")
                return
            }
        }

        // 如果配置中启用了正则拦截，并且命令匹配正则，则拦截并发送消息
        if (ConfigManager.regexInterceptEnabled) {
            for ((regex, reason) in ConfigManager.regexBlockList) {
                if (regex.containsMatchIn(info.command)) {
                    // 拦截指令执行
                    event.isCancelled = true

                    for (player in Bukkit.getOnlinePlayers()) {
                        player.sendMessage(
                            Component.text("§e[CBSpy] §c已拦截命令方块: §6$reason ")
                                .append(
                                    Component.text("§a${info.worldName}, ${info.x}, ${info.y}, ${info.z}")
                                        .clickEvent(ClickEvent.runCommand("/tp @s ${info.x} ${info.y} ${info.z}"))
                                        .hoverEvent(HoverEvent.showText(Component.text("点击传送到该命令方块")))
                                )
                        )
                    }
                    MainPlugin.instance.logger.info("[拦截] 正则表达式 | $reason | ${info.worldName},${info.x},${info.y},${info.z} | ${info.command}")
                    break
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onMinecartCreate(event: VehicleCreateEvent) {
        // 如果配置中禁止命令方块矿车，则拦截并发送消息
        if (!ConfigManager.banCommandMinecart) return
        val entity = event.vehicle
        if (entity is CommandMinecart) {
            event.isCancelled = true
            for (player in Bukkit.getOnlinePlayers()) {
                player.sendMessage(Component.text("§e[CBSpy] §c已拦截命令方块矿车: §a${entity.location.world.name}, ${entity.location.blockX}, ${entity.location.blockY}, ${entity.location.blockZ}"))
            }
            MainPlugin.instance.logger.info("[拦截] 命令方块矿车 | ${entity.location.world},${entity.location.blockX},${entity.location.blockY},${entity.location.blockZ}")
        }
    }
}