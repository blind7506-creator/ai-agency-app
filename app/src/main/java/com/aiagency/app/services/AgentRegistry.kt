package com.aiagency.app.services

import com.aiagency.app.domain.Agent
import com.aiagency.app.domain.AgentStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AgentRegistry(private val onAgentUpdated: (Agent) -> Unit) {
    private val _agents = MutableStateFlow<Map<String, Agent>>(emptyMap())
    val agents: StateFlow<Map<String, Agent>> = _agents.asStateFlow()

    fun registerAgent(agent: Agent) {
        val current = _agents.value.toMutableMap()
        current[agent.id] = agent
        _agents.value = current
    }

    fun getAgent(id: String): Agent? = _agents.value[id]

    fun listAgents(): List<Agent> = _agents.value.values.toList()

    fun findAgentByCapability(capability: String): Agent? {
        return _agents.value.values.find { it.capabilities.contains(capability) }
    }

    fun updateAgentStatus(id: String, status: AgentStatus) {
        val current = _agents.value.toMutableMap()
        current[id]?.let {
            val updated = it.copy(status = status)
            current[id] = updated
            _agents.value = current
            onAgentUpdated(updated)
        }
    }

    fun incrementTaskCount(id: String) {
        val current = _agents.value.toMutableMap()
        current[id]?.let {
            val updated = it.copy(taskCount = it.taskCount + 1)
            current[id] = updated
            _agents.value = current
            onAgentUpdated(updated)
        }
    }

    fun toggleAgentEnabled(id: String) {
        val current = _agents.value.toMutableMap()
        current[id]?.let {
            val updated = it.copy(isEnabled = !it.isEnabled)
            current[id] = updated
            _agents.value = current
            onAgentUpdated(updated)
        }
    }
}
