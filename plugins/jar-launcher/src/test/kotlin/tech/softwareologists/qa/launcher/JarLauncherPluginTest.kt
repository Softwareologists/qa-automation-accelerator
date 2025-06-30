package tech.softwareologists.qa.launcher

import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.Attributes
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import java.util.jar.Manifest
import javax.tools.ToolProvider
import tech.softwareologists.qa.core.LaunchConfig
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JarLauncherPluginTest {
    @Test
    fun supports_jar_files() {
        val plugin = JarLauncherPlugin()
        val config = LaunchConfig(Path.of("sample.jar"))
        assertTrue(plugin.supports(config))
    }

    @Test
    fun should_launch_jar_process() {
        val dir = createTempDirectory()
        val jar = createTestJar(dir)
        val plugin = JarLauncherPlugin()
        val process = plugin.launch(LaunchConfig(jar))
        val exit = process.waitFor()
        assertEquals(0, exit)
        dir.toFile().deleteRecursively()
    }

    private fun createTestJar(tempDir: Path): Path {
        val source = "public class Hello { public static void main(String[] args) {} }"
        val javaFile = tempDir.resolve("Hello.java")
        Files.writeString(javaFile, source)
        val compiler = ToolProvider.getSystemJavaCompiler()
        val result = compiler.run(null, null, null, javaFile.toString())
        check(result == 0)
        val classFile = tempDir.resolve("Hello.class")
        val jarFile = tempDir.resolve("hello.jar")
        val manifest = Manifest().apply {
            mainAttributes[Attributes.Name.MANIFEST_VERSION] = "1.0"
            mainAttributes[java.util.jar.Attributes.Name.MAIN_CLASS] = "Hello"
        }
        JarOutputStream(Files.newOutputStream(jarFile), manifest).use { jar ->
            jar.putNextEntry(JarEntry("Hello.class"))
            Files.newInputStream(classFile).use { it.copyTo(jar) }
            jar.closeEntry()
        }
        return jarFile
    }
}
