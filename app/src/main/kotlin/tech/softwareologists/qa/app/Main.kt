package tech.softwareologists.qa.app

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class RecordCommand : CliktCommand(help = "Record application interactions") {
    override fun run() {
        echo("Recording flow (TODO)")
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
