package tech.softwareologists.qa.fileio

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import tech.softwareologists.qa.core.FileEventType

class NioFileIoEmulatorTest {
    private lateinit var emulator: NioFileIoEmulator
    private lateinit var tempDir: Path

    @BeforeTest
    fun setUp() {
        tempDir = createTempDirectory()
    }

    @AfterTest
    fun tearDown() {
        emulator.stop()
        tempDir.toFile().deleteRecursively()
    }

    @Test
    fun should_capture_create_modify_delete_events() {
        emulator = NioFileIoEmulator()
        emulator.watch(listOf(tempDir))

        val file = tempDir.resolve("sample.txt")
        Files.writeString(file, "hello")
        Files.writeString(file, " world", java.nio.file.StandardOpenOption.APPEND)
        Files.delete(file)

        Thread.sleep(500)
        emulator.stop()

        val types = emulator.events().map { it.type }
        assertTrue(FileEventType.CREATE in types)
        assertTrue(FileEventType.MODIFY in types)
        assertTrue(FileEventType.DELETE in types)
    }
}
