package tech.softwareologists.qa.core

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion

/** Validates [Flow] objects against the bundled JSON schema. */
object FlowValidator {
    private val jsonMapper: ObjectMapper =
        ObjectMapper().apply {
            registerKotlinModule()
            findAndRegisterModules()
            disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    private val schema = run {
        val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule().findAndRegisterModules()
        val stream = requireNotNull(FlowValidator::class.java.getResourceAsStream("/flow.schema.yaml")) {
            "flow.schema.yaml not found"
        }
        val schemaNode: JsonNode = mapper.readTree(stream)
        JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7).getSchema(schemaNode)
    }

    /** Throws [IllegalArgumentException] if the given [flow] violates the schema. */
    fun validate(flow: Flow) {
        val node = jsonMapper.valueToTree<JsonNode>(flow)
        val errors = schema.validate(node)
        if (errors.isNotEmpty()) {
            val message = errors.joinToString("; ") { it.message }
            throw IllegalArgumentException("Invalid flow: $message")
        }
    }
}
