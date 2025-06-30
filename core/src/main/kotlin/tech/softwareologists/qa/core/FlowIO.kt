package tech.softwareologists.qa.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.nio.file.Files
import java.nio.file.Path

/** Utility for reading and writing [Flow] objects as YAML. */
object FlowIO {
    private val mapper: ObjectMapper =
        ObjectMapper(YAMLFactory())
            .registerKotlinModule()
            .findAndRegisterModules()

    fun write(flow: Flow, path: Path) {
        Files.newBufferedWriter(path).use { writer ->
            mapper.writeValue(writer, flow)
        }
    }

    fun read(path: Path): Flow =
        Files.newBufferedReader(path).use { reader ->
            mapper.readValue(reader, Flow::class.java)
        }
}
