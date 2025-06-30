package tech.softwareologists.qa.launcher

import org.junit.Assume.assumeTrue
import tech.softwareologists.qa.core.LaunchConfig
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DotNetLauncherPluginTest {
    @Test
    fun supports_dll_files() {
        val plugin = DotNetLauncherPlugin()
        val config = LaunchConfig(Path.of("sample.dll"))
        assertTrue(plugin.supports(config))
    }

    @Test
    fun should_launch_dotnet_process() {
        assumeTrue("dotnet CLI is not available", isDotnetInstalled())

        val dir = createTempDirectory()
        val dll = createTestAssembly(dir)
        val plugin = DotNetLauncherPlugin()
        val process = plugin.launch(LaunchConfig(dll))
        val exit = process.waitFor()
        assertEquals(0, exit)
        dir.toFile().deleteRecursively()
    }

    private fun createTestAssembly(tempDir: Path): Path {
        val projectDir = tempDir.resolve("Hello")
        ProcessBuilder("dotnet", "new", "console", "-o", projectDir.toString(), "--no-restore")
            .start().waitFor()
        val outDir = projectDir.resolve("out")
        ProcessBuilder("dotnet", "build", projectDir.toString(), "-c", "Release", "-o", outDir.toString())
            .start().waitFor()
        return outDir.resolve("Hello.dll")
    }

    private fun isDotnetInstalled(): Boolean = try {
        ProcessBuilder("dotnet", "--version").start().waitFor() == 0
    } catch (e: Exception) {
        false
    }
}
