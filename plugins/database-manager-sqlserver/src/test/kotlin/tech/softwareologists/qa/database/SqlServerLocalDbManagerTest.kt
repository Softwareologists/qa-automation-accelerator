package tech.softwareologists.qa.database

import kotlin.test.Test
import kotlin.test.assertTrue
import java.nio.file.Files
import java.sql.DriverManager

class SqlServerLocalDbManagerTest {
    @Test
    fun `can export sql script`() {
        if (!System.getProperty("os.name").startsWith("Windows")) return
        val manager = SqlServerLocalDbManager()
        val info = manager.startDatabase()
        DriverManager.getConnection(info.jdbcUrl).use { conn ->
            conn.createStatement().use { st ->
                st.executeUpdate("CREATE TABLE test(id INT PRIMARY KEY, name VARCHAR(100))")
                st.executeUpdate("INSERT INTO test VALUES(1, 'hello')")
            }
        }
        val dump = Files.createTempFile("dump", ".sql")
        manager.exportDump(dump)
        val text = Files.readString(dump)
        assertTrue(text.contains("CREATE TABLE"))
        manager.stop()
    }
}
