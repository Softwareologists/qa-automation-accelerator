package tech.softwareologists.qa.core

import java.nio.file.Path

private val varRegex = "\\$\\{([^}]+)\\}".toRegex()

internal fun interpolate(text: String, vars: Map<String, String>): String =
    varRegex.replace(text) { match -> vars[match.groupValues[1]] ?: match.value }

fun HttpInteraction.applyVariables(vars: Map<String, String>): HttpInteraction =
    copy(
        path = interpolate(path, vars),
        headers = headers.mapValues { (_, v) -> interpolate(v, vars) },
        body = body?.let { interpolate(it, vars) }
    )

fun FileEvent.applyVariables(vars: Map<String, String>): FileEvent =
    copy(path = Path.of(interpolate(path.toString(), vars)))

fun LaunchConfig.applyVariables(vars: Map<String, String>): LaunchConfig =
    copy(
        executable = Path.of(interpolate(executable.toString(), vars)),
        args = args.map { interpolate(it, vars) },
        environment = environment.mapValues { (_, v) -> interpolate(v, vars) },
        workingDir = workingDir?.let { Path.of(interpolate(it.toString(), vars)) }
    )

fun Flow.applyVariables(overrides: Map<String, String> = emptyMap()): Flow {
    val merged = variables + overrides
    val httpData = emulator.http.interactions.map { it.applyVariables(merged) }
    val fileData = emulator.file.events.map { it.applyVariables(merged) }
    val stepData = steps.map { it.applyVariables(merged) }
    return copy(
        variables = merged,
        emulator = EmulatorData(HttpData(httpData), FileData(fileData)),
        steps = stepData
    )
}

private fun FlowStep.applyVariables(vars: Map<String, String>): FlowStep {
    val appliedThen = then?.map { it.applyVariables(vars) }
    val appliedElse = elseSteps?.map { it.applyVariables(vars) }
    val appliedLoop = loop?.let { loop ->
        Loop(loop.steps.map { it.applyVariables(vars) }, loop.until, loop.count)
    }
    return copy(
        description = interpolate(description, vars),
        then = appliedThen,
        elseSteps = appliedElse,
        loop = appliedLoop
    )
}
