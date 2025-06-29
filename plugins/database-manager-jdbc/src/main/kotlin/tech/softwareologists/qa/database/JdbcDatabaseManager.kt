package tech.softwareologists.qa.database

import tech.softwareologists.qa.core.DatabaseInfo
import tech.softwareologists.qa.core.DatabaseManager
import java.nio.file.Files
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager

/**
 * [DatabaseManager] that connects to an existing database using a JDBC
 * connection string.
 */
class JdbcDatabaseManager(
    private val jdbcUrl: String,
    private val username: String,
    private val password: String
) : DatabaseManager {
    private var connection: Connection? = null

    override fun startDatabase(): DatabaseInfo {
        connection = DriverManager.getConnection(jdbcUrl, username, password)
        return DatabaseInfo(jdbcUrl, username, password)
    }

    override fun exportDump(target: Path) {
        val conn = requireNotNull(connection) { "Database not started" }
        conn.use { c ->
            val meta = c.metaData
            val tables = mutableListOf<String>()
            meta.getTables(null, null, "%", arrayOf("TABLE")).use { rs ->
                while (rs.next()) {
                    val schema = rs.getString("TABLE_SCHEM")
                    if (schema != null && schema.uppercase() != "PUBLIC") continue
                    tables += rs.getString("TABLE_NAME")
                }
            }
            val sb = StringBuilder()
            for (table in tables) {
                val columns = mutableListOf<String>()
                meta.getColumns(null, null, table, null).use { rs ->
                    while (rs.next()) {
                        columns += rs.getString("COLUMN_NAME")
                    }
                }
                c.createStatement().use { stmt ->
                    stmt.executeQuery("SELECT * FROM \"$table\"").use { rs ->
                        while (rs.next()) {
                            val values = columns.joinToString(", ") { col ->
                                val value = rs.getObject(col)
                                value?.let { "'${it.toString().replace("'", "''")}'" } ?: "NULL"
                            }
                            val cols = columns.joinToString(", ") { "\"$it\"" }
                            sb.append("INSERT INTO \"$table\"($cols) VALUES($values);\n")
                        }
                    }
                }
            }
            Files.writeString(target, sb.toString())
        }
    }

    override fun stop() {
        connection?.close()
        connection = null
    }
}
