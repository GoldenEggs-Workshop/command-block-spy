package gold.eggs.commandblockspy

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
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
            MainPlugin.instance.logger.info(
                I18nManager.get(
                    "monitor.execution.console",
                    mapOf(
                        "type" to info.type,
                        "world" to info.worldName,
                        "x" to info.x.toString(),
                        "y" to info.y.toString(),
                        "z" to info.z.toString(),
                        "command" to info.command
                    )
                )
            )
        }

        // 如果玩家开启了监控，则发送消息
        for (player in Bukkit.getOnlinePlayers()) {
            if (ConfigManager.isPlayerMonitoring(player.uniqueId)) {
                sendMessageWithLocation(
                    player,
                    "monitor.execution.player",
                    mapOf(
                        "type" to info.type,
                        "command" to info.command
                    ),
                    info,
                    prefixKey = null  // 不加 prefix，直接发送消息
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
            val location = sender.location
            MainPlugin.instance.logger.info(
                I18nManager.get(
                    "intercept.minecart.console",
                    mapOf(
                        "world" to location.world.name,
                        "x" to location.blockX.toString(),
                        "y" to location.blockY.toString(),
                        "z" to location.blockZ.toString()
                    )
                )
            )
            return
        }

        // 提取命令方块信息，如果无法提取则返回
        val info = extractCommandBlockInfo(event) ?: return

        // 如果配置中拦截重复命令方块，并且命令方块是循环类型，则拦截并关闭
        if (ConfigManager.interceptRepeatingAuto && info.block.type == Material.REPEATING_COMMAND_BLOCK) {
            if (info.commandBlock.auto == true) {
                info.commandBlock.auto = false
                event.isCancelled = true

                for (player in Bukkit.getOnlinePlayers()) {
                    if (!player.hasPermission("cbspy.intercept")) continue
                    sendMessageWithLocation(player, "intercept.repeating.player", emptyMap(), info)
                }

                MainPlugin.instance.logger.info(
                    I18nManager.get(
                        "intercept.repeating.console", mapOf(
                            "world" to info.worldName,
                            "x" to info.x.toString(),
                            "y" to info.y.toString(),
                            "z" to info.z.toString(),
                            "command" to info.command
                        )
                    )
                )
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
                        if (!player.hasPermission("cbspy.intercept")) continue
                        sendMessageWithLocation(
                            player,
                            "intercept.pattern.player",
                            mapOf("reason" to reason),
                            info
                        )
                    }

                    MainPlugin.instance.logger.info(
                        I18nManager.get(
                            "intercept.pattern.console", mapOf(
                                "reason" to reason,
                                "world" to info.worldName,
                                "x" to info.x.toString(),
                                "y" to info.y.toString(),
                                "z" to info.z.toString(),
                                "command" to info.command
                            )
                        )
                    )
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
            val msg = I18nManager.get(
                "intercept.minecart.player",
                mapOf("location" to "${entity.location.world.name}, ${entity.location.blockX}, ${entity.location.blockY}, ${entity.location.blockZ}")
            )
            for (player in Bukkit.getOnlinePlayers()) {
                // 判断是否有权限
                if (!player.hasPermission("cbspy.intercept")) continue
                player.sendMessage(
                    Component.text(
                        I18nManager.get("prefix.cbspy.player", mapOf("msg" to msg))
                    )
                )
            }

            MainPlugin.instance.logger.info(
                I18nManager.get(
                    "intercept.minecart.console",
                    mapOf(
                        "world" to entity.location.world.name,
                        "x" to entity.location.blockX.toString(),
                        "y" to entity.location.blockY.toString(),
                        "z" to entity.location.blockZ.toString()
                    )
                )
            )
        }
    }

    fun sendMessageWithLocation(
        player: Player,
        messageKey: String,
        placeholders: Map<String, String> = emptyMap(),
        info: CommandBlockInfo,
        prefixKey: String? = "prefix.cbspy.player" // 传 null 就不加 prefix
    ) {
        val raw = I18nManager.get(messageKey, placeholders + ("location" to "{location}"))
        val parts = raw.split("{location}", limit = 2)

        val locationComponent = Component.text(
            I18nManager.get(
                "location", mapOf(
                    "world" to info.worldName,
                    "x" to info.x.toString(),
                    "y" to info.y.toString(),
                    "z" to info.z.toString()
                )
            )
        )
            .clickEvent(ClickEvent.runCommand("/tp @s ${info.x} ${info.y} ${info.z}"))
            .hoverEvent(HoverEvent.showText(Component.text(I18nManager.get("tp.hover_event"))))

        val messageComponent = Component.text()
            .append(Component.text(parts[0]))
            .append(locationComponent)
            .append(Component.text(parts.getOrElse(1) { "" }))

        if (prefixKey == null) {
            // 不带 prefix，直接发消息
            player.sendMessage(messageComponent)
        } else {
            // 带 prefix，解析 prefix 内的 {msg} 占位符位置
            val prefixRaw = I18nManager.get(prefixKey, mapOf("msg" to "__MSG__"))
            val prefixParts = prefixRaw.split("__MSG__", limit = 2)

            val prefixComponent = Component.text()
                .append(Component.text(prefixParts[0]))
                .append(messageComponent)
                .append(Component.text(prefixParts.getOrElse(1) { "" }))

            player.sendMessage(prefixComponent)
        }
    }
}