package tech.softwareologists.qa.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertFailsWith

class StepExecutorTest {
    @Test
    fun loop_until_condition_met() {
        val loopStep = FlowStep(
            id = "poll",
            description = "Poll for status",
            loop = Loop(
                steps = listOf(FlowStep("check", "Check status")),
                until = Condition("check", "$.status", "READY"),
            )
        )
        var attempt = 0
        val executor = StepExecutor { step ->
            if (step.id == "check") {
                attempt++
                mapOf("status" to if (attempt < 3) "WAIT" else "READY")
            } else emptyMap()
        }
        executor.run(listOf(loopStep))
        assertEquals(3, attempt)
        assertEquals("READY", executor.results["check"]?.get("status"))
        assertEquals(3, executor.results["poll"]?.get("iterations"))
    }

    @Test
    fun executes_then_or_else_branch() {
        val steps = listOf(
            FlowStep("setup", "Set flag"),
            FlowStep(
                id = "cond",
                description = "Branch",
                condition = Condition("setup", "$.ready", "yes"),
                then = listOf(FlowStep("then", "then")),
                elseSteps = listOf(FlowStep("else", "else")),
            )
        )
        var first = true
        val executor = StepExecutor { step ->
            if (step.id == "setup") {
                val value = if (first) "no" else "yes"
                first = false
                mapOf("ready" to value)
            } else emptyMap()
        }
        executor.run(steps)
        assertNotNull(executor.results["else"])
        // second run to trigger then branch
        executor.results.clear()
        first = false
        executor.run(steps)
        assertNotNull(executor.results["then"])
    }

    @Test
    fun assert_step_verifies_previous_result() {
        val steps = listOf(
            FlowStep("call", "Call API"),
            FlowStep(
                id = "check",
                description = "verify status",
                assertion = Assertion("call", "$.status", "200"),
            ),
        )

        val executor = StepExecutor { step ->
            if (step.id == "call") mapOf("status" to "200") else emptyMap()
        }

        executor.run(steps)
        assertNotNull(executor.results["check"])

        // failure case
        val failing = listOf(
            FlowStep("call", "Call API"),
            FlowStep(
                id = "bad",
                description = "wrong assert",
                assertion = Assertion("call", "$.status", "404"),
            ),
        )

        assertFailsWith<AssertionError> { executor.run(failing) }
    }
}
