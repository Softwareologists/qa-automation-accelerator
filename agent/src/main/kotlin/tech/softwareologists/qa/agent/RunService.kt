package tech.softwareologists.qa.agent

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import tech.softwareologists.qa.core.FlowExecutor
import tech.softwareologists.qa.core.LaunchConfig
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class RunResult(val id: String, val success: Boolean, val details: List<String> = emptyList())

@Service
class RunService(
    private val repository: FlowRepository,
    private val executor: FlowExecutor,
    private val webClient: WebClient,
    @Value("\${qa.webhook-url:}") private val webhookUrl: String?
) {
    private val results = ConcurrentHashMap<String, RunResult>()

    fun run(flowId: String): RunResult {
        val flow = repository.get(flowId) ?: throw IllegalArgumentException("Flow not found: $flowId")
        val runId = UUID.randomUUID().toString()
        val workingDir = java.nio.file.Files.createTempDirectory("run")
        val reportDir = Files.createTempDirectory("report")
        val result = try {
            executor.playback(flow, LaunchConfig(Path.of("/usr/bin/true"), workingDir = workingDir))
            RunResult(runId, true)
        } catch (e: Exception) {
            RunResult(runId, false, listOfNotNull(e.message))
        }
        executor.collectEvidence(flowId, reportDir, result.success, result.details)
        workingDir.toFile().deleteRecursively()

        if (!webhookUrl.isNullOrBlank()) {
            try {
                val tsDir = Files.list(reportDir.resolve(flowId)).findFirst().get()
                val payload = Files.readString(tsDir.resolve("result.json"))
                webClient.post()
                    .uri(webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Void::class.java)
                    .block()
            } catch (ex: Exception) {
                println("Webhook POST failed: ${ex.message}")
            }
        }
        reportDir.toFile().deleteRecursively()

        results[runId] = result
        return result
    }

    fun find(id: String): RunResult? = results[id]
}
