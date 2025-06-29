package tech.softwareologists.qa.database

import org.h2.tools.Script
import tech.softwareologists.qa.core.DatabaseInfo
import tech.softwareologists.qa.core.DatabaseManager
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager

/** Simple in-memory H2 implementation of [DatabaseManager]. */
class H2DatabaseManager : DatabaseManager {
    private var connection: Connection? = null
    private var jdbcUrl: String = ""

    override fun startDatabase(): DatabaseInfo {
        jdbcUrl = "jdbc:h2:mem:qa_helper;DB_CLOSE_DELAY=-1"
        connection = DriverManager.getConnection(jdbcUrl, "sa", "")
        return DatabaseInfo(jdbcUrl, "sa", "")
    }

    override fun exportDump(target: Path) {
        val conn = requireNotNull(connection) { "Database not started" }
        Script.process(conn, target.toString(), "", "")
    }

    override fun stop() {
        connection?.close()
        connection = null
    }
}
