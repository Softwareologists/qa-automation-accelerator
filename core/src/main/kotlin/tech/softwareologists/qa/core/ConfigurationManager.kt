package tech.softwareologists.qa.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.nio.file.Files
import java.nio.file.Path
import java.util.Properties
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile

/**
 * Utility for discovering and editing configuration files.
 */
object ConfigurationManager {

    private val yamlMapper: ObjectMapper =
        ObjectMapper(YAMLFactory()).registerKotlinModule().findAndRegisterModules()

    /**
     * Recursively search [root] for `application.properties`, `application.yml`,
     * or `application.yaml` files.
     */
    fun findConfigFiles(root: Path): List<Path> {
        val results = mutableListOf<Path>()
        Files.walk(root).use { stream ->
            stream.filter { path ->
                path.isRegularFile() &&
                    (
                        path.fileName.toString() == "application.properties" ||
                            path.fileName.toString() == "application.yml" ||
                            path.fileName.toString() == "application.yaml"
                        )
            }.forEach { path -> results.add(path) }
        }
        return results
    }

    /**
     * Load all key/value pairs from a properties or YAML [file]. Nested YAML
     * keys are flattened using dot-notation.
     */
    fun readConfig(file: Path): Map<String, String> {
        require(Files.exists(file)) { "File does not exist: $file" }
        return if (file.extension == "properties") {
            val props = Properties()
            Files.newInputStream(file).use { props.load(it) }
            props.entries.associate { it.key.toString() to it.value.toString() }
        } else {
            val root: Map<String, Any?> =
                yamlMapper.readValue(file.toFile(), Map::class.java) as Map<String, Any?>
            val result = mutableMapOf<String, String>()
            flattenYaml(null, root, result)
            result
        }
    }

    /**
     * Write [overrides] to the given [file] in standard properties format.
     */
    fun persistOverrides(overrides: Map<String, String>, file: Path) {
        Files.createDirectories(file.parent)
        val props = Properties().apply { putAll(overrides) }
        Files.newOutputStream(file).use { props.store(it, null) }
    }

    private fun flattenYaml(prefix: String?, value: Any?, target: MutableMap<String, String>) {
        when (value) {
            is Map<*, *> -> value.forEach { (k, v) ->
                val next = if (prefix == null) k.toString() else "$prefix.$k"
                flattenYaml(next, v, target)
            }
            is Iterable<*> -> target[prefix.orEmpty()] = value.joinToString(",") { it.toString() }
            null -> target[prefix.orEmpty()] = ""
            else -> target[prefix.orEmpty()] = value.toString()
        }
    }
}
