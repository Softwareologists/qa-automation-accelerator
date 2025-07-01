package tech.softwareologists.qa.core

import java.nio.file.Files
import java.nio.file.StandardOpenOption

/** Utility functions for flow playback. */

/**
 * Convert recorded [HttpInteraction]s into a stub mapping keyed by HTTP method
 * and path.
 */
fun List<HttpInteraction>.toStubMappings(): Map<Pair<String, String>, HttpInteraction> =
    associateBy { it.method to it.path }

/**
 * Simulate the given [FileEvent]s by creating or deleting files.
 */
fun simulateFileEvents(events: List<FileEvent>) {
    events.forEach { event ->
        when (event.type) {
            FileEventType.CREATE -> {
                Files.createDirectories(event.path.parent)
                if (Files.notExists(event.path)) {
                    Files.createFile(event.path)
                }
            }

            FileEventType.DELETE -> Files.deleteIfExists(event.path)

            FileEventType.MODIFY -> if (Files.exists(event.path)) {
                Files.writeString(event.path, "", StandardOpenOption.APPEND)
            }

            FileEventType.MOVE -> Unit
        }
    }
}

/**
 * Generate a simple unified diff between two lists of [HttpInteraction]s.
 * Lines starting with '-' denote expected interactions, '+' denote actual
 * interactions that differ.
 */
fun diffInteractions(
    expected: List<HttpInteraction>,
    actual: List<HttpInteraction>
): List<String> {
    val lines = mutableListOf<String>()
    val max = maxOf(expected.size, actual.size)
    for (i in 0 until max) {
        val e = expected.getOrNull(i)
        val a = actual.getOrNull(i)
        if (e != a) {
            if (e != null) lines += "- $e"
            if (a != null) lines += "+ $a"
        }
    }
    return lines
}
