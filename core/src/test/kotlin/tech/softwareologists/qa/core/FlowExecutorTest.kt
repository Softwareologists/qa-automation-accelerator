package tech.softwareologists.qa.core

import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private class FakeHttpEmulator(private val stubs: List<HttpInteraction>) : HttpEmulator {
    private val recorded = mutableListOf<HttpInteraction>()

    override fun start(): String = "http://localhost"

    override fun stop() {}

    override fun interactions(): List<HttpInteraction> = recorded.toList()

    fun request(method: String, path: String) {
        val stub = stubs.firstOrNull { it.method == method && it.path == path }
        recorded += stub ?: HttpInteraction(method, path)
    }
}

private class FakeFileIoEmulator : FileIoEmulator {
    override fun watch(paths: List<Path>) {}
    override fun stop() {}
    override fun events(): List<FileEvent> = emptyList()
}

private class RequestLauncher(private val emulator: FakeHttpEmulator) : LauncherPlugin {
    override fun supports(config: LaunchConfig): Boolean = true

    override fun launch(config: LaunchConfig): Process {
        emulator.request("GET", "/users/42")
        return ProcessBuilder("true").start()
    }
}

private class NoopLauncher : LauncherPlugin {
    override fun supports(config: LaunchConfig): Boolean = true
    override fun launch(config: LaunchConfig): Process = ProcessBuilder("true").start()
}

class FlowExecutorTest {
    @Test
    fun playback_succeeds_when_interactions_match() {
        val stub = HttpInteraction("GET", "/users/42")
        val flow = Flow(
            version = "1",
            appVersion = "test",
            variables = mapOf("id" to "42"),
            emulator = EmulatorData(HttpData(listOf(HttpInteraction("GET", "/users/${'$'}{id}"))), FileData()),
            steps = emptyList()
        )

        val http = FakeHttpEmulator(listOf(stub))
        val executor = FlowExecutor(
            http,
            FakeFileIoEmulator(),
            RequestLauncher(http)
        )

        val tempDir = createTempDirectory()
        executor.playback(flow, LaunchConfig(Paths.get("/usr/bin/true"), workingDir = tempDir))
        tempDir.toFile().deleteRecursively()
    }

    @Test
    fun playback_throws_on_mismatch() {
        val stub = HttpInteraction("GET", "/hello")
        val flow = Flow(
            version = "1",
            appVersion = "test",
            emulator = EmulatorData(HttpData(listOf(stub)), FileData()),
            steps = emptyList()
        )

        val executor = FlowExecutor(
            FakeHttpEmulator(listOf(stub)),
            FakeFileIoEmulator(),
            NoopLauncher()
        )
        val tempDir = createTempDirectory()
        val ex = assertFailsWith<IllegalStateException> {
            executor.playback(flow, LaunchConfig(Paths.get("/usr/bin/true"), workingDir = tempDir))
        }
        assertTrue(ex.message!!.contains("HTTP interactions mismatch"))
        tempDir.toFile().deleteRecursively()
    }
}
