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
data class FlowStep(
    val id: String,
    val description: String
)
