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
    NavHost(navController = navController, startDestination = "chat") {
        composable("chat") { PlaceholderScreen("Home/Chat") }
        composable("agents") { PlaceholderScreen("Agents") }
        composable("work") { PlaceholderScreen("Work/Analytics") }
        composable("activity") { PlaceholderScreen("Activity/Logs") }
        composable("more") { PlaceholderScreen("More/Profile") }

        // Other placeholders can be added here as specific routes
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
