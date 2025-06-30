package tech.softwareologists.qa.core

import com.fasterxml.jackson.module.kotlin.registerKotlinModule

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

    /**
     * Writes HTTP interactions, file events, and an optional database dump to
     * `reports/{flow}/{timestamp}` under [reportRoot].
     */
    fun collectEvidence(flowName: String, reportRoot: java.nio.file.Path) {
        val timestamp = java.time.format.DateTimeFormatter.ISO_INSTANT
            .format(java.time.Instant.now())
        val targetDir = reportRoot.resolve(flowName).resolve(timestamp)
        java.nio.file.Files.createDirectories(targetDir)

        val mapper =
            com.fasterxml.jackson.databind.ObjectMapper()
                .registerKotlinModule()
                .findAndRegisterModules()

        java.nio.file.Files.newBufferedWriter(targetDir.resolve("http_interactions.json")).use {
            mapper.writeValue(it, httpEmulator.interactions())
        }

        java.nio.file.Files.newBufferedWriter(targetDir.resolve("file_events.json")).use {
            mapper.writeValue(it, fileIoEmulator.events())
        }

        databaseManager?.exportDump(targetDir.resolve("db_dump.sql"))
    }
}
