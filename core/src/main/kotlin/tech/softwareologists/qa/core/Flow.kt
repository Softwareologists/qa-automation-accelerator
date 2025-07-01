package tech.softwareologists.qa.core

/**
 * High-level representation of a recorded QA flow.
 */
data class Flow(
    val version: String,
    val appVersion: String,
    val variables: Map<String, String> = emptyMap(),
    val emulator: EmulatorData,
    val steps: List<FlowStep>
)

/** Container for recorded emulator data. */
data class EmulatorData(
    val http: HttpData,
    val file: FileData
)

/** Recorded HTTP interactions. */
data class HttpData(
    val interactions: List<HttpInteraction> = emptyList()
)

/** Recorded file system events. */
data class FileData(
    val events: List<FileEvent> = emptyList()
)

/** User-defined step within a flow. */
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
data class FlowStep(
    val id: String,
    val description: String,
    @com.fasterxml.jackson.annotation.JsonProperty("assert")
    val assertion: Assertion? = null,
    @com.fasterxml.jackson.annotation.JsonProperty("if")
    val condition: Condition? = null,
    val then: List<FlowStep>? = null,
    @com.fasterxml.jackson.annotation.JsonProperty("else")
    val elseSteps: List<FlowStep>? = null,
    val loop: Loop? = null,
)

data class Assertion(
    val stepId: String,
    val path: String,
    val equals: String,
)

data class Condition(
    val stepId: String,
    val path: String,
    val equals: String,
)

@com.fasterxml.jackson.annotation.JsonInclude(
    com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
)
data class Loop(
    val steps: List<FlowStep>,
    val until: Condition? = null,
    val count: Int? = null,
)
