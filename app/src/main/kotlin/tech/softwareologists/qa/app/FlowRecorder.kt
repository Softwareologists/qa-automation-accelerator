package tech.softwareologists.qa.app

import tech.softwareologists.qa.core.*
import java.nio.file.Path

/**
 * Handles start and stop of recording sessions for the Compose UI.
 */
interface FlowRecorder {
    fun start(jarPath: String)
    fun stop(output: Path)
}

/** Default [FlowRecorder] used by the application. */
class PluginFlowRecorder : FlowRecorder {
    private var http: HttpEmulator? = null
    private var fileIo: FileIoEmulator? = null
    private var process: Process? = null

    override fun start(jarPath: String) {
        val exe = Path.of(jarPath)
        val httpEmulator = PluginRegistry.httpEmulators.firstOrNull()
        val fileEmulator = PluginRegistry.fileIoEmulators.firstOrNull()
        val launcher = PluginRegistry.launcherPlugins.firstOrNull()

        if (httpEmulator == null || fileEmulator == null || launcher == null) {
            return
        }

        http = httpEmulator
        fileIo = fileEmulator

        httpEmulator.start()
        fileEmulator.watch(listOf(exe.parent))
        process = launcher.launch(LaunchConfig(exe))
    }

    override fun stop(output: Path) {
        process?.destroy()
        process?.waitFor()
        val httpEmulator = http
        val fileEmulator = fileIo
        httpEmulator?.stop()
        fileEmulator?.stop()

        val flow = Flow(
            version = "1",
            appVersion = "unknown",
            emulator = EmulatorData(
                http = HttpData(httpEmulator?.interactions() ?: emptyList()),
                file = FileData(fileEmulator?.events() ?: emptyList())
            ),
            steps = emptyList()
        )
        FlowIO.write(flow, output)

        process = null
        http = null
        fileIo = null
    }
}
