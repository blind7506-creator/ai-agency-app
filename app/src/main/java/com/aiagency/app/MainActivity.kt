package com.aiagency.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHostController
import com.aiagency.app.ui.screens.PlaceholderScreen
import com.aiagency.app.ui.theme.AIAgencyTheme
import com.aiagency.app.domain.Agent
import com.aiagency.app.services.AgentRegistry
import com.aiagency.app.services.AgencyDefaults
import com.aiagency.app.services.LLMProvider
import com.aiagency.app.services.MockLLMProvider
import com.aiagency.app.services.AgentOrchestrator
import androidx.compose.runtime.remember
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIAgencyTheme {
                MainScreen()
            }
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Chat : Screen("chat", "Chat", Icons.Default.Chat)
    object Agents : Screen("agents", "Agents", Icons.Default.Star)
    object Work : Screen("work", "Work", Icons.Default.Work)
    object Activity : Screen("activity", "Activity", Icons.Default.Settings)
    object More : Screen("more", "More", Icons.Default.Person)
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AppNavHost(navController)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(Screen.Chat, Screen.Agents, Screen.Work, Screen.Activity, Screen.More)
    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = false, // Simplified for foundation
                onClick = { navController.navigate(screen.route) }
            )
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController) {
    // Initialize Core Engine components
    val registry = remember {
        AgentRegistry().apply {
            AgencyDefaults.initialAgents.forEach { registerAgent(it) }
        }
    }
    val llmProvider = remember { MockLLMProvider() }
    val orchestrator = remember { AgentOrchestrator(registry, llmProvider) }

    NavHost(navController = navController, startDestination = "chat") {
        composable("chat") { PlaceholderScreen("Home/Chat") }
        composable("agents") {
            AgentsScreen(registry)
        }
        composable("work") { PlaceholderScreen("Work/Analytics") }
        composable("activity") { PlaceholderScreen("Activity/Logs") }
        composable("more") { PlaceholderScreen("More/Profile") }

        // Other placeholders
        composable("analytics") { PlaceholderScreen("Analytics") }
        composable("business_details") { PlaceholderScreen("Business Details") }
        composable("improvement") { PlaceholderScreen("Business Improvement") }
        composable("create_agent") { PlaceholderScreen("Create Agent") }
        composable("swarm") { PlaceholderScreen("Swarm") }
        composable("profile") { PlaceholderScreen("Profile") }
        composable("command_center") { PlaceholderScreen("Command Center") }
        composable("previous_chats") { PlaceholderScreen("Previous Chats") }
    }
}

@Composable
fun AgentsScreen(registry: AgentRegistry) {
    val agents = registry.listAgents()
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(agents) { agent ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                    ) {
                        Text(text = agent.name, style = MaterialTheme.typography.headlineSmall)
                        Text(
                            text = agent.operationalStatus.name.replace("_", " "),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (agent.operationalStatus == com.aiagency.app.domain.AgentOperationalStatus.CONNECTED_AND_VERIFIED)
                                    androidx.compose.ui.graphics.Color.Green
                                    else androidx.compose.ui.graphics.Color.Gray
                        )
                    }
                    Text(text = agent.role, style = MaterialTheme.typography.bodyMedium)
                    Text(text = agent.description, style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                    ) {
                        Text(text = "Status: ${agent.status}", style = MaterialTheme.typography.labelSmall)
                        Text(text = "Tasks: ${agent.taskCount}", style = MaterialTheme.typography.labelSmall)
                    }
                    Text(text = "Last Activity: ${agent.lastActivity}", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun Spacer(modifier: Modifier) {
    androidx.compose.foundation.layout.Spacer(modifier = modifier)
}
