package com.aiagency.app.domain

import java.util.UUID

enum class AgentStatus {
    IDLE, BUSY, OFFLINE, ERROR
}

enum class AgentOperationalStatus {
    DEFINED,
    OPERATIONAL_LOCALLY,
    INTEGRATION_REQUIRED,
    CONNECTED_AND_VERIFIED
}

data class Agent(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val role: String,
    val description: String,
    val status: AgentStatus = AgentStatus.IDLE,
    val operationalStatus: AgentOperationalStatus = AgentOperationalStatus.DEFINED,
    val capabilities: List<String> = emptyList(),
    val systemInstructions: String = "",
    val permissionPolicy: String = "Standard",
    var isEnabled: Boolean = true,
    var taskCount: Int = 0,
    var lastActivity: String = "Never"
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
    var error: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
