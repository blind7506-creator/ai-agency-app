package com.aiagency.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aiagency.app.data.ApprovalEntity
import com.aiagency.app.services.AgentOrchestrator
import com.aiagency.app.data.ApprovalRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@Composable
fun ApprovalQueueScreen(
    approvalRepo: ApprovalRepository,
    orchestrator: AgentOrchestrator
) {
    var pendingApprovals by remember { mutableStateOf(emptyList<ApprovalEntity>()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        while (true) {
            pendingApprovals = approvalRepo.getPendingApprovals().first()
            kotlinx.coroutines.delay(2000)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Pending Approvals",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (pendingApprovals.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No pending approvals")
            }
        } else {
            LazyColumn {
                items(pendingApprovals) { approval ->
                    ApprovalCard(
                        approval = approval,
                        onApprove = {
                            scope.launch {
                                orchestrator.resumeApprovedTask(approval.taskId, "User")
                            }
                        },
                        onReject = {
                            scope.launch {
                                orchestrator.rejectTask(approval.taskId, "User")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ApprovalCard(
    approval: ApprovalEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Task ID: ${approval.taskId}", style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = approval.proposedAction, style = MaterialTheme.typography.bodyMedium)

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End
            ) {
                TextButton(onClick = onReject) {
                    Text("Reject")
                }
                Button(onClick = onApprove) {
                    Text("Approve")
                }
            }
        }
    }
}
