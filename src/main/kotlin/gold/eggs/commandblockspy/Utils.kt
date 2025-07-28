package gold.eggs.commandblockspy

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.command.BlockCommandSender
import org.bukkit.event.server.ServerCommandEvent
import java.text.SimpleDateFormat
import java.util.Date

private fun BlockCommandSender.getCommandBlockEntity(): Any? {
    return try {
        val blockField = this.javaClass.getDeclaredField("block").apply { isAccessible = true }
        val commandSourceStack = blockField.get(this)

        val sourceField = commandSourceStack.javaClass.getDeclaredField("source").apply { isAccessible = true }
        val source = sourceField.get(commandSourceStack)

        val this0Field = source.javaClass.getDeclaredField("this\$0").apply { isAccessible = true }
        this0Field.get(source)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

var BlockCommandSender.auto: Boolean?
    get() = try {
        val entity = getCommandBlockEntity() ?: return null
        val autoField = entity.javaClass.getDeclaredField("auto").apply { isAccessible = true }
        autoField.getBoolean(entity)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
    set(value) {
        if (value == null) return
        try {
            val entity = getCommandBlockEntity() ?: return
            val autoField = entity.javaClass.getDeclaredField("auto").apply { isAccessible = true }
            autoField.setBoolean(entity, value)

            val updateMethod = entity.javaClass.getMethod("setChanged")
            updateMethod.invoke(entity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

data class CommandBlockInfo(
    val commandBlock: BlockCommandSender,
    val command: String,
    val block: Block,
    val location: Location,
    val type: String,
    val x: Int,
    val y: Int,
    val z: Int,
    val worldName: String,
    val timestamp: String
)

fun extractCommandBlockInfo(event: ServerCommandEvent): CommandBlockInfo? {
    val commandBlock = event.sender as? BlockCommandSender ?: return null
    val command = event.command
    val block = commandBlock.block
    val loc = block.location

    val type = when (block.type) {
        Material.REPEATING_COMMAND_BLOCK -> "循环"
        Material.CHAIN_COMMAND_BLOCK -> "连锁"
        Material.COMMAND_BLOCK -> "普通"
        else -> "未知"
    }

    val x = loc.blockX
    val y = loc.blockY
    val z = loc.blockZ
    val worldName = loc.world?.name ?: "未知世界"

    val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())

    return CommandBlockInfo(
        commandBlock, command, block, loc,
        type, x, y, z, worldName, timestamp
    )
}