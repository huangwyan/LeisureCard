package com.leisure.card.checkTask

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 *  Create by hwy on 2025/7/26
 **/
@Composable
fun TaskListDialog(
    onDismissRequest: () -> Unit,
    viewModel: TaskViewModel = viewModel()
) {
    val tasks by viewModel.tasks.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startTasks()
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                val allDone = tasks.all { it.status != TaskStatus.LOADING }
                Text(
                    if (allDone) "检测完成" else "设备检测中",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))

                // 不使用 LazyColumn，改为 Column 一次性展示所有任务
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tasks.forEach { task ->
                        TaskItemView(task)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismissRequest,
                    enabled = allDone,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(if (allDone) "关闭" else "检测执行中…")
                }
            }
        }
    }
}

@Composable
fun TaskItemView(task: TaskItem) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(task.title, Modifier.weight(1f))

            when (task.status) {
                TaskStatus.LOADING -> CircularProgressIndicator(
                    Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )

                TaskStatus.SUCCESS -> Text("✅", color = Color(0xFF4CAF50))
                TaskStatus.FAIL -> Text("❌", color = Color.Red)
            }
        }

        if (!task.result.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = task.result,
                fontSize = 12.sp,
                color = if (task.status == TaskStatus.FAIL) Color.Red else Color.DarkGray
            )
        }
    }
}