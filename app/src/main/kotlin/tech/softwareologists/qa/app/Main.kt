package tech.softwareologists.qa.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import javax.swing.JFileChooser

@Composable
fun MainScreen() {
    var jarPath by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }

    fun chooseFile() {
        val chooser = JFileChooser()
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            jarPath = chooser.selectedFile.absolutePath
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

            Spacer(Modifier.height(16.dp))
            Text("Configuration panels go here", modifier = Modifier.weight(1f))

            Row {
                Button(onClick = { isRecording = true /* TODO invoke CLI */ }, enabled = !isRecording) {
                    Text("Start Recording")
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { isRecording = false /* TODO invoke CLI */ }, enabled = isRecording) {
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
