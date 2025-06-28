package tech.softwareologists.qa.core

import java.util.ServiceLoader

/**
 * Loads available plugin implementations using [ServiceLoader].
 */
object PluginRegistry {
    /** Available [HttpEmulator] implementations. */
    val httpEmulators: List<HttpEmulator> by lazy { load() }

    /** Available [FileIoEmulator] implementations. */
    val fileIoEmulators: List<FileIoEmulator> by lazy { load() }

    /** Available [LauncherPlugin] implementations. */
    val launcherPlugins: List<LauncherPlugin> by lazy { load() }

    /** Available [DatabaseManager] implementations. */
    val databaseManagers: List<DatabaseManager> by lazy { load() }

    private inline fun <reified T> load(): List<T> =
        ServiceLoader.load(T::class.java).toList()
}
