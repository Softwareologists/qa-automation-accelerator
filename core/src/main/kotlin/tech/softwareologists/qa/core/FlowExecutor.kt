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

    /**
     * Starts emulators with recorded data and replays the given [flow]. The
     * System Under Test (SUT) is launched via [launcher] and its HTTP
     * interactions are compared against those recorded in the flow. A mismatch
     * will throw an [IllegalStateException].
     */
    fun playback(flow: Flow, config: LaunchConfig) {
        val baseUrl = httpEmulator.start()
        fileIoEmulator.watch(listOf(config.workingDir ?: config.executable.parent))

        // Pass emulator base URL to the SUT
        val updatedConfig = config.copy(
            environment = config.environment + ("HTTP_BASE_URL" to baseUrl)
        )

        // Replay file system events while watchers are active
        simulateFileEvents(flow.emulator.file.events)

        val process = launcher.launch(updatedConfig)
        process.waitFor()

        fileIoEmulator.stop()
        httpEmulator.stop()

        val expected = flow.emulator.http.interactions.toStubMappings()
        val actual = httpEmulator.interactions().toStubMappings()
        if (actual != expected) {
            throw IllegalStateException(
                "HTTP interactions mismatch. Expected $expected, got $actual"
            )
        }
    }
}
