package gold.eggs.commandblockspy

import org.bukkit.configuration.file.FileConfiguration
import java.util.*

object ConfigManager {
    var databaseLoggingEnabled: Boolean = false
    var backendMonitorEnabled: Boolean = false
    var interceptRepeatingAuto = false
    var banCommandMinecart = false
    var regexInterceptEnabled = false
    val regexBlockList = mutableMapOf<Regex, String>()
    var language: String = "zh_CN"

    // 玩家个人监控状态（仅内存，重启失效）
    private val monitoringPlayers = mutableSetOf<UUID>()

    /**
     * 从 config.yml 加载配置
     */
    fun load(config: FileConfiguration) {
        databaseLoggingEnabled = config.getBoolean("database-logging-enabled", false)
        backendMonitorEnabled = config.getBoolean("backend-monitor-enabled", false)
        interceptRepeatingAuto = config.getBoolean("intercept-repeating-auto", false)
        banCommandMinecart = config.getBoolean("ban-command-minecart", false)
        regexInterceptEnabled = config.getBoolean("regex-intercept-enabled", false)
        language = config.getString("language", "zh_CN") ?: "zh_CN"
        regexBlockList.clear()

        // 加载正则拦截列表
        val list = config.getMapList("regex-block-list")
        for (entry in list) {
            val pattern = entry["pattern"]?.toString() ?: continue
            val message = entry["message"]?.toString() ?: continue
            try {
                val regex = Regex(pattern)
                regexBlockList[regex] = message
            } catch (e: Exception) {
                println("Invalid regex in config: $pattern")
                println("Error: ${e.message}")
            }
        }
    }

    /**
     * 保存配置
     */
    fun save() {
        val plugin = MainPlugin.instance
        val config = plugin.config
        config.set("database-logging-enabled", databaseLoggingEnabled)
        config.set("backend-monitor-enabled", backendMonitorEnabled)
        config.set("intercept-repeating-auto", interceptRepeatingAuto)
        config.set("ban-command-minecart", banCommandMinecart)
        config.set("regex-intercept-enabled", regexInterceptEnabled)
        plugin.saveConfig()
    }

    /**
     * 切换玩家监控状态，返回是否开启状态
     */
    fun togglePlayerMonitoring(uuid: UUID): Boolean {
        return if (monitoringPlayers.contains(uuid)) {
            monitoringPlayers.remove(uuid)
            false
        } else {
            monitoringPlayers.add(uuid)
            true
        }
    }

    /**
     * 查询某玩家是否启用了监控
     */
    fun isPlayerMonitoring(uuid: UUID): Boolean {
        return monitoringPlayers.contains(uuid)
    }
}