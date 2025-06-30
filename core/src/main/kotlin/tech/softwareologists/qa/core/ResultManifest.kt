package tech.softwareologists.qa.core

/** Summary of a flow run. */
data class ResultManifest(
    val success: Boolean,
    val timings: Map<String, Long> = emptyMap(),
    val mismatches: List<String> = emptyList()
)
