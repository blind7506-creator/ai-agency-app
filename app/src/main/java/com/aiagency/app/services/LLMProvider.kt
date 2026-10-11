package com.aiagency.app.services

import com.aiagency.app.services.tools.AgencyTool

interface LLMProvider {
    suspend fun generateResponse(prompt: String): Result<String>
    suspend fun generateToolCall(prompt: String, availableTools: List<AgencyTool>): Result<ToolCall>
}

data class ToolCall(val toolName: String, val arguments: Map<String, Any>)

class MockLLMProvider : LLMProvider {
    override suspend fun generateResponse(prompt: String): Result<String> {
        return Result.success("Mock LLM response for prompt: $prompt")
    }

    override suspend fun generateToolCall(prompt: String, availableTools: List<AgencyTool>): Result<ToolCall> {
        // Simulates the LLM deciding to use a tool if the prompt contains certain keywords
        val tool = availableTools.firstOrNull { tool ->
            prompt.contains(tool.name, ignoreCase = true) ||
            (tool is com.aiagency.app.services.tools.WebSearchTool && prompt.contains("search", ignoreCase = true))
        }

        return if (tool != null) {
            Result.success(ToolCall(tool.name, mapOf("query" to "mock search query")))
        } else {
            Result.failure(Exception("No tool call requested"))
        }
    }
}
