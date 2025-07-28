package gold.eggs.commandblockspy

import org.bukkit.block.Block
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class MonitorTabCompleter : TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>? {
        val availableSubcommands = mutableListOf<String>()

        if (sender.hasPermission("cbspy.reload")) {
            availableSubcommands.add("reload")
        }
        if (ConfigManager.databaseLoggingEnabled && sender.hasPermission("cbspy.query")) {
            availableSubcommands.add("query")
        }
        if (availableSubcommands.isEmpty()) {
            return mutableListOf()
        }

        if (args.size == 1) {
            return availableSubcommands.filter { it.startsWith(args[0].lowercase()) }.toMutableList()
        }

        if (ConfigManager.databaseLoggingEnabled &&args.size == 2 && args[0].equals("query", ignoreCase = true) && sender.hasPermission("cbspy.query")) {
            val secondArgs = listOf("recent", "loc")
            return secondArgs.filter { it.startsWith(args[1].lowercase()) }.toMutableList()
        }

        if (ConfigManager.databaseLoggingEnabled &&args.size == 3 && args[0].equals("query", ignoreCase = true) && args[1].equals("loc", ignoreCase = true) && sender is Player) {
            val block: Block? = sender.getTargetBlockExact(10) // 获取玩家视线10格内方块，1.13以上建议使用getTargetBlockExact
            if (block != null) {
                val loc = block.location
                return mutableListOf("${loc.blockX} ${loc.blockY} ${loc.blockZ}")
            }
        }
        if (ConfigManager.databaseLoggingEnabled &&args.size == 3 && args[0].equals("query", ignoreCase = true) && args[1].equals("recent", ignoreCase = true)) {
            return mutableListOf("1", "5", "10")
        }


        return mutableListOf()
    }
}