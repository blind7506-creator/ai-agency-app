package com.aiagency.app.domain

import java.util.UUID

enum class AgentStatus {
    IDLE, BUSY, OFFLINE, ERROR
}

data class Agent(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val role: String,
    val description: String,
    val status: AgentStatus = AgentStatus.IDLE,
    val capabilities: List<String> = emptyList()
)

enum class TaskStatus {
    PENDING, IN_PROGRESS, COMPLETED, FAILED, AWAITING_APPROVAL
}

data class AgentTask(
    val id: String = UUID.randomUUID().toString(),
    val assignedAgentId: String,
    val input: String,
    var status: TaskStatus = TaskStatus.PENDING,
    var output: String? = null,
    var error: String? = null
)
