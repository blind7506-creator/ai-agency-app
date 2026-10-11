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
import com.aiagency.app.ui.screens.DashboardScreen
import com.aiagency.app.ui.screens.ChatScreen
import com.aiagency.app.ui.screens.SettingsScreen
import com.aiagency.app.ui.screens.AgentsScreen
import com.aiagency.app.ui.screens.ApprovalQueueScreen
import com.aiagency.app.domain.Agent
import com.aiagency.app.services.AgentRegistry
import com.aiagency.app.services.AgencyDefaults
import com.aiagency.app.services.LLMProvider
import com.aiagency.app.services.MockLLMProvider
import com.aiagency.app.services.AgentOrchestrator
import com.aiagency.app.data.*
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
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    // Initialize Real Room Database
    val db = remember {
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "ai_agency_db"
        ).build()
    }

    // Repositories using real DAOs
    val agentRepo = remember { AgentRepository(db.agentDao()) }
    val taskRepo = remember { TaskRepository(db.taskDao()) }
    val auditRepo = remember { AuditRepository(db.auditDao()) }
    val approvalRepo = remember { ApprovalRepository(db.approvalDao()) }

    val registry = remember {
        AgentRegistry(onAgentUpdated = { agent ->
            scope.launch { agentRepo.saveAgent(agent) }
        })
    }

    // Agent Seeding Logic: Load from Room, then seed defaults if empty
    LaunchedEffect(Unit) {
        val persistedAgents = agentRepo.getAgents().first()
        if (persistedAgents.isEmpty()) {
            AgencyDefaults.initialAgents.forEach { agent ->
                registry.registerAgent(agent)
                agentRepo.saveAgent(agent)
            }
        } else {
            persistedAgents.forEach { registry.registerAgent(it) }
        }
    }

    val llmProvider = remember { MockLLMProvider() }
    val orchestrator = remember {
        AgentOrchestrator(
            registry,
            llmProvider,
            taskRepo,
            auditRepo,
            approvalRepo,
            context.filesDir
        )
    }

    NavHost(navController = navController, startDestination = "chat") {
        composable("chat") { ChatScreen(orchestrator) }
        composable("agents") {
            AgentsScreen(registry)
        }
        composable("work") { DashboardScreen(registry, { navController.navigate("agents") }, { navController.navigate("activity") }) }
        composable("activity") {
            ApprovalQueueScreen(approvalRepo, orchestrator)
        }
        composable("more") { SettingsScreen() }

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

}

