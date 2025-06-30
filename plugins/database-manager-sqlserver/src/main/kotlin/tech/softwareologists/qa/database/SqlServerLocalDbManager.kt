package tech.softwareologists.qa.database

import tech.softwareologists.qa.core.DatabaseInfo
import tech.softwareologists.qa.core.DatabaseManager
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager
import java.util.UUID

/** Implementation of [DatabaseManager] using SQL Server Express LocalDB. */
class SqlServerLocalDbManager : DatabaseManager {
    private var instanceName: String? = null
    private var databaseName: String? = null
    private var connection: Connection? = null

    override fun startDatabase(): DatabaseInfo {
        instanceName = "QAHelper_${UUID.randomUUID().toString().replace('-', '_')}"
        databaseName = "sandbox_${UUID.randomUUID().toString().replace('-', '_')}"
        runCommand("sqllocaldb", "create", instanceName!!)
        runCommand("sqllocaldb", "start", instanceName!!)
        val masterUrl = "jdbc:sqlserver://(localdb)\\${instanceName};databaseName=master;integratedSecurity=true;"
        DriverManager.getConnection(masterUrl).use { conn ->
            conn.createStatement().executeUpdate("CREATE DATABASE [${databaseName}]")
        }
        val jdbcUrl = "jdbc:sqlserver://(localdb)\\${instanceName};databaseName=${databaseName};integratedSecurity=true;"
        connection = DriverManager.getConnection(jdbcUrl)
        return DatabaseInfo(jdbcUrl, "", "")
    }

    override fun seed(dataset: Path) {
        val conn = requireNotNull(connection) { "Database not started" }
        val statements = java.nio.file.Files.readString(dataset)
            .split(";")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        conn.createStatement().use { stmt ->
            for (sql in statements) {
                stmt.execute(sql)
            }
        }
    }

    override fun exportDump(target: Path) {
        val inst = requireNotNull(instanceName) { "Database not started" }
        val db = requireNotNull(databaseName) { "Database not started" }
        val process = ProcessBuilder(
            "SqlPackage",
            "/Action:Script",
            "/SourceServerName:(localdb)\\$inst",
            "/SourceDatabaseName:$db",
            "/TargetFile:${target.toAbsolutePath()}"
        ).inheritIO().start()
        val exit = process.waitFor()
        check(exit == 0) { "SqlPackage failed with exit code $exit" }
    }

    override fun cleanup() {
        val conn = requireNotNull(connection) { "Database not started" }
        val tables = mutableListOf<Pair<String?, String>>()
        val meta = conn.metaData
        meta.getTables(null, null, "%", arrayOf("TABLE")).use { rs ->
            while (rs.next()) {
                val schema = rs.getString("TABLE_SCHEM")
                val table = rs.getString("TABLE_NAME")
                tables += schema to table
            }
        }
        conn.createStatement().use { stmt ->
            for ((schema, table) in tables) {
                val qualified = if (schema != null) "[$schema].[$table]" else "[$table]"
                stmt.executeUpdate("DROP TABLE $qualified")
            }
        }
    }

    override fun stop() {
        connection?.close()
        connection = null
        instanceName?.let { runCommand("sqllocaldb", "stop", it) }
        instanceName?.let { runCommand("sqllocaldb", "delete", it) }
        instanceName = null
        databaseName = null
    }

    private fun runCommand(vararg cmd: String) {
        val proc = ProcessBuilder(*cmd).inheritIO().start()
        val code = proc.waitFor()
        check(code == 0) { "Command '${cmd.joinToString(" ")}' failed with exit code $code" }
    }
}
