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
    fun record(config: LaunchConfig) {
        // stub
    }

    /** Starts emulators with recorded data and replays the flow. */
    fun playback(config: LaunchConfig) {
        // stub
    }
}
