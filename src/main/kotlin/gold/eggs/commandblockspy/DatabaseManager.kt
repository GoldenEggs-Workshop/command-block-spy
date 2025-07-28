package gold.eggs.commandblockspy



object DatabaseManager {
    fun init() {
        // TODO: 初始化数据库或文件
    }

    fun recordCommandExecution(cmd: String, loc: org.bukkit.Location, time: String) {
        // TODO: 保存记录
        MainPlugin.instance.logger.info("[记录] $cmd @ ${loc.blockX}, ${loc.blockY}, ${loc.blockZ} at $time")
    }

    fun close() {
        // 可清理数据库连接等
    }
}