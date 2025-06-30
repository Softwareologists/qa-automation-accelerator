package tech.softwareologists.qa.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.nio.file.Files
import java.nio.file.Path

/** Utility for reading and writing [Flow] objects as YAML. */
object FlowIO {
    private val mapper: ObjectMapper =
        ObjectMapper(YAMLFactory()).apply {
            registerKotlinModule()
            registerModule(JavaTimeModule())
            disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }

    fun write(flow: Flow, path: Path) {
        Files.newBufferedWriter(path).use { writer ->
            mapper.writeValue(writer, flow)
        }
    }

    fun read(path: Path): Flow =
        Files.newBufferedReader(path).use { reader ->
            val flow = mapper.readValue(reader, Flow::class.java)
            FlowValidator.validate(flow)
            flow
        }
}
