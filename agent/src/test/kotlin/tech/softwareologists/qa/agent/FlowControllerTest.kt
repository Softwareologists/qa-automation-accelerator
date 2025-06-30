package tech.softwareologists.qa.agent

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class FlowControllerTest {
    @Autowired
    lateinit var client: WebTestClient

    private val sampleFlow = """
        version: "1"
        appVersion: "test"
        emulator:
          http:
            interactions: []
          file:
            events: []
        steps: []
    """.trimIndent()

    @Test
    fun end_to_end_flow_run() {
        val flowId = client.post().uri("/flows")
            .bodyValue(sampleFlow)
            .exchange()
            .expectStatus().isOk
            .expectBody(FlowInfo::class.java)
            .returnResult().responseBody!!.id

        val run = client.post().uri("/flows/{id}/run", flowId)
            .exchange()
            .expectStatus().isOk
            .expectBody(RunResult::class.java)
            .returnResult().responseBody!!
        assertEquals(true, run.success)

        client.get().uri("/runs/{id}", run.id)
            .exchange()
            .expectStatus().isOk
            .expectBody(RunResult::class.java)
    }
}
