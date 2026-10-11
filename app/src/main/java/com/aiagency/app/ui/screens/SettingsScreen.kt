package com.aiagency.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {
        Text(
            text = "Agency Settings",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        SettingsSection("AI Configuration") {
            SettingItem("LLM Provider", "MockLLMProvider (Default)")
            SettingItem("API Key", "••••••••••••••••")
            SettingItem("Model", "GPT-4o / Claude 3.5")
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSection("Workspace") {
            SettingItem("Workspace Root", "/data/user/0/com.aiagency.app/files")
            SettingItem("Auto-Save", "Enabled")
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSection("Account") {
            SettingItem("User Role", "Agency Owner")
            SettingItem("Notifications", "Enabled")
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1E1E1E)),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            content()
        }
    }
}

@Composable
fun SettingItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* No-op for now */ }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.White, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
    }
}
