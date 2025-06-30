package tech.softwareologists.qa.core

import java.time.Instant

/** Minimal HAR data model for recording HTTP exchanges. */
data class Har(val log: HarLog)

data class HarLog(
    val version: String = "1.2",
    val creator: HarCreator = HarCreator("QA Helper", "1.0"),
    val entries: List<HarEntry>
)

data class HarCreator(val name: String, val version: String)

data class HarEntry(
    val startedDateTime: String,
    val time: Long = 0,
    val request: HarRequest,
    val response: HarResponse = HarResponse(),
    val timings: HarTimings = HarTimings()
)

data class HarRequest(
    val method: String,
    val url: String,
    val httpVersion: String = "HTTP/1.1",
    val headers: List<HarHeader> = emptyList(),
    val postData: HarPostData? = null,
    val headersSize: Int = -1,
    val bodySize: Int = -1
)

data class HarHeader(val name: String, val value: String)

data class HarPostData(val mimeType: String = "text/plain", val text: String)

data class HarResponse(
    val status: Int = 200,
    val statusText: String = "OK",
    val httpVersion: String = "HTTP/1.1",
    val headers: List<HarHeader> = emptyList(),
    val content: HarContent = HarContent(),
    val redirectURL: String = "",
    val headersSize: Int = -1,
    val bodySize: Int = -1
)

data class HarContent(val size: Int = 0, val mimeType: String = "", val text: String? = null)

data class HarTimings(val send: Long = 0, val wait: Long = 0, val receive: Long = 0)

/** Convert recorded HTTP interactions to a simple HAR object. */
fun List<HttpInteraction>.toHar(baseUrl: String = ""): Har {
    val entries = map { interaction ->
        val url = baseUrl.trimEnd('/') + interaction.path
        HarEntry(
            startedDateTime = Instant.now().toString(),
            request = HarRequest(
                method = interaction.method,
                url = url,
                headers = interaction.headers.map { HarHeader(it.key, it.value) },
                postData = interaction.body?.let { HarPostData(text = it) }
            )
        )
    }
    return Har(HarLog(entries = entries))
}
