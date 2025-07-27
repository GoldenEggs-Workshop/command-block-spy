package gold.eggs

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.command.BlockCommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerCommandEvent
import org.bukkit.plugin.java.JavaPlugin
import java.text.SimpleDateFormat
import java.util.*


class Main : JavaPlugin(), Listener {
    private val monitorEnabledPlayers = mutableSetOf<UUID>()

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
        saveDefaultConfig()
        logger.info("命令方块检测器已启动！")
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun commandBlockMonitor(event: ServerCommandEvent) {
        val commandBlock = event.sender as? BlockCommandSender ?: return
        val command = event.command
        val block = commandBlock.block
        val loc = block.location

        // 获取命令方块类型
        val type = when (block.type) {
            Material.REPEATING_COMMAND_BLOCK -> "循环"
            Material.CHAIN_COMMAND_BLOCK -> "连锁"
            Material.COMMAND_BLOCK -> "普通"
            else -> "未知"
        }
        // 格式化坐标
        val (x, y, z) = listOf(loc.blockX, loc.blockY, loc.blockZ)
        val world = loc.world?.name ?: "未知世界"

        // 时间戳
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())

        // 构建日志信息
//        logger.info(
//            "§c[命令方块执行] §e$type §f| §b$time §f| " +
//                    "§6坐标: §a$world§f,§a$x§f,§a$y§f,§a$z §f| " +
//                    "§d命令: §7$command"
//        )
//        event.sender.server.broadcastMessage(
//            "§c[命令方块执行] §e$type §f| §b$time §f| " +
//                    "§6坐标: §a$world§f,§a$x§f,§a$y§f,§a$z §f| " +
//                    "§d命令: §7$command"
//        )
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun commandBlockInterceptor(event: ServerCommandEvent) {
        val commandBlock = event.sender as? BlockCommandSender ?: return
        val command = event.command
        val block = commandBlock.block

        // 获取命令方块类型
        val type = when (block.type) {
            Material.REPEATING_COMMAND_BLOCK -> "循环"
            Material.CHAIN_COMMAND_BLOCK -> "连锁"
            Material.COMMAND_BLOCK -> "普通"
            else -> "未知"
        }

        if (type == "循环") {
            if (commandBlock.auto == true) {
                commandBlock.auto = false
                event.sender.server.sendMessage(Component.text("§c[命令方块拦截] §e检测到循环命令方块，已自动禁用。"))

            }
        }
    }
}