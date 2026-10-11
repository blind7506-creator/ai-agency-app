package com.aiagency.app.services

import com.aiagency.app.domain.*
import com.aiagency.app.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class AgentOrchestrator(
    private val registry: AgentRegistry,
    private val llmProvider: LLMProvider,
    private val taskRepo: TaskRepository,
    private val auditRepo: AuditRepository,
    private val approvalRepo: ApprovalRepository
) {
    private val _tasks = MutableStateFlow<List<AgentTask>>(emptyList())
    val tasks: StateFlow<List<AgentTask>> = _tasks.asStateFlow()

    suspend fun executeTask(input: String, requiredCapability: String): Result<String> {
        val agent = registry.findAgentByCapability(requiredCapability)
            ?: return Result.failure(Exception("No agent found with capability: $requiredCapability"))

        if (!agent.isEnabled) {
            return Result.failure(Exception("Agent ${agent.name} is currently disabled"))
        }

        val task = AgentTask(assignedAgentId = agent.id, input = input)
        updateTaskState(task, TaskStatus.PENDING)

        return try {
            registry.updateAgentStatus(agent.id, AgentStatus.BUSY)

            if (requiresApproval(requiredCapability)) {
                updateTaskState(task, TaskStatus.AWAITING_APPROVAL)
                approvalRepo.saveApproval(ApprovalEntity(
                    taskId = task.id,
                    proposedAction = "Execute capability $requiredCapability with input: $input",
                    status = "PENDING",
                    decisionBy = null,
                    decisionTimestamp = null
                ))
                auditRepo.logEvent("Task ${task.id} paused: Awaiting approval for $requiredCapability")
                return Result.failure(Exception("Action requires explicit approval"))
            }

            val prompt = "You are ${agent.name}, the ${agent.role}. ${agent.description}\nSystem Instructions: ${agent.systemInstructions}\nTask: $input"
            val response = llmProvider.generateResponse(prompt).getOrThrow()

            updateTaskState(task.copy(status = TaskStatus.COMPLETED, output = response))
            registry.updateAgentStatus(agent.id, AgentStatus.IDLE)
            registry.incrementTaskCount(agent.id)
            auditRepo.logEvent("Agent ${agent.name} successfully completed task ${task.id}")

            Result.success(response)
        } catch (e: Exception) {
            updateTaskState(task.copy(status = TaskStatus.FAILED, error = e.message))
            registry.updateAgentStatus(agent.id, AgentStatus.ERROR)
            auditRepo.logEvent("Error executing task ${task.id} with ${agent.name}: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun resumeApprovedTask(taskId: String, approvedBy: String) {
        val task = taskRepo.getTask(taskId) ?: return
        val approval = approvalRepo.getApprovalByTaskId(taskId) ?: return

        approvalRepo.saveApproval(approval.copy(
            status = "APPROVED",
            decisionBy = approvedBy,
            decisionTimestamp = System.currentTimeMillis()
        ))

        auditRepo.logEvent("Task $taskId approved by $approvedBy. Resuming execution...")

        // In a real system, this would re-trigger the specific logic
        // For now, we simulate completion since we use a MockLLM
        updateTaskState(task.copy(status = TaskStatus.COMPLETED, output = "Action approved and executed successfully."))
    }

    private fun requiresApproval(capability: String): Boolean {
        val sensitiveCapabilities = listOf(
            "proposal_drafting", "coding", "financial_reporting",
            "content_creation", "social_engagement", "store_optimization",
            "client_communication"
        )
        return sensitiveCapabilities.contains(capability)
    }

    private suspend fun updateTaskState(task: AgentTask, status: TaskStatus = task.status) {
        val updatedTask = task.copy(status = status)
        taskRepo.saveTask(updatedTask)
        _tasks.value = _tasks.value.filter { it.id != task.id } + updatedTask
    }
}
