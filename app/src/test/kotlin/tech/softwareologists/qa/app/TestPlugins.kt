package tech.softwareologists.qa.app

import tech.softwareologists.qa.core.*
import java.nio.file.Path
import java.time.Instant
import java.nio.file.Files

class TestHttpEmulator : HttpEmulator {
    private val recorded = mutableListOf<HttpInteraction>()
    override fun start(): String {
        instance = this
        recorded.clear()
        return "http://localhost"
    }
    override fun stop() {}
    override fun interactions(): List<HttpInteraction> = recorded.toList()
    fun record(method: String = "GET", path: String = "/hello") {
        recorded += HttpInteraction(method, path)
    }
    companion object {
        var instance: TestHttpEmulator? = null
    }
}

class TestFileIoEmulator : FileIoEmulator {
    override fun watch(paths: List<Path>) {}
    override fun stop() {}
    override fun events(): List<FileEvent> = listOf(
        FileEvent(FileEventType.CREATE, Path.of("a.txt"), Instant.EPOCH)
    )
}

class TestLauncher : LauncherPlugin {
    override fun supports(config: LaunchConfig): Boolean = true
    override fun launch(config: LaunchConfig): Process {
        TestHttpEmulator.instance?.record()
        return ProcessBuilder("true").start()
    }
}

class TestDatabaseManager : DatabaseManager {
    override fun startDatabase(): DatabaseInfo = DatabaseInfo("", "", "")
    override fun seed(dataset: Path) {}
    override fun exportDump(target: Path) {
        Files.writeString(target, "dump")
    }
    override fun cleanup() {}
    override fun stop() {}
}
