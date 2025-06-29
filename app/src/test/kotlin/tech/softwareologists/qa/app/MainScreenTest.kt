import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.*
import org.junit.Rule
import org.junit.Test

class MainScreenTest {
    @get:Rule
    val composeTestRule: ComposeContentTestRule = createComposeRule()

    @Test
    fun start_and_stop_button_toggle_state() {
        composeTestRule.setContent {
            tech.softwareologists.qa.app.MainScreen()
        }

        val startButton = composeTestRule.onNodeWithText("Start Recording")
        val stopButton = composeTestRule.onNodeWithText("Stop")

        startButton.assertIsEnabled()
        stopButton.assertIsNotEnabled()

        startButton.performClick()

        startButton.assertIsNotEnabled()
        stopButton.assertIsEnabled()

        stopButton.performClick()

        startButton.assertIsEnabled()
        stopButton.assertIsNotEnabled()
    }
}
