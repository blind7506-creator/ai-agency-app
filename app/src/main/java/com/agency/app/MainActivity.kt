package com.agency.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgencyTheme {
                MainDashboard()
            }
        }
    }
}

@Composable
fun AgencyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF4A90E2),
            surface = Color(0xFFE0E5EC),
            background = Color(0xFFE0E5EC)
        ),
        content = content
    )
}

@Composable
fun MainDashboard() {
    var totalProfit by remember { mutableStateOf("$4,782.32") }
    var agentCount by remember { mutableStateOf(8) }
    var activeAgents by remember { mutableStateOf(6) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0E5EC))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("AI Agency", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D3748))
                Text("Autonomous Control Center", fontSize = 14.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Total Profit", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Text(totalProfit, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4A90E2))
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Agents", agentCount.toString(), Modifier.weight(1f))
            StatCard("Active", activeAgents.toString(), Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Tasks", "12", Modifier.weight(1f))
            StatCard("Businesses", "3", Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Workforce", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(listOf("Researcher", "Copywriter", "Developer", "Analyst")) { agent ->
                AgentRow(agent)
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFE0E5EC))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
        }
    }
}

@Composable
fun AgentRow(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(Color(0xFFE0E5EC))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(Color.Green))
            Spacer(modifier = Modifier.width(12.dp))
            Text(name, fontWeight = FontWeight.Medium)
        }
        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))) {
            Text("Connect", fontSize = 10.sp, color = Color.White)
        }
    }
}
