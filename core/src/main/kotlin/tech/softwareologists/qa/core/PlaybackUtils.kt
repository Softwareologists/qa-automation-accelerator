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
                Files.createFile(event.path)
            }

            FileEventType.DELETE -> Files.deleteIfExists(event.path)

            FileEventType.MODIFY -> if (Files.exists(event.path)) {
                Files.writeString(event.path, "", StandardOpenOption.APPEND)
            }

            FileEventType.MOVE -> Unit
        }
    }
}
