package tech.softwareologists.qa.agent

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tech.softwareologists.qa.core.FlowExecutor
import tech.softwareologists.qa.fileio.NioFileIoEmulator
import tech.softwareologists.qa.http.KtorHttpEmulator

@Configuration
@OpenAPIDefinition(info = Info(title = "QA Helper API", version = "v1"))
class AgentConfig {
    @Bean
    fun flowExecutor(): FlowExecutor = FlowExecutor(
        KtorHttpEmulator(),
        NioFileIoEmulator(),
        object : tech.softwareologists.qa.core.LauncherPlugin {
            override fun supports(config: tech.softwareologists.qa.core.LaunchConfig) = true
            override fun launch(config: tech.softwareologists.qa.core.LaunchConfig): Process {
                return ProcessBuilder(config.executable.toString()).start()
            }
        }
    )

    @Bean
    fun yamlMapper(): ObjectMapper =
        ObjectMapper(YAMLFactory()).registerKotlinModule().findAndRegisterModules()
}
