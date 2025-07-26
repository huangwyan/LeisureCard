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

    private val _tasks = MutableStateFlow<List<TaskItem>>(emptyList())
    val tasks: StateFlow<List<TaskItem>> = _tasks

    fun startTasks() {
        // 配置任务名称 + 动作
        val taskList = listOf(
            TaskItem("任务 A", action = { delay(1500) }),
            TaskItem("任务 B", action = { delay(2000) }),
            TaskItem("任务 C", action = { delay(1000) }),
            TaskItem("任务 D", action = { delay(2500) })
        )

        // 初始化状态为 LOADING
        _tasks.value = taskList.map { it.copy(status = TaskStatus.LOADING) }

        // 启动并发执行任务
        viewModelScope.launch {
            taskList.forEachIndexed { index, task ->
                launch {
                    runTask(index, task)
                }
            }
        }
    }

    private suspend fun runTask(index: Int, task: TaskItem) {
        try {
            task.action() // 执行自定义任务
            updateTaskStatus(index, TaskStatus.SUCCESS)
        } catch (e: Exception) {
            updateTaskStatus(index, TaskStatus.FAIL)
        }
    }

    private fun updateTaskStatus(index: Int, status: TaskStatus) {
        _tasks.update { currentList ->
            currentList.toMutableList().apply {
                this[index] = this[index].copy(status = status)
            }
        }
    }
}