package tech.softwareologists.qa.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
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
import tech.softwareologists.qa.app.BranchCreateCommand
import javax.swing.JFileChooser

@Composable
fun MainScreen(recorder: FlowRecorder = PluginFlowRecorder()) {
    var jarPath by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    var flowPath by remember { mutableStateOf("") }
    var flow by remember { mutableStateOf<Flow?>(null) }

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
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "QA Helper") {
        MainScreen()
    }
}

