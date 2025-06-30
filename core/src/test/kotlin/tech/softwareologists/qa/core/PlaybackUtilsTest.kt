package tech.softwareologists.qa.core

import java.nio.file.Files
import java.time.Instant
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlaybackUtilsTest {
    @Test
    fun toStubMappings_maps_by_method_and_path() {
        val interactions = listOf(
            HttpInteraction("GET", "/a", emptyMap(), "1"),
            HttpInteraction("POST", "/b", emptyMap(), "2")
        )

        val map = interactions.toStubMappings()

        assertEquals(interactions[0], map["GET" to "/a"])
        assertEquals(interactions[1], map["POST" to "/b"])
    }

    @Test
    fun simulateFileEvents_creates_and_deletes_files() {
        val dir = createTempDirectory()
        val target = dir.resolve("sample.txt")
        val events = listOf(
            FileEvent(FileEventType.CREATE, target, Instant.now()),
            FileEvent(FileEventType.DELETE, target, Instant.now())
        )

        simulateFileEvents(events)

        assertFalse(Files.exists(target))
        assertTrue(Files.exists(dir))

        dir.toFile().deleteRecursively()
    }
}
