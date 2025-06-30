package tech.softwareologists.qa.agent

import org.springframework.stereotype.Component
import tech.softwareologists.qa.core.Flow
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class FlowInfo(val id: String, val appVersion: String)

@Component
class FlowRepository {
    private val flows = ConcurrentHashMap<String, Flow>()

    fun list(): List<FlowInfo> = flows.map { FlowInfo(it.key, it.value.appVersion) }

    fun save(flow: Flow): FlowInfo {
        val id = UUID.randomUUID().toString()
        flows[id] = flow
        return FlowInfo(id, flow.appVersion)
    }

    fun get(id: String): Flow? = flows[id]
}
