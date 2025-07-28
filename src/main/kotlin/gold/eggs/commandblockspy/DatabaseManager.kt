package gold.eggs.commandblockspy

import org.bukkit.Location
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class CommandRecord(
    val command: String,
    val world: String,
    val x: Int,
    val y: Int,
    val z: Int,
    val executionCount: Int,
    val lastExecution: String
)

object DatabaseManager {
    private lateinit var connection: Connection
    private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun init() {
        val dbFile = File(MainPlugin.instance.dataFolder, "cbspy.db")
        dbFile.parentFile.mkdirs()
        connection = DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}")

        val stmt = connection.createStatement()
        stmt.executeUpdate(
            """
            CREATE TABLE IF NOT EXISTS command_executions (
                command TEXT,
                world TEXT,
                x INT,
                y INT,
                z INT,
                execution_count INT,
                last_execution TEXT,
                PRIMARY KEY (world, x, y, z)
            )
            """
        )
        stmt.close()
    }

    fun recordCommandExecution(cmd: String, loc: Location) {
        val now = LocalDateTime.now().format(dateFormat)
        val sql = """
            INSERT INTO command_executions (command, world, x, y, z, execution_count, last_execution)
            VALUES (?, ?, ?, ?, ?, 1, ?)
            ON CONFLICT(world, x, y, z)
            DO UPDATE SET
                execution_count = execution_count + 1,
                command = excluded.command,
                last_execution = excluded.last_execution
        """

        connection.prepareStatement(sql).use { ps ->
            ps.setString(1, cmd)
            ps.setString(2, loc.world.name)
            ps.setInt(3, loc.blockX)
            ps.setInt(4, loc.blockY)
            ps.setInt(5, loc.blockZ)
            ps.setString(6, now)
            ps.executeUpdate()
        }
    }

    fun queryByLocation(loc: Location): CommandRecord? {
        val sql = """
            SELECT * FROM command_executions WHERE world = ? AND x = ? AND y = ? AND z = ?
        """
        connection.prepareStatement(sql).use { ps ->
            ps.setString(1, loc.world.name)
            ps.setInt(2, loc.blockX)
            ps.setInt(3, loc.blockY)
            ps.setInt(4, loc.blockZ)
            ps.executeQuery().use { rs ->
                return if (rs.next()) resultToRecord(rs) else null
            }
        }
    }

    fun queryRecentExecutions(limit: Int): List<CommandRecord> {
        val sql = """
            SELECT * FROM command_executions ORDER BY last_execution DESC LIMIT ?
        """
        connection.prepareStatement(sql).use { ps ->
            ps.setInt(1, limit)
            ps.executeQuery().use { rs ->
                val result = mutableListOf<CommandRecord>()
                while (rs.next()) {
                    result.add(resultToRecord(rs))
                }
                return result
            }
        }
    }

    private fun resultToRecord(rs: ResultSet): CommandRecord {
        return CommandRecord(
            command = rs.getString("command"),
            world = rs.getString("world"),
            x = rs.getInt("x"),
            y = rs.getInt("y"),
            z = rs.getInt("z"),
            executionCount = rs.getInt("execution_count"),
            lastExecution = rs.getString("last_execution")
        )
    }

    fun close() {
        if (::connection.isInitialized && !connection.isClosed) {
            connection.close()
        }
    }
}
