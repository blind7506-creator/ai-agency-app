package com.aiagency.app.services.tools

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

interface AgencyTool {
    val name: String
    val description: String
    val parameters: Map<String, String> // Parameter name to description

    suspend fun execute(args: Map<String, Any>): ToolResult
}

sealed class ToolResult {
    data class Success(val output: String) : ToolResult()
    data class Failure(val error: String) : ToolResult()
}

class WebSearchTool : AgencyTool {
    override val name = "web_search"
    override val description = "Search the internet for real-time information (SIMULATED)"
    override val parameters = mapOf("query" to "The search query string")

    override suspend fun execute(args: Map<String, Any>): ToolResult = withContext(Dispatchers.IO) {
        val query = args["query"] as? String ?: return@withContext ToolResult.Failure("Missing query parameter")
        ToolResult.Success("[SIMULATED Search] Results for '$query': No real network call performed. This is a simulated response for development.")
    }
}

class FileSystemTool(private val workspaceRoot: File) : AgencyTool {
    override val name = "file_manager"
    override val description = "Read, write, or list files in the agency workspace"
    override val parameters = mapOf(
        "action" to "read, write, or list",
        "path" to "The file path relative to workspace",
        "content" to "The content to write (optional)"
    )

    override suspend fun execute(args: Map<String, Any>): ToolResult = withContext(Dispatchers.IO) {
        val action = args["action"] as? String ?: return@withContext ToolResult.Failure("Missing action")
        val relativePath = args["path"] as? String ?: return@withContext ToolResult.Failure("Missing path")

        try {
            val file = File(workspaceRoot, relativePath).canonicalFile

            // Path Traversal and Symlink escape check
            if (!file.absolutePath.startsWith(workspaceRoot.canonicalPath)) {
                return@withContext ToolResult.Failure("Security Error: Access denied. Path is outside the authorized workspace.")
            }

            when (action) {
                "read" -> {
                    if (!file.exists() || file.isDirectory) return@withContext ToolResult.Failure("File not found or is a directory")
                    ToolResult.Success(file.readText())
                }
                "write" -> {
                    val content = args["content"] as? String ?: ""
                    file.writeText(content)
                    ToolResult.Success("Successfully wrote to ${file.name}")
                }
                "list" -> {
                    val files = file.listFiles()?.joinToString(", ") { it.name } ?: "Empty directory"
                    ToolResult.Success("Files in ${file.name}: $files")
                }
                else -> ToolResult.Failure("Unsupported action: $action")
            }
        } catch (e: Exception) {
            ToolResult.Failure("File system error: ${e.message}")
        }
    }
}

class HttpTool : AgencyTool {
    override val name = "http_request"
    override val description = "Perform an HTTP request to an authorized external API"
    override val parameters = mapOf(
        "url" to "The target URL",
        "method" to "GET, POST, PUT, DELETE",
        "body" to "The JSON body (optional)"
    )

    private val allowedDomains = listOf("api.openai.com", "api.anthropic.com", "tavily.com")

    override suspend fun execute(args: Map<String, Any>): ToolResult = withContext(Dispatchers.IO) {
        val urlString = args["url"] as? String ?: return@withContext ToolResult.Failure("Missing URL")
        val method = (args["method"] as? String ?: "GET").uppercase()

        try {
            val url = URL(urlString)
            val host = url.host.lowercase()

            // 1. Exact domain or strict subdomain matching
            val isAllowed = allowedDomains.any { domain ->
                host == domain || host.endsWith("." + domain)
            }
            if (!isAllowed) {
                return@withContext ToolResult.Failure("Security Error: Domain $host is not in the authorized allow-list.")
            }

            // 2. Block loopback, private, link-local, and IPv6 local addresses
            if (host == "localhost" || host == "127.0.0.1" || host == "::1" ||
                host.startsWith("192.168.") || host.startsWith("10.") ||
                host.startsWith("172.16.") || host.startsWith("169.254.") ||
                host.startsWith("fe80:")) {
                return@withContext ToolResult.Failure("Security Error: Local or private network access is forbidden.")
            }

            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = method
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.instanceFollowRedirects = false // Disable automatic redirects

            if (method != "GET") {
                val body = args["body"] as? String ?: ""
                connection.doOutput = true
                connection.outputStream.write(body.toByteArray())
            }

            val responseCode = connection.responseCode

            // 3. Handle Redirects Manually
            if (responseCode in 301..308) {
                val location = connection.getHeaderField("Location") ?: ""
                return@withContext ToolResult.Failure("Redirects are disabled for security. Target was: $location")
            }

            if (responseCode in 200..299) {
                // 4. Enforce response-size limit while streaming
                val responseText = readLimitedStream(connection.inputStream, 10000)
                ToolResult.Success("HTTP $responseCode: $responseText")
            } else {
                ToolResult.Failure("HTTP Error: $responseCode")
            }
        } catch (e: Exception) {
            ToolResult.Failure("Network error: ${e.message}")
        }
    }

    private fun readLimitedStream(inputStream: InputStream, limit: Int): String {
        val reader = inputStream.bufferedReader()
        val sb = StringBuilder()
        var charsRead = 0
        while (charsRead < limit) {
            val char = reader.read()
            if (char == -1) break
            sb.append(char.toChar())
            charsRead++
        }
        return if (charsRead >= limit) sb.toString() + "... [truncated]" else sb.toString()
    }
}
