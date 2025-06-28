package tech.softwareologists.qa.core

import java.nio.file.Path
import java.time.Instant

/**
 * SPI interfaces for sandbox emulators, launchers, and database managers.
 */

/** Describes an interaction captured by the HTTP emulator. */
data class HttpInteraction(
    val method: String,
    val path: String,
    val headers: Map<String, String> = emptyMap(),
    val body: String? = null
)

/** Possible types of file events captured by the File I/O emulator. */
enum class FileEventType { CREATE, MODIFY, DELETE, MOVE }

/** Describes a file system event. */
data class FileEvent(
    val type: FileEventType,
    val path: Path,
    val timestamp: Instant
)

/** Configuration used by [LauncherPlugin] implementations. */
data class LaunchConfig(
    val executable: Path,
    val args: List<String> = emptyList(),
    val environment: Map<String, String> = emptyMap(),
    val workingDir: Path? = null
)

/** Connection details returned by [DatabaseManager] when starting a database. */
data class DatabaseInfo(
    val jdbcUrl: String,
    val username: String,
    val password: String
)

/**
 * Emulates HTTP interactions so they can be recorded and replayed.
 */
interface HttpEmulator {
    /** Starts the emulator and returns the base URL that the SUT should use. */
    fun start(): String

    /** Stops the emulator and releases any resources. */
    fun stop()

    /** Returns all HTTP interactions captured since start. */
    fun interactions(): List<HttpInteraction>
}

/**
 * Monitors and replays file system events for the sandboxed application.
 */
interface FileIoEmulator {
    /** Begins watching the given directories for file events. */
    fun watch(paths: List<Path>)

    /** Stops watching and releases any resources. */
    fun stop()

    /** Returns all file events captured since watching began. */
    fun events(): List<FileEvent>
}

/**
 * Launches the system under test with the provided configuration.
 */
interface LauncherPlugin {
    /** True if the plugin can handle this configuration. */
    fun supports(config: LaunchConfig): Boolean

    /** Launches the process and returns its handle. */
    fun launch(config: LaunchConfig): Process
}

/**
 * Manages a sandbox database lifecycle and exporting of dumps.
 */
interface DatabaseManager {
    /** Starts the database and returns connection information. */
    fun startDatabase(): DatabaseInfo

    /** Exports a dump of the current database state to the given path. */
    fun exportDump(target: Path)

    /** Stops the database and cleans up resources. */
    fun stop()
}

