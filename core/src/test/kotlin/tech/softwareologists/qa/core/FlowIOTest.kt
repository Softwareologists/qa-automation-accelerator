package tech.softwareologists.qa.core

import java.nio.file.Files
import java.time.Instant
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FlowIOTest {
    @Test
    fun round_trip_serialization() {
        val tempDir = createTempDirectory()
        val file = tempDir.resolve("flow.yaml")

        val interaction = HttpInteraction("GET", "/hello")
        val event = FileEvent(FileEventType.CREATE, tempDir.resolve("a.txt"), Instant.EPOCH)
        val flow = Flow(
            version = "1",
            appVersion = "test",
            variables = mapOf("user" to "alice"),
            emulator = EmulatorData(
                http = HttpData(listOf(interaction)),
                file = FileData(listOf(event))
            ),
            steps = listOf(FlowStep("1", "step"))
        )

        FlowIO.write(flow, file)
        val loaded = FlowIO.read(file)

        assertEquals(flow, loaded)

        Files.deleteIfExists(file)
        Files.deleteIfExists(tempDir)
    }

    @Test
    fun invalid_flow_is_rejected() {
        val dir = createTempDirectory()
        val file = dir.resolve("bad.yaml")
        Files.writeString(file, "version: '1'")

        assertFailsWith<Exception> { FlowIO.read(file) }

        Files.deleteIfExists(file)
        Files.deleteIfExists(dir)
    }
}
