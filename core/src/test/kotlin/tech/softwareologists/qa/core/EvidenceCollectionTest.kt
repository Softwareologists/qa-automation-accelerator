package tech.softwareologists.qa.core

import java.nio.file.Files
import java.time.Instant
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertTrue

private class RecordingHttpEmulator : HttpEmulator {
    private val recorded = mutableListOf<HttpInteraction>()
    override fun start(): String = "http://localhost"
    override fun stop() {}
    override fun interactions(): List<HttpInteraction> = recorded.toList()
    fun record() { recorded += HttpInteraction("GET", "/hello") }
}

private class RecordingFileIoEmulator : FileIoEmulator {
    override fun watch(paths: List<java.nio.file.Path>) {}
    override fun stop() {}
    override fun events(): List<FileEvent> = listOf(
        FileEvent(FileEventType.CREATE, java.nio.file.Paths.get("a.txt"), Instant.EPOCH)
    )
}

private class DummyLauncher(private val http: RecordingHttpEmulator) : LauncherPlugin {
    override fun supports(config: LaunchConfig): Boolean = true
    override fun launch(config: LaunchConfig): Process {
        http.record()
        return ProcessBuilder("true").start()
    }
}

private class DumpingDatabaseManager : DatabaseManager {
    override fun startDatabase(): DatabaseInfo = DatabaseInfo("", "", "")
    override fun exportDump(target: java.nio.file.Path) {
        Files.writeString(target, "dump")
    }
    override fun stop() {}
}

class EvidenceCollectionTest {
    @Test
    fun collectEvidence_writes_files() {
        val http = RecordingHttpEmulator()
        val executor = FlowExecutor(
            http,
            RecordingFileIoEmulator(),
            DummyLauncher(http),
            DumpingDatabaseManager()
        )
        val flow = Flow(
            version = "1",
            appVersion = "test",
            emulator = EmulatorData(
                http = HttpData(listOf(HttpInteraction("GET", "/hello"))),
                file = FileData(),
            ),
            steps = emptyList()
        )
        val dir = createTempDirectory()
        executor.playback(
            flow,
            LaunchConfig(java.nio.file.Paths.get("/usr/bin/true"), workingDir = dir)
        )
        executor.collectEvidence("sample", dir, success = true)
        val tsDir = Files.list(dir.resolve("sample")).findFirst().get()
        assertTrue(Files.exists(tsDir.resolve("http_interactions.json")))
        assertTrue(Files.exists(tsDir.resolve("file_events.json")))
        assertTrue(Files.exists(tsDir.resolve("db_dump.sql")))
        val junit = tsDir.resolve("junit.xml")
        assertTrue(Files.exists(junit))
        val suite = javax.xml.parsers.DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(junit.toFile())
            .documentElement
        assertTrue(suite.nodeName == "testsuite")

        val html = tsDir.resolve("summary.html")
        assertTrue(Files.exists(html))
        val content = Files.readString(html)
        assertTrue("Status: <strong>Passed</strong>" in content)
        dir.toFile().deleteRecursively()
    }
}
