package tech.softwareologists.qa.core

import java.nio.file.Files
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/** Utility for exporting run results in JUnit-XML and HTML formats. */
object ReportExporter {
    fun writeReports(dir: Path, flowName: String, success: Boolean, details: List<String> = emptyList()) {
        writeJUnitXml(dir.resolve("junit.xml"), flowName, success, details)
        writeHtml(dir.resolve("summary.html"), flowName, success, details)
    }

    private fun writeJUnitXml(target: Path, flowName: String, success: Boolean, details: List<String>) {
        val docFactory = DocumentBuilderFactory.newInstance()
        val docBuilder = docFactory.newDocumentBuilder()
        val doc = docBuilder.newDocument()

        val suite = doc.createElement("testsuite")
        suite.setAttribute("name", flowName)
        suite.setAttribute("tests", "1")
        suite.setAttribute("failures", if (success) "0" else "1")
        doc.appendChild(suite)

        val case = doc.createElement("testcase")
        case.setAttribute("classname", flowName)
        case.setAttribute("name", "playback")
        suite.appendChild(case)

        if (!success) {
            val failure = doc.createElement("failure")
            failure.setAttribute("message", details.joinToString(";"))
            failure.textContent = details.joinToString("\n")
            case.appendChild(failure)
        }

        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        Files.newOutputStream(target).use { out ->
            transformer.transform(DOMSource(doc), StreamResult(out))
        }
    }

    private fun writeHtml(target: Path, flowName: String, success: Boolean, details: List<String>) {
        val status = if (success) "Passed" else "Failed"
        val body = buildString {
            append("<html><head><title>")
            append("Flow Report")
            append("</title></head><body>")
            append("<h1>")
            append(flowName)
            append("</h1>")
            append("<p>Status: <strong>")
            append(status)
            append("</strong></p>")
            if (!success) {
                append("<pre>")
                append(details.joinToString("\n"))
                append("</pre>")
            }
            append("</body></html>")
        }
        Files.writeString(target, body)
    }
}
