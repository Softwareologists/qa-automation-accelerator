package tech.softwareologists.qa.app

import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertTrue
import tech.softwareologists.qa.core.*
import java.nio.file.Files
import java.time.Instant

class ReplayRunCommandTest {
    private fun createFlow(dir: java.nio.file.Path): java.nio.file.Path {
        val flow = Flow(
            version = "1",
            appVersion = "test",
            emulator = EmulatorData(
                HttpData(listOf(HttpInteraction("GET", "/hello"))),
                FileData(listOf(FileEvent(FileEventType.CREATE, dir.resolve("a.txt"), Instant.EPOCH)))
            ),
            steps = emptyList()
        )
        val file = dir.resolve("flow.yaml")
        FlowIO.write(flow, file)
        return file
    }

    @Test
    fun replay_command_executes_flow() {
        val dir = createTempDirectory()
        val flowFile = createFlow(dir)
        ReplayCommand().parse(arrayOf("--flow", flowFile.toString()))
        dir.toFile().deleteRecursively()
    }

    @Test
    fun run_command_collects_evidence() {
        val dir = createTempDirectory()
        val reports = createTempDirectory()
        val flowFile = createFlow(dir)

        RunCommand().parse(arrayOf("--flow", flowFile.toString(), "--reportDir", reports.toString()))

        val flowName = flowFile.fileName.toString().substringBeforeLast('.')
        val tsDir = Files.list(reports.resolve(flowName)).findFirst().get()
        assertTrue(Files.exists(tsDir.resolve("http_interactions.json")))
        assertTrue(Files.exists(tsDir.resolve("file_events.json")))
        assertTrue(Files.exists(tsDir.resolve("db_dump.sql")))
        assertTrue(Files.exists(tsDir.resolve("junit.xml")))
        assertTrue(Files.exists(tsDir.resolve("summary.html")))

        dir.toFile().deleteRecursively()
        reports.toFile().deleteRecursively()
    }
}
