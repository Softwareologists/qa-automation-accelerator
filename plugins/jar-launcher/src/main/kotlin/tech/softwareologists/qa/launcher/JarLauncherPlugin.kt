package tech.softwareologists.qa.launcher

import tech.softwareologists.qa.core.LaunchConfig
import tech.softwareologists.qa.core.LauncherPlugin

/**
 * [LauncherPlugin] implementation that runs a JAR using the system Java runtime.
 */
class JarLauncherPlugin : LauncherPlugin {
    override fun supports(config: LaunchConfig): Boolean =
        config.executable.toString().endsWith(".jar", ignoreCase = true)

    override fun launch(config: LaunchConfig): Process {
        val cmd = listOf(
            "java",
            "-jar",
            config.executable.toString(),
            *config.args.toTypedArray()
        )
        val builder = ProcessBuilder(cmd)
        if (config.environment.isNotEmpty()) {
            builder.environment().putAll(config.environment)
        }
        val dir = config.workingDir ?: config.executable.parent
        builder.directory(dir.toFile())
        return builder.start()
    }
}
