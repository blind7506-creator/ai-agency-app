package com.aiagency.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aiagency.app.domain.Agent
import com.aiagency.app.domain.AgentOperationalStatus
import com.aiagency.app.services.AgentRegistry
import androidx.compose.runtime.collectAsState


@Composable
fun AgentsScreen(registry: AgentRegistry) {
    val agents by registry.agents.collectAsState()
    val agentList = agents.values.toList()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Agency Specialists",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn {
            items(agentList) { agent ->
                AgentCard(agent, onToggle = { registry.toggleAgentEnabled(agent.id) })
            }
        }
    }
}

@Composable
fun AgentCard(agent: Agent, onToggle: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = agent.name, style = MaterialTheme.typography.titleLarge)
                    Text(text = agent.role, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }
                Switch(
                    checked = agent.isEnabled,
                    onCheckedChange = { onToggle() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = agent.description, style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
            ) {
                StatusBadge(agent.operationalStatus)
                Text(
                    text = "Tasks: ${agent.taskCount}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: AgentOperationalStatus) {
    val color = when (status) {
        AgentOperationalStatus.CONNECTED_AND_VERIFIED -> Color(0xFF4CAF50)
        AgentOperationalStatus.OPERATIONAL_LOCALLY -> Color(0xFF2196F3)
        AgentOperationalStatus.INTEGRATION_REQUIRED -> Color(0xFFFFC107)
        AgentOperationalStatus.DEFINED -> Color.Gray
    }

    Surface(
        color = color.copy(alpha = 0.2f),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(
            text = status.name.replace("_", " "),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
