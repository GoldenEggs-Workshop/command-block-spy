package gold.eggs.commandblockspy

import org.bukkit.configuration.file.FileConfiguration
import java.util.*

object ConfigManager {
    // 配置项：控制后台日志输出
    var backendMonitorEnabled: Boolean = false
    var interceptRepeatingAuto = false
    var banCommandMinecart = false
    var regexInterceptEnabled = false
    val regexBlockList = mutableMapOf<Regex, String>()

    // 玩家个人监控状态（仅内存，重启失效）
    private val monitoringPlayers = mutableSetOf<UUID>()

    /**
     * 从 config.yml 加载配置
     */
    fun load(config: FileConfiguration) {
        backendMonitorEnabled = config.getBoolean("backend-monitor-enabled", false)
        interceptRepeatingAuto = config.getBoolean("intercept-repeating-auto", false)
        banCommandMinecart = config.getBoolean("ban-command-minecart", false)
    }

    /**
     * 保存配置
     */
    fun save() {
        val plugin = MainPlugin.instance
        val config = plugin.config
        config.set("backend-monitor-enabled", backendMonitorEnabled)
        config.set("intercept-repeating-auto", interceptRepeatingAuto)
        config.set("ban-command-minecart", banCommandMinecart)
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