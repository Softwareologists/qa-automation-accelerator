package tech.softwareologists.qa.core

import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertTrue
import java.nio.file.Files

class ReportExporterTest {
    @Test
    fun html_contains_colored_diff() {
        val dir = createTempDirectory()
        ReportExporter.writeReports(
            dir,
            "sample",
            success = false,
            details = listOf("- expected", "+ actual")
        )
        val html = Files.readString(dir.resolve("summary.html"))
        assertTrue(html.contains("<span style=\"color:red\">- expected"))
        assertTrue(html.contains("<span style=\"color:green\">+ actual"))
        dir.toFile().deleteRecursively()
    }
}
