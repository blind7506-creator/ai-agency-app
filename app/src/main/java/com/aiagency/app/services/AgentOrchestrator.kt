package com.aiagency.app.services

import com.aiagency.app.domain.*
import com.aiagency.app.data.*
import com.aiagency.app.services.tools.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class AgentOrchestrator(
    private val registry: AgentRegistry,
    private val llmProvider: LLMProvider,
    private val taskRepo: TaskRepository,
    private val auditRepo: AuditRepository,
    private val approvalRepo: ApprovalRepository,
    private val workspaceDir: File
) {
    private val _tasks = MutableStateFlow<List<AgentTask>>(emptyList())
    val tasks: StateFlow<List<AgentTask>> = _tasks.asStateFlow()

    private val availableTools: List<AgencyTool> by lazy {
        listOf(
            WebSearchTool(),
            FileSystemTool(workspaceDir),
            HttpTool()
        )
    }

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

            val result = processWithToolLoop(agent, input)

            updateTaskState(task.copy(status = TaskStatus.COMPLETED, output = result))
            registry.updateAgentStatus(agent.id, AgentStatus.IDLE)
            registry.incrementTaskCount(agent.id)
            auditRepo.logEvent("Agent ${agent.name} successfully completed task ${task.id}")

            Result.success(result)
        } catch (e: Exception) {
            updateTaskState(task.copy(status = TaskStatus.FAILED, error = e.message))
            registry.updateAgentStatus(agent.id, AgentStatus.ERROR)
            auditRepo.logEvent("Error executing task ${task.id} with ${agent.name}: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun resumeApprovedTask(taskId: String, approvedBy: String) {
        val task = taskRepo.getTask(taskId) ?: return
        val agent = registry.getAgent(task.assignedAgentId) ?: return
        val approval = approvalRepo.getApprovalByTaskId(taskId) ?: return

        approvalRepo.saveApproval(approval.copy(
            status = "APPROVED",
            decisionBy = approvedBy,
            decisionTimestamp = System.currentTimeMillis()
        ))

        auditRepo.logEvent("Task $taskId approved by $approvedBy. Resuming execution...")

        try {
            registry.updateAgentStatus(agent.id, AgentStatus.BUSY)
            val result = processWithToolLoop(agent, task.input)
            updateTaskState(task.copy(status = TaskStatus.COMPLETED, output = result))
            registry.updateAgentStatus(agent.id, AgentStatus.IDLE)
            registry.incrementTaskCount(agent.id)
            auditRepo.logEvent("Resumed task $taskId successfully completed.")
        } catch (e: Exception) {
            updateTaskState(task.copy(status = TaskStatus.FAILED, error = e.message))
            registry.updateAgentStatus(agent.id, AgentStatus.ERROR)
            auditRepo.logEvent("Error resuming task $taskId: ${e.message}")
        }
    }

    private suspend fun processWithToolLoop(agent: Agent, input: String): String {
        var currentInput = input
        var iterations = 0
        val maxIterations = 5
        val seenCalls = mutableSetOf<String>()

        while (iterations < maxIterations) {
            val prompt = "You are ${agent.name}, the ${agent.role}. ${agent.description}\nSystem Instructions: ${agent.systemInstructions}\nTask: $currentInput"

            val toolCallResult = llmProvider.generateToolCall(prompt, availableTools)

            if (toolCallResult.isSuccess) {
                val toolCall = toolCallResult.getOrThrow()

                // Prevent infinite loops of identical calls
                val callSignature = "${toolCall.toolName}:${toolCall.arguments}"
                if (seenCalls.contains(callSignature)) {
                    return "Error: Agent entered a loop by making the same tool call repeatedly. Last tool: ${toolCall.toolName}"
                }
                seenCalls.add(callSignature)

                val tool = availableTools.find { it.name == toolCall.toolName }
                    ?: throw Exception("Tool ${toolCall.toolName} not found")

                auditRepo.logEvent("Agent ${agent.name} using tool ${tool.name}")
                val toolResult = tool.execute(toolCall.arguments)

                val resultText = when (toolResult) {
                    is ToolResult.Success -> toolResult.output
                    is ToolResult.Failure -> "Tool Error: ${toolResult.error}"
                }

                currentInput = "Tool ${tool.name} returned: $resultText\nOriginal Task: $input\nContinue and provide final answer."
                iterations++
            } else {
                // No more tool calls needed, get final response
                return llmProvider.generateResponse(prompt).getOrThrow()
            }
        }

        return "Maximum tool iterations reached. Last known state: $currentInput"
    }

    suspend fun rejectTask(taskId: String, rejectedBy: String) {
        val task = taskRepo.getTask(taskId) ?: return
        val approval = approvalRepo.getApprovalByTaskId(taskId) ?: return

        approvalRepo.saveApproval(approval.copy(
            status = "REJECTED",
            decisionBy = rejectedBy,
            decisionTimestamp = System.currentTimeMillis()
        ))

        updateTaskState(task.copy(status = TaskStatus.FAILED, error = "Rejected by $rejectedBy"))
        auditRepo.logEvent("Task $taskId rejected by $rejectedBy")
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
