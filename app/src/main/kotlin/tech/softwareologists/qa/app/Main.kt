package tech.softwareologists.qa.app

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.path
import tech.softwareologists.qa.core.LaunchConfig
import tech.softwareologists.qa.core.PluginRegistry

class RecordCommand : CliktCommand(help = "Record application interactions") {
    private val executable by argument(help = "Path to application JAR/DLL").path()

    override fun run() {
        val http = PluginRegistry.httpEmulators.firstOrNull()
        val fileIo = PluginRegistry.fileIoEmulators.firstOrNull()
        val launcher = PluginRegistry.launcherPlugins.firstOrNull()

        if (http == null || fileIo == null || launcher == null) {
            echo("Required plugins not available. Cannot start recording.")
            return
        }

        http.start()
        fileIo.watch(listOf(executable.parent))
        launcher.launch(LaunchConfig(executable))

        echo("Recording started for ${executable.fileName}")
    }
}

class ReplayCommand : CliktCommand(help = "Replay a recorded flow") {
    override fun run() {
        echo("Replaying flow (TODO)")
    }
}

class BranchCommand : CliktCommand(help = "Manage branches") {
    override fun run() {
        echo("Branching (TODO)")
    }
}

class RunCommand : CliktCommand(help = "Run a flow end-to-end") {
    override fun run() {
        echo("Running flow (TODO)")
    }
}

class QaCli : CliktCommand(name = "qa-helper") {
    init {
        subcommands(
            RecordCommand(),
            ReplayCommand(),
            BranchCommand(),
            RunCommand()
        )
    }

    override fun run() = Unit
}

fun main(args: Array<String>) = QaCli().main(args)
