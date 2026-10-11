package com.aiagency.app.data

import androidx.room.*
import com.aiagency.app.domain.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AgentDao {
    @Query("SELECT * FROM agents")
    fun getAllAgents(): Flow<List<AgentEntity>>

    @Upsert
    suspend fun upsertAgent(agent: AgentEntity)

    @Query("SELECT * FROM agents WHERE id = :id")
    suspend fun getAgentById(id: String): AgentEntity?
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY timestamp DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Upsert
    suspend fun upsertTask(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: String): TaskEntity?
}

@Dao
interface AuditDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getLogs(): Flow<List<AuditLogEntity>>

    @Insert
    suspend fun insertLog(log: AuditLogEntity)
}

@Dao
interface ApprovalDao {
    @Query("SELECT * FROM approvals WHERE status = 'PENDING'")
    fun getPendingApprovals(): Flow<List<ApprovalEntity>>

    @Upsert
    suspend fun upsertApproval(approval: ApprovalEntity)

    @Query("SELECT * FROM approvals WHERE taskId = :taskId")
    suspend fun getApprovalByTaskId(taskId: String): ApprovalEntity?
}

@Database(entities = [AgentEntity::class, TaskEntity::class, AuditLogEntity::class, ApprovalEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun agentDao(): AgentDao
    abstract fun taskDao(): TaskDao
    abstract fun auditDao(): AuditDao
    abstract fun approvalDao(): ApprovalDao
}
