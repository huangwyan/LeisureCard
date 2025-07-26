package com.leisure.card.checkTask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import base.aminer.DebugStatusChecker
import base.aminer.EmulatorDetector
import base.aminer.RootDetectionUtils
import base.aminer.ScreenRecorderDetector
import base.ip.PublicIpFetcher
import com.leisure.card.app.App
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
        val taskList = listOf(
            TaskItem("获取本设备公网 IP", action = {
                val ipTasks = PublicIpFetcher.getPublicIp(forceRefresh = true)
                "ip:${ipTasks.first ?: ""}" // 作为返回值
            }),
            TaskItem("检查 Root", action = {
                delay(1000)
                val root = RootDetectionUtils.isDeviceRooted(App.appContext)
                "Root: $root"
            }),
            TaskItem("检查录屏", action = {
                delay(500)
                val recorder = ScreenRecorderDetector.isScreenRecordingLikely(App.appContext)
                "录屏中: $recorder"
            }),
            TaskItem("检查模拟器", action = {
                delay(1000)
                val isEmulator = EmulatorDetector.isEmulator(App.appContext)
                "模拟器: $isEmulator"
            }),
            TaskItem("检查开发者选项", action = {
                delay(1500)
                val isDebug = DebugStatusChecker.isDebugRelatedEnabled(App.appContext)
                "开发者选项: $isDebug"
            })
        )

        _tasks.value = taskList.map { it.copy(status = TaskStatus.LOADING, result = null) }

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
            val result = task.action() // 获取结果
            updateTask(index, TaskStatus.SUCCESS, result)
        } catch (e: Exception) {
            updateTask(index, TaskStatus.FAIL, e.localizedMessage ?: "未知错误")
        }
    }

    private fun updateTask(index: Int, status: TaskStatus, result: String?) {
        _tasks.update { currentList ->
            currentList.toMutableList().apply {
                this[index] = this[index].copy(status = status, result = result)
            }
        }
    }
}