package gold.eggs.commandblockspy

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class MonitorTabCompleter : TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>? {
        if (args.size == 1) {
            val subcommands = mutableListOf<String>()
            if (sender.hasPermission("cbspy.reload")) {
                subcommands.add("reload")
            }
            // 普通玩家没有子命令，这里可留空或添加其他普通命令
            return subcommands.filter { it.startsWith(args[0].lowercase()) }.toMutableList()
        }
        return mutableListOf()
    }
}