package tech.softwareologists.qa.agent

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import tech.softwareologists.qa.core.Flow

@RestController
class FlowController(
    private val repository: FlowRepository,
    private val runService: RunService,
    private val mapper: ObjectMapper
) {
    @GetMapping("/flows")
    fun listFlows(): Flux<FlowInfo> = Flux.fromIterable(repository.list())

    @PostMapping("/flows")
    fun importFlow(@RequestBody body: String): Mono<FlowInfo> = Mono.fromCallable {
        val flow = mapper.readValue(body, Flow::class.java)
        repository.save(flow)
    }

    @PostMapping("/flows/{id}/run")
    fun runFlow(@PathVariable id: String): Mono<RunResult> = Mono.fromCallable {
        runService.run(id)
    }

    @GetMapping("/runs/{id}")
    fun runResult(@PathVariable id: String): Mono<RunResult> =
        runService.find(id)?.let { Mono.just(it) }
            ?: Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND))
}
