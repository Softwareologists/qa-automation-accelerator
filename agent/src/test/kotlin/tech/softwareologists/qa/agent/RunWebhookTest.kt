package tech.softwareologists.qa.agent

import com.sun.net.httpserver.HttpServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RunWebhookTest {
    @Autowired
    lateinit var client: WebTestClient

    companion object {
        private val received = CompletableFuture<String>()
        private val server: HttpServer = HttpServer.create(InetSocketAddress(0), 0).apply {
            createContext("/hook") { exchange ->
                val body = String(exchange.requestBody.readAllBytes(), StandardCharsets.UTF_8)
                received.complete(body)
                exchange.sendResponseHeaders(200, 0)
                exchange.responseBody.close()
            }
            start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("qa.webhook-url") { "http://localhost:${server.address.port}/hook" }
        }
    }

    @AfterAll
    fun tearDown() {
        server.stop(0)
    }

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
    fun posts_result_json() {
        val flowId = client.post().uri("/flows")
            .bodyValue(sampleFlow)
            .exchange()
            .expectStatus().isOk
            .expectBody(FlowInfo::class.java)
            .returnResult().responseBody!!.id

        client.post().uri("/flows/{id}/run", flowId)
            .exchange()
            .expectStatus().isOk

        val body = received.get(5, TimeUnit.SECONDS)
        assertTrue(body.contains("\"success\":true"))
    }
}
