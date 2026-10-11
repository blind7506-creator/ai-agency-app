package com.aiagency.app.services

interface LLMProvider {
    suspend fun generateResponse(prompt: String): Result<String>
}

class MockLLMProvider : LLMProvider {
    override suspend fun generateResponse(prompt: String): Result<String> {
        return Result.success("Mock LLM response for prompt: $prompt")
    }
}
