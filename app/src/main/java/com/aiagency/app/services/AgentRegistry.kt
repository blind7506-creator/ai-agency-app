package com.aiagency.app.services

import com.aiagency.app.domain.Agent
import com.aiagency.app.domain.AgentStatus
import java.util.concurrent.ConcurrentHashMap

class AgentRegistry {
    private val agents = ConcurrentHashMap<String, Agent>()

    fun registerAgent(agent: Agent) {
        agents[agent.id] = agent
    }

    fun getAgent(id: String): Agent? = agents[id]

    fun listAgents(): List<Agent> = agents.values.toList()

    fun findAgentByCapability(capability: String): Agent? {
        return agents.values.find { it.capabilities.contains(capability) }
    }

    fun updateAgentStatus(id: String, status: AgentStatus) {
        agents[id]?.let {
            agents[id] = it.copy(status = status)
        }
    }

    fun incrementTaskCount(id: String) {
        agents[id]?.let {
            agents[id] = it.copy(taskCount = it.taskCount + 1)
        }
    }

    fun toggleAgentEnabled(id: String) {
        agents[id]?.let {
            agents[id] = it.copy(isEnabled = !it.isEnabled)
        }
    }
}
