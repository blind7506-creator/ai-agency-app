package com.aiagency.app.services

import com.aiagency.app.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AgentOrchestrator(
    private val registry: AgentRegistry,
    private val llmProvider: LLMProvider
) {
    private val _tasks = MutableStateFlow<List<AgentTask>>(emptyList())
    val tasks: StateFlow<List<AgentTask>> = _tasks.asStateFlow()

    suspend fun executeTask(input: String, requiredCapability: String): Result<String> {
        val agent = registry.findAgentByCapability(requiredCapability)
            ?: return Result.failure(Exception("No agent found with capability: $requiredCapability"))

        val task = AgentTask(assignedAgentId = agent.id, input = input)
        updateTask(task)

        return try {
            registry.updateAgentStatus(agent.id, AgentStatus.BUSY)

            // Construct a prompt for the specialist
            val prompt = "You are ${agent.name}, the ${agent.role}. ${agent.description}\nTask: $input"
            val response = llmProvider.generateResponse(prompt).getOrThrow()

            task.status = TaskStatus.COMPLETED
            task.output = response

            registry.updateAgentStatus(agent.id, AgentStatus.IDLE)
            updateTask(task)
            Result.success(response)
        } catch (e: Exception) {
            task.status = TaskStatus.FAILED
            task.error = e.message
            registry.updateAgentStatus(agent.id, AgentStatus.ERROR)
            updateTask(task)
            Result.failure(e)
        }
    }

    private fun updateTask(task: AgentTask) {
        _tasks.value = _tasks.value + task
    }

    fun requestApproval(taskId: String, action: String, onApproved: () -> Unit, onDenied: () -> Unit) {
        // In a real app, this would trigger a UI notification
        // For the engine, we represent the state transition
        val task = _tasks.value.find { it.id == taskId }
        task?.let {
            it.status = TaskStatus.AWAITING_APPROVAL
            updateTask(it)
        }
    }
}
