package gold.eggs.commandblockspy

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


class MainPlugin : JavaPlugin(), Listener {
    companion object {
        lateinit var instance: MainPlugin
            private set
    }

    override fun onEnable() {
        instance = this

        // 日志
        logger.info("插件已启动")

        // 初始化配置文件
        saveDefaultConfig()
        ConfigManager.load(config)

        // 初始化数据库（可选）
//        DatabaseManager.init()

        // 注册监听器
        server.pluginManager.registerEvents(CommandBlockListener(), this)

        // 注册命令
        getCommand("cbspy")?.setExecutor(MonitorCommand())

        // 其他初始化逻辑
    }

    override fun onDisable() {
        // 插件关闭前清理
//        DatabaseManager.close()
        logger.info("插件已关闭")
    }
}