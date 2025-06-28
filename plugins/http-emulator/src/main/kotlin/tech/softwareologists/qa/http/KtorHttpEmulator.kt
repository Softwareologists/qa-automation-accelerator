package tech.softwareologists.qa.http

import tech.softwareologists.qa.core.HttpEmulator
import tech.softwareologists.qa.core.HttpInteraction

class KtorHttpEmulator : HttpEmulator {
    override fun start(): String = "http://localhost"

    override fun stop() {
        // no-op
    }

    override fun interactions(): List<HttpInteraction> = emptyList()
}
