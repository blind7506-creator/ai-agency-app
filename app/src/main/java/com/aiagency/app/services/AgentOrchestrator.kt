package com.aiagency.app.services

import com.aiagency.app.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class AgentOrchestrator(
    private val registry: AgentRegistry,
    private val llmProvider: LLMProvider
) {
    private val _tasks = MutableStateFlow<List<AgentTask>>(emptyList())
    val tasks: StateFlow<List<AgentTask>> = _tasks.asStateFlow()

    private val _auditLog = MutableStateFlow<List<String>>(emptyList())
    val auditLog: StateFlow<List<String>> = _auditLog.asStateFlow()

    suspend fun executeTask(input: String, requiredCapability: String): Result<String> {
        val agent = registry.findAgentByCapability(requiredCapability)
            ?: return Result.failure(Exception("No agent found with capability: $requiredCapability"))

        if (!agent.isEnabled) {
            return Result.failure(Exception("Agent ${agent.name} is currently disabled"))
        }

        val task = AgentTask(assignedAgentId = agent.id, input = input)
        updateTask(task)

        return try {
            registry.updateAgentStatus(agent.id, AgentStatus.BUSY)

            // Check if action requires approval
            if (requiresApproval(requiredCapability)) {
                task.status = TaskStatus.AWAITING_APPROVAL
                updateTask(task)
                logAction("Task ${task.id} awaiting approval for capability: $requiredCapability")
                return Result.failure(Exception("Action requires explicit approval"))
            }

            val prompt = "You are ${agent.name}, the ${agent.role}. ${agent.description}\nSystem Instructions: ${agent.systemInstructions}\nTask: $input"
            val response = llmProvider.generateResponse(prompt).getOrThrow()

            task.status = TaskStatus.COMPLETED
            task.output = response

            registry.updateAgentStatus(agent.id, AgentStatus.IDLE)
            registry.incrementTaskCount(agent.id)
            updateTask(task)
            logAction("Agent ${agent.name} successfully completed task ${task.id}")

            Result.success(response)
        } catch (e: Exception) {
            task.status = TaskStatus.FAILED
            task.error = e.message
            registry.updateAgentStatus(agent.id, AgentStatus.ERROR)
            updateTask(task)
            logAction("Error executing task ${task.id} with ${agent.name}: ${e.message}")
            Result.failure(e)
        }
    }

    fun approveTask(taskId: String, approvedBy: String) {
        val task = _tasks.value.find { it.id == taskId }
        task?.let {
            it.status = TaskStatus.IN_PROGRESS
            updateTask(it)
            logAction("Task ${it.id} approved by $approvedBy")
        }
    }

    private fun requiresApproval(capability: String): Boolean {
        val sensitiveCapabilities = listOf(
            "proposal_drafting", "coding", "financial_reporting",
            "content_creation", "social_engagement", "store_optimization"
        )
        return sensitiveCapabilities.contains(capability)
    }

    private fun updateTask(task: AgentTask) {
        _tasks.value = _tasks.value.filter { it.id != task.id } + task
    }

    private fun logAction(message: String) {
        _auditLog.value = _auditLog.value + "[${System.currentTimeMillis()}] $message"
    }
}
