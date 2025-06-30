package tech.softwareologists.qa.core

import java.nio.file.Files
import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConfigurationManagerTest {
    @Test
    fun findConfigFiles_returns_matching_files() {
        val dir = createTempDirectory()
        val moduleA = dir.resolve("a").resolve("application.properties")
        Files.createDirectories(moduleA.parent)
        moduleA.writeText("foo=bar")
        val moduleB = dir.resolve("b").resolve("application.yml")
        Files.createDirectories(moduleB.parent)
        moduleB.writeText("bar: baz")
        val ignore = dir.resolve("c").resolve("other.yml")
        Files.createDirectories(ignore.parent)
        ignore.writeText("ignored: true")

        val found = ConfigurationManager.findConfigFiles(dir).toSet()

        assertEquals(setOf(moduleA, moduleB), found)
        dir.toFile().deleteRecursively()
    }

    @Test
    fun readConfig_parses_properties_and_yaml() {
        val dir = createTempDirectory()
        val propsFile = dir.resolve("test.properties")
        propsFile.writeText("a=1")
        val yamlFile = dir.resolve("test.yaml")
        yamlFile.writeText("""
            server:
              port: 8080
        """.trimIndent())

        val props = ConfigurationManager.readConfig(propsFile)
        val yaml = ConfigurationManager.readConfig(yamlFile)

        assertEquals(mapOf("a" to "1"), props)
        assertEquals(mapOf("server.port" to "8080"), yaml)
        dir.toFile().deleteRecursively()
    }

    @Test
    fun persistOverrides_writes_properties() {
        val dir = createTempDirectory()
        val file = dir.resolve("override.properties")

        ConfigurationManager.persistOverrides(mapOf("x" to "y"), file)

        assertTrue(Files.exists(file))
        val loaded = ConfigurationManager.readConfig(file)
        assertEquals(mapOf("x" to "y"), loaded)

        dir.toFile().deleteRecursively()
    }
}
