package com.leisure.card.checkTask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 *  Create by hwy on 2025/7/26
 **/
class TaskViewModel : ViewModel() {

    private val _taskStatusMap = MutableStateFlow<Map<String, TaskStatus>>(emptyMap())
    val taskStatusMap: StateFlow<Map<String, TaskStatus>> = _taskStatusMap

    // 模拟任务名称列表（你可以传参）
    private val tasks = listOf("任务 A", "任务 B", "任务 C", "任务 D")

    fun startAllTasks() {
        // 初始化为 LOADING 状态
        _taskStatusMap.value = tasks.associateWith { TaskStatus.LOADING }

        viewModelScope.launch {
            tasks.forEach { taskName ->
                launch {
                    runSingleTask(taskName)
                }
            }
        }
    }

    private suspend fun runSingleTask(taskName: String) {
        val duration = (1000..3000).random().toLong() // 随机延迟
        delay(duration)

        _taskStatusMap.update { currentMap ->
            currentMap + (taskName to TaskStatus.SUCCESS)
        }
    }
}