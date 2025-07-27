package gold.eggs

import org.bukkit.command.BlockCommandSender

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