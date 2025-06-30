package tech.softwareologists.qa.agent

import org.springframework.stereotype.Service
import tech.softwareologists.qa.core.FlowExecutor
import tech.softwareologists.qa.core.LaunchConfig
import java.nio.file.Path
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class RunResult(val id: String, val success: Boolean, val details: List<String> = emptyList())

@Service
class RunService(
    private val repository: FlowRepository,
    private val executor: FlowExecutor
) {
    private val results = ConcurrentHashMap<String, RunResult>()

    fun run(flowId: String): RunResult {
        val flow = repository.get(flowId) ?: throw IllegalArgumentException("Flow not found: $flowId")
        val runId = UUID.randomUUID().toString()
        val workingDir = java.nio.file.Files.createTempDirectory("run")
        val result = try {
            executor.playback(flow, LaunchConfig(Path.of("/usr/bin/true"), workingDir = workingDir))
            RunResult(runId, true)
        } catch (e: Exception) {
            RunResult(runId, false, listOfNotNull(e.message))
        } finally {
            workingDir.toFile().deleteRecursively()
        }
        results[runId] = result
        return result
    }

    fun find(id: String): RunResult? = results[id]
}
