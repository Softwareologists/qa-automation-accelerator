package tech.softwareologists.qa.core

/**
 * Executes recorded flows in either recording or playback mode.
 */
class FlowExecutor(
    private val httpEmulator: HttpEmulator,
    private val fileIoEmulator: FileIoEmulator,
    private val launcher: LauncherPlugin,
    private val databaseManager: DatabaseManager? = null,
) {
    /** Starts emulators and launches the SUT in recording mode. */
    fun record(config: LaunchConfig, output: java.nio.file.Path) {
        httpEmulator.start()
        fileIoEmulator.watch(listOf(config.workingDir ?: config.executable.parent))

        val process = launcher.launch(config)
        process.waitFor()

        fileIoEmulator.stop()
        httpEmulator.stop()

        val flow = Flow(
            version = "1",
            appVersion = "unknown",
            emulator = EmulatorData(
                http = HttpData(httpEmulator.interactions()),
                file = FileData(fileIoEmulator.events())
            ),
            steps = emptyList()
        )
        FlowIO.write(flow, output)
    }

    /** Starts emulators with recorded data and replays the flow. */
    fun playback(config: LaunchConfig) {
        // stub
    }
}
