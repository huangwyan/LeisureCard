package com.leisure.card.checkTask

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 *  Create by hwy on 2025/7/26
 **/
@Composable
fun TaskListDialog(
    onDismissRequest: () -> Unit,
) {
    val viewModel: TaskViewModel = viewModel()
    val taskMap by viewModel.taskStatusMap.collectAsState()

    // 启动任务（只启动一次）
    LaunchedEffect(Unit) {
        viewModel.startAllTasks()
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text("任务执行中", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                ) {
                    items(taskMap.entries.toList()) { (taskName, status) ->
                        TaskItem(taskName, status)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val allDone = taskMap.values.all { it == TaskStatus.SUCCESS }
                Button(
                    onClick = onDismissRequest,
                    enabled = allDone,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(if (allDone) "关闭" else "执行中…")
                }
            }
        }
    }
}

@Composable
fun TaskItem(taskName: String, status: TaskStatus) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F8F8), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(taskName, Modifier.weight(1f))
        when (status) {
            TaskStatus.LOADING -> CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )

            TaskStatus.SUCCESS -> Text("✅ 完成", color = Color(0xFF4CAF50))
        }
    }
}