package tech.softwareologists.qa.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import tech.softwareologists.qa.core.Flow
import tech.softwareologists.qa.core.FlowIO
import tech.softwareologists.qa.core.FlowValidator
import tech.softwareologists.qa.core.PluginRegistry
import tech.softwareologists.qa.core.FlowExecutor
import tech.softwareologists.qa.core.LaunchConfig
import tech.softwareologists.qa.app.BranchCreateCommand
import javax.swing.JFileChooser

@Composable
fun MainScreen(recorder: FlowRecorder = PluginFlowRecorder()) {
    var jarPath by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    var flowPath by remember { mutableStateOf("") }
    var flow by remember { mutableStateOf<Flow?>(null) }
    var diff by remember { mutableStateOf<List<String>?>(null) }
    val logs = remember { mutableStateListOf<String>() }
    val scope = rememberCoroutineScope()
    val logScrollState = rememberScrollState()

    fun chooseFile() {
        val chooser = JFileChooser()
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            jarPath = chooser.selectedFile.absolutePath
        }
    }

    fun startRecording() {
        recorder.start(jarPath)
        isRecording = true
    }

    fun stopRecording() {
        val target = if (flowPath.isNotBlank()) {
            java.nio.file.Path.of(flowPath)
        } else {
            java.nio.file.Path.of("flow.yaml")
        }
        recorder.stop(target)
        if (java.nio.file.Files.exists(target)) {
            val loaded = FlowIO.read(target)
            FlowValidator.validate(loaded)
            flow = loaded
        }
        isRecording = false
    }

    fun chooseFlow() {
        val chooser = JFileChooser()
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            flowPath = chooser.selectedFile.absolutePath
            val loaded = FlowIO.read(java.nio.file.Path.of(flowPath))
            FlowValidator.validate(loaded)
            flow = loaded
        }
    }

    fun branch(step: tech.softwareologists.qa.core.FlowStep) {
        if (flowPath.isNotBlank()) {
            BranchCreateCommand().main(
                arrayOf(
                    "--base", flowPath,
                    "--at", step.id,
                    "--name", step.id + "-branch"
                )
            )
        }
    }

    fun runFlow() {
        if (jarPath.isBlank()) return
        logs.clear()
        diff = null
        scope.launch {
            val http = PluginRegistry.httpEmulators.firstOrNull()
            val fileIo = PluginRegistry.fileIoEmulators.firstOrNull()
            val launcher = PluginRegistry.launcherPlugins.firstOrNull()
            val db = PluginRegistry.databaseManagers.firstOrNull()
            if (http == null || fileIo == null || launcher == null) return@launch

            val executor = FlowExecutor(http, fileIo, launcher, db)

            val loaded = if (flowPath.isNotBlank()) {
                FlowIO.read(java.nio.file.Path.of(flowPath))
            } else null

            diff = try {
                if (loaded != null) {
                    executor.playback(
                        loaded,
                        LaunchConfig(java.nio.file.Paths.get(jarPath)),
                        logHandler = { line -> logs.add(line) },
                    )
                } else {
                    val process = launcher.launch(LaunchConfig(java.nio.file.Paths.get(jarPath)))
                    val out = launch(Dispatchers.IO) {
                        process.inputStream.bufferedReader().forEachLine { logs.add(it) }
                    }
                    val err = launch(Dispatchers.IO) {
                        process.errorStream.bufferedReader().forEachLine { logs.add(it) }
                    }
                    process.waitFor()
                    out.join()
                    err.join()
                }
                null
            } catch (e: Exception) {
                e.message?.lines()?.drop(1)
            }
        }
    }

    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = jarPath,
                    onValueChange = { jarPath = it },
                    label = { Text("Application JAR/DLL") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = { chooseFile() }) { Text("Browse") }
            }

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = flowPath,
                    onValueChange = { flowPath = it },
                    label = { Text("Flow YAML") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = { chooseFlow() }) { Text("Browse") }
            }

            Spacer(Modifier.height(16.dp))
            flow?.let { f ->
                var menuStep by remember { mutableStateOf<tech.softwareologists.qa.core.FlowStep?>(null) }
                Column(modifier = Modifier.weight(1f)) {
                    f.steps.forEach { step ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { menuStep = step }
                                .padding(4.dp)
                        ) {
                            Text(step.description, modifier = Modifier.weight(1f))
                        }
                    }
                    DropdownMenu(expanded = menuStep != null, onDismissRequest = { menuStep = null }) {
                        DropdownMenuItem(text = { Text("Create Branch") }, onClick = {
                            menuStep?.let { branch(it) }
                            menuStep = null
                        })
                    }
                }
            } ?: Text("Configuration panels go here", modifier = Modifier.weight(1f))

            Row {
                Button(onClick = { startRecording() }, enabled = !isRecording) {
                    Text("Start Recording")
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { stopRecording() }, enabled = isRecording) {
                    Text("Stop")
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { runFlow() }, enabled = jarPath.isNotBlank()) {
                    Text("Run")
                }
            }

            if (logs.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth()
                        .verticalScroll(logScrollState)
                ) {
                    logs.forEach { line -> Text(line) }
                }
            }

            diff?.let { lines ->
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { diff = null },
                    confirmButton = {
                        Button(onClick = { diff = null }) { Text("Close") }
                    },
                    title = { Text("Mismatch") },
                    text = {
                        Column {
                            lines.forEach { line ->
                                val color = when {
                                    line.startsWith("+") -> androidx.compose.ui.graphics.Color.Green
                                    line.startsWith("-") -> androidx.compose.ui.graphics.Color.Red
                                    else -> androidx.compose.ui.graphics.Color.Unspecified
                                }
                                Text(line, color = color)
                            }
                        }
                    }
                )
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "QA Helper") {
        MainScreen()
    }
}

