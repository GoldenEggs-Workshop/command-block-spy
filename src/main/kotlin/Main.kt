import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.CommandBlock
import org.bukkit.command.BlockCommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerCommandEvent
import org.bukkit.plugin.java.JavaPlugin
import java.text.SimpleDateFormat
import java.util.*


class Main : JavaPlugin(), Listener {
    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
        logger.info("命令方块检测器已启动！")
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onCommandBlockExecute(event: ServerCommandEvent) {
        val sender = event.sender
        if (sender !is BlockCommandSender) return
        val command = event.command
        val commandBlock = sender
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
        logger.info(
            "§c[命令方块执行] §e$type §f| §b$time §f| " +
                    "§6坐标: §a$world§f,§a$x§f,§a$y§f,§a$z §f| " +
                    "§d命令: §7$command"
        )
        event.sender.server.broadcastMessage(
            "§c[命令方块执行] §e$type §f| §b$time §f| " +
                    "§6坐标: §a$world§f,§a$x§f,§a$y§f,§a$z §f| " +
                    "§d命令: §7$command"
        )

        if (command == "say 1"){
            event.isCancelled = true
            event.sender.server.broadcastMessage("已拦截")
        }
    }
}