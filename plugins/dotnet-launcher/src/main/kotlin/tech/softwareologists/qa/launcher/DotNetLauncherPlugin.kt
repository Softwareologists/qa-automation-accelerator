package tech.softwareologists.qa.launcher

import tech.softwareologists.qa.core.LaunchConfig
import tech.softwareologists.qa.core.LauncherPlugin

/**
 * [LauncherPlugin] implementation that runs a .NET assembly using the system dotnet runtime.
 */
class DotNetLauncherPlugin : LauncherPlugin {
    override fun supports(config: LaunchConfig): Boolean =
        config.executable.toString().endsWith(".dll", ignoreCase = true)

    override fun launch(config: LaunchConfig): Process {
        val cmd = mutableListOf(
            "dotnet",
            config.executable.toString()
        )
        cmd.addAll(config.args)
        val builder = ProcessBuilder(cmd)
        if (config.environment.isNotEmpty()) {
            builder.environment().putAll(config.environment)
        }
        val dir = config.workingDir ?: config.executable.parent
        builder.directory(dir.toFile())
        return builder.start()
    }
}

