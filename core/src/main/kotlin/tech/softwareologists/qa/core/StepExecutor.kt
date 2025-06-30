package tech.softwareologists.qa.core

/** Simple executor that handles conditional and looped steps. */
class StepExecutor(private val action: (FlowStep) -> Map<String, Any?> = { emptyMap<String, Any?>() }) {
    val results: MutableMap<String, Map<String, Any?>> = mutableMapOf()

    fun run(steps: List<FlowStep>) {
        steps.forEach { execute(it) }
    }

    private fun execute(step: FlowStep) {
        step.loop?.let { runLoop(step.id, it); return }

        step.assertion?.let {
            checkAssertion(it)
            results[step.id] = mapOf("assert" to true)
            return
        }

        val conditionMet = step.condition?.let { evaluate(it) } ?: true
        val branch = if (conditionMet) step.then ?: emptyList() else step.elseSteps ?: emptyList()
        if (branch.isNotEmpty()) {
            branch.forEach { execute(it) }
            results[step.id] = mapOf("executed" to true)
        } else {
            val result = action(step)
            results[step.id] = result
        }
    }

    private fun runLoop(id: String, loop: Loop) {
        var iterations = 0
        while (true) {
            loop.steps.forEach { execute(it) }
            iterations++
            val untilOk = loop.until?.let { evaluate(it) } ?: false
            if ((loop.count != null && iterations >= loop.count) || untilOk) break
        }
        results[id] = mapOf("iterations" to iterations)
    }

    private fun evaluate(cond: Condition): Boolean {
        val result = results[cond.stepId] ?: return false
        val path = cond.path.removePrefix("$.")
        val value = result[path]?.toString() ?: return false
        return value == cond.equals
    }

    private fun value(stepId: String, path: String): String? {
        val result = results[stepId] ?: return null
        val key = path.removePrefix("$.")
        return result[key]?.toString()
    }

    private fun checkAssertion(assertion: Assertion) {
        val actual = value(assertion.stepId, assertion.path)
        if (actual != assertion.equals) {
            throw AssertionError(
                "Assertion failed for step ${assertion.stepId} at ${assertion.path}: expected ${assertion.equals}, got $actual"
            )
        }
    }
}
