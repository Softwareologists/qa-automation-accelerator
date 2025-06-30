package tech.softwareologists.qa.core

import kotlin.test.Test
import kotlin.test.assertEquals

class VariableUtilsTest {
    @Test
    fun applyVariables_replaces_placeholders() {
        val flow = Flow(
            version = "1",
            appVersion = "test",
            variables = mapOf("id" to "42"),
            emulator = EmulatorData(
                http = HttpData(listOf(HttpInteraction("GET", "/users/${'$'}{id}"))),
                file = FileData()
            ),
            steps = emptyList()
        )

        val resolved = flow.applyVariables()
        assertEquals("/users/42", resolved.emulator.http.interactions.first().path)
    }
}
