package tech.softwareologists.qa.http

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.request.httpMethod
import io.ktor.server.request.receiveText
import io.ktor.server.request.uri
import io.ktor.server.response.respondText
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import tech.softwareologists.qa.core.HttpEmulator
import tech.softwareologists.qa.core.HttpInteraction

/**
 * Simple [HttpEmulator] backed by an embedded Ktor server.
 * When started without stubs, it records incoming HTTP calls.
 * When provided with stubbed [HttpInteraction]s, it replays them as responses.
 */
class KtorHttpEmulator(private val stubs: List<HttpInteraction> = emptyList()) : HttpEmulator {
    private val recorded = mutableListOf<HttpInteraction>()
    private var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? = null
    private var baseUrl: String = ""

    override fun start(): String {
        server = embeddedServer(Netty, port = 0) {
            routing {
                route("{...}") {
                    handle {
                        val method = call.request.httpMethod.value
                        val path = call.request.uri
                        val headers = call.request.headers.entries().associate { it.key to it.value.joinToString() }
                        val body = call.receiveText().ifEmpty { null }
                        recorded += HttpInteraction(method, path, headers, body)

                        val stub = stubs.firstOrNull { it.method == method && it.path == path }
                        val responseBody = stub?.body ?: ""
                        call.respondText(text = responseBody, status = HttpStatusCode.OK)
                    }
                }
            }
        }.start(wait = false)
        val port = kotlinx.coroutines.runBlocking { server!!.engine.resolvedConnectors().first().port }
        baseUrl = "http://localhost:$port"
        return baseUrl
    }

    override fun stop() {
        server?.stop(1000, 1000)
        server = null
    }

    override fun interactions(): List<HttpInteraction> = recorded.toList()
}
