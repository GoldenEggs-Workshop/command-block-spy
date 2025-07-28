package gold.eggs.commandblockspy

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin


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
        config.options().copyDefaults(true)
        saveConfig()
        ConfigManager.load(config)

        // 初始化数据库（可选）
//        DatabaseManager.init()

        // 注册监听器
        server.pluginManager.registerEvents(CbSpyListener(), this)

        // 注册命令
        val command = getCommand("cbspy")
        command?.setExecutor(MonitorCommand())
        command?.tabCompleter = MonitorTabCompleter()

        // 其他初始化逻辑
    }

    override fun onDisable() {
        // 插件关闭前清理
//        DatabaseManager.close()
        logger.info("插件已关闭")
    }
}