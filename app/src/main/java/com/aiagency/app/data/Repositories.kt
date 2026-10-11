package com.aiagency.app.data

import com.aiagency.app.domain.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AgentRepository(private val agentDao: AgentDao) {
    fun getAgents(): Flow<List<Agent>> = agentDao.getAllAgents().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun saveAgent(agent: Agent) {
        agentDao.upsertAgent(agent.toEntity())
    }

    suspend fun getAgent(id: String): Agent? = agentDao.getAgentById(id)?.toDomain()

    private fun AgentEntity.toDomain() = Agent(
        id = id, name = name, role = role, description = description,
        status = AgentStatus.valueOf(status),
        operationalStatus = AgentOperationalStatus.valueOf(operationalStatus),
        capabilities = capabilities.split(",").filter { it.isNotBlank() },
        systemInstructions = systemInstructions,
        permissionPolicy = permissionPolicy,
        isEnabled = isEnabled,
        taskCount = taskCount,
        lastActivity = lastActivity
    )

    private fun Agent.toEntity() = AgentEntity(
        id = id, name = name, role = role, description = description,
        status = status.name,
        operationalStatus = operationalStatus.name,
        capabilities = capabilities.joinToString(","),
        systemInstructions = systemInstructions,
        permissionPolicy = permissionPolicy,
        isEnabled = isEnabled,
        taskCount = taskCount,
        lastActivity = lastActivity
    )
}

class TaskRepository(private val taskDao: TaskDao) {
    fun getTasks(): Flow<List<AgentTask>> = taskDao.getAllTasks().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun saveTask(task: AgentTask) {
        taskDao.upsertTask(task.toEntity())
    }

    suspend fun getTask(id: String): AgentTask? = taskDao.getTaskById(id)?.toDomain()

    private fun TaskEntity.toDomain() = AgentTask(
        id = id, assignedAgentId = assignedAgentId, input = input,
        status = TaskStatus.valueOf(status),
        output = output, error = error
    )

    private fun AgentTask.toEntity() = TaskEntity(
        id = id, assignedAgentId = assignedAgentId, input = input,
        status = status.name, output = output, error = error, timestamp = System.currentTimeMillis()
    )
}

class AuditRepository(private val auditDao: AuditDao) {
    fun getAuditLogs(): Flow<List<AuditLogEntity>> = auditDao.getLogs()
    suspend fun logEvent(event: String) = auditDao.insertLog(AuditLogEntity(event = event))
}

class ApprovalRepository(private val approvalDao: ApprovalDao) {
    fun getPendingApprovals(): Flow<List<ApprovalEntity>> = approvalDao.getPendingApprovals()
    suspend fun saveApproval(approval: ApprovalEntity) = approvalDao.upsertApproval(approval)
    suspend fun getApproval(taskId: String) = approvalDao.getApprovalByTaskId(taskId)
}
