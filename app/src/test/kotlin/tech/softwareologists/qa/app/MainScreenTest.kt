import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.*
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import tech.softwareologists.qa.app.FlowRecorder

class MainScreenTest {
    @get:Rule
    val composeTestRule: ComposeContentTestRule = createComposeRule()

    private class FakeRecorder : FlowRecorder {
        var startCalled = 0
        var stopCalled = 0
        override fun start(jarPath: String) { startCalled++ }
        override fun stop(output: java.nio.file.Path) { stopCalled++ }
    }

    @Test
    fun start_and_stop_button_toggle_state() {
        val recorder = FakeRecorder()
        composeTestRule.setContent {
            tech.softwareologists.qa.app.MainScreen(recorder)
        }

        val startButton = composeTestRule.onNodeWithText("Start Recording")
        val stopButton = composeTestRule.onNodeWithText("Stop")

        startButton.assertIsEnabled()
        stopButton.assertIsNotEnabled()

        startButton.performClick()

        startButton.assertIsNotEnabled()
        stopButton.assertIsEnabled()

        stopButton.performClick()

        assertEquals(1, recorder.startCalled)
        assertEquals(1, recorder.stopCalled)

        startButton.assertIsEnabled()
        stopButton.assertIsNotEnabled()
    }

    @Test
    fun stop_writes_flow_file() {
        val dir = kotlin.io.path.createTempDirectory()
        val output = dir.resolve("flow.yaml")
        val recorder = object : FlowRecorder {
            override fun start(jarPath: String) {}
            override fun stop(output: java.nio.file.Path) {
                val flow = tech.softwareologists.qa.core.Flow(
                    version = "1",
                    appVersion = "test",
                    emulator = tech.softwareologists.qa.core.EmulatorData(
                        tech.softwareologists.qa.core.HttpData(),
                        tech.softwareologists.qa.core.FileData()
                    ),
                    steps = emptyList()
                )
                tech.softwareologists.qa.core.FlowIO.write(flow, output)
            }
        }

        composeTestRule.setContent {
            tech.softwareologists.qa.app.MainScreen(recorder)
        }

        // enter flow path
        composeTestRule.onNodeWithText("Flow YAML")
            .performTextInput(output.toString())

        composeTestRule.onNodeWithText("Start Recording").performClick()
        composeTestRule.onNodeWithText("Stop").performClick()

        kotlin.test.assertTrue(java.nio.file.Files.exists(output))

        dir.toFile().deleteRecursively()
    }

    @Test
    fun run_displays_process_logs() {
        composeTestRule.setContent {
            tech.softwareologists.qa.app.MainScreen()
        }

        composeTestRule.onNodeWithText("Application JAR/DLL")
            .performTextInput("/tmp/app.jar")

        composeTestRule.onNodeWithText("Run").performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("test-log").fetchSemanticsNodes().isNotEmpty()
        }
    }
}
