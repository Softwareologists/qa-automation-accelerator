package tech.softwareologists.qa.http

import io.ktor.client.HttpClient
import io.ktor.client.call.bodyAsText
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import tech.softwareologists.qa.core.HttpInteraction

class KtorHttpEmulatorTest {
    private var emulator: KtorHttpEmulator? = null

    @AfterTest
    fun tearDown() {
        emulator?.stop()
    }

    @Test
    fun should_record_http_interaction() = kotlinx.coroutines.runBlocking {
        emulator = KtorHttpEmulator()
        val baseUrl = emulator!!.start()
        val client = HttpClient(CIO)

        client.get("$baseUrl/hello")
        emulator!!.stop()

        val interactions = emulator!!.interactions()
        assertEquals(1, interactions.size)
        val recorded = interactions.first()
        assertEquals("GET", recorded.method)
        assertEquals("/hello", recorded.path)
    }

    @Test
    fun should_replay_stubbed_response() = kotlinx.coroutines.runBlocking {
        val stub = HttpInteraction("GET", "/hello", emptyMap(), "world")
        emulator = KtorHttpEmulator(listOf(stub))
        val baseUrl = emulator!!.start()
        val client = HttpClient(CIO)

        val response = client.get("$baseUrl/hello")
        val body = response.bodyAsText()
        assertEquals("world", body)
    }
}
