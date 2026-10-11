package com.aiagency.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiagency.app.domain.Agent

@Composable
fun DashboardScreen(
    registry: com.aiagency.app.services.AgentRegistry,
    onNavigateToAgents: () -> Unit,
    onNavigateToActivity: () -> Unit
) {
    val agents = registry.listAgents()
    val activeAgents = agents.count { it.isEnabled }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {
        Text(
            text = "Agency Command",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Summary Cards Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard("Active Agents", activeAgents.toString(), Modifier.weight(1f))
            StatCard("Total Tasks", agents.sumOf { it.taskCount }.toString(), Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionButton("Manage Agents", onNavigateToAgents, Modifier.weight(1f))
            ActionButton("Approval Queue", onNavigateToActivity, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Top Specialists",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(agents.take(5)) { agent ->
                AgentSummaryItem(agent)
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.headlineSmall, color = Color(0xFFFF5722), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF2A2A2A))
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color.White, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun AgentSummaryItem(agent: Agent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (agent.isEnabled) Color(0xFF4CAF50) else Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Text(text = agent.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = agent.name, color = Color.White, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Text(text = agent.role, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
