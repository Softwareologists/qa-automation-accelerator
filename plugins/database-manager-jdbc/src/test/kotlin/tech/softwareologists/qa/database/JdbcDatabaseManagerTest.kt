package tech.softwareologists.qa.database

import kotlin.test.Test
import kotlin.test.assertTrue
import java.nio.file.Files
import java.sql.DriverManager

class JdbcDatabaseManagerTest {
    @Test
    fun `connects via connection string and exports dump`() {
        val url = "jdbc:h2:mem:external;DB_CLOSE_DELAY=-1"
        val manager = JdbcDatabaseManager(url, "sa", "")
        val info = manager.startDatabase()
        DriverManager.getConnection(info.jdbcUrl, info.username, info.password).use { conn ->
            conn.createStatement().use { st ->
                st.executeUpdate("CREATE TABLE test(id INT PRIMARY KEY, name VARCHAR(100))")
                st.executeUpdate("INSERT INTO test VALUES(1, 'hello')")
            }
        }
        val dump = Files.createTempFile("ext-dump", ".sql")
        manager.exportDump(dump)
        val text = Files.readString(dump)
        assertTrue(text.contains("INSERT INTO \"TEST\""))
        assertTrue(text.contains("'hello'"))
        manager.stop()
    }

    @Test
    fun `seeds and cleans up via jdbc`() {
        val url = "jdbc:h2:mem:ext2;DB_CLOSE_DELAY=-1"
        val manager = JdbcDatabaseManager(url, "sa", "")
        val info = manager.startDatabase()
        val dataset = Files.createTempFile("jdbc-seed", ".sql")
        Files.writeString(dataset, "CREATE TABLE item(id INT);INSERT INTO item VALUES(2)")
        manager.seed(dataset)
        DriverManager.getConnection(info.jdbcUrl, info.username, info.password).use { conn ->
            conn.createStatement().use { st ->
                val rs = st.executeQuery("SELECT COUNT(*) FROM item")
                rs.next()
                assertTrue(rs.getInt(1) == 1)
            }
        }
        manager.cleanup()
        DriverManager.getConnection(info.jdbcUrl, info.username, info.password).use { conn ->
            val meta = conn.metaData
            meta.getTables(null, null, "ITEM", null).use { rs ->
                assertTrue(!rs.next())
            }
        }
        manager.stop()
    }
}
