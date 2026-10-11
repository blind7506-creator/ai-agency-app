package com.aiagency.app.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agents")
data class AgentEntity(
    @PrimaryKey val id: String,
    val name: String,
    val role: String,
    val description: String,
    val status: String, // mapped from AgentStatus
    val operationalStatus: String, // mapped from AgentOperationalStatus
    val capabilities: String, // stored as comma-separated string
    val systemInstructions: String,
    val permissionPolicy: String,
    val isEnabled: Boolean,
    val taskCount: Int,
    val lastActivity: String
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val assignedAgentId: String,
    val input: String,
    val status: String, // mapped from TaskStatus
    val output: String?,
    val error: String?,
    val timestamp: Long
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val event: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "approvals")
data class ApprovalEntity(
    @PrimaryKey val taskId: String,
    val proposedAction: String,
    val status: String, // PENDING, APPROVED, REJECTED
    val decisionBy: String?,
    val decisionTimestamp: Long?
)
