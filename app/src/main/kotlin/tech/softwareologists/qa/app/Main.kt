package tech.softwareologists.qa.app

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.path
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import java.nio.file.Path
import tech.softwareologists.qa.core.FlowIO
import tech.softwareologists.qa.core.FlowStep
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

class BranchCreateCommand : CliktCommand(name = "create", help = "Create a new branch from a flow") {
    private val base by option("--base", help = "Base flow YAML")
        .path(mustExist = true)
        .required()
    private val at by option("--at", help = "Step ID to branch at").required()
    private val name by option("--name", help = "Name of the new branch").required()

    override fun run() {
        val flow = FlowIO.read(base)
        val index = flow.steps.indexOfFirst { it.id == at }
        if (index < 0) {
            echo("Step '$at' not found in ${'$'}base")
            return
        }

        val branch = flow.copy(steps = flow.steps.subList(0, index + 1))
        val target = (base.parent ?: Path.of(".")).resolve("${name}.yaml")
        FlowIO.write(branch, target)
        echo("Created branch $name at step $at -> $target")
    }
}

class BranchCommand : CliktCommand(help = "Manage branches") {
    init {
        subcommands(BranchCreateCommand())
    }

    override fun run() = Unit
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
