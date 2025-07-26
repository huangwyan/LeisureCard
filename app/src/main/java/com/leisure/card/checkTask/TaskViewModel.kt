package com.leisure.card.checkTask

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import base.aminer.DebugStatusChecker
import base.aminer.EmulatorDetector
import base.aminer.RootDetectionUtils
import base.aminer.ScreenRecorderDetector
import base.ip.PublicIpFetcher
import com.google.gson.Gson
import com.imyyq.mvvm.utils.LogUtil
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
        // 配置任务名称 + 动作
        val taskList = listOf(
            TaskItem("获取本设备公网ip", action = {
                val ipTasks = PublicIpFetcher.getPublicIp(forceRefresh = true)
                Log.d("task", "ip json ${Gson().toJson(ipTasks)}")
            }),
            TaskItem("检查Root", action = {
                val root = RootDetectionUtils.isDeviceRooted(App.appContext)
                Log.d("task", "检查Root $root")
            }),
            TaskItem("检查录屏", action = {
                val recorder = ScreenRecorderDetector.isScreenRecordingLikely(App.appContext)
                Log.d("task", "检查录屏 $recorder")
            }),
            TaskItem(
                "检查是否是模拟器",
                action = { val isEmulator = EmulatorDetector.isEmulator(App.appContext)
                    Log.d("task", "检查模拟器 $isEmulator")}),
            TaskItem(
                "检查是否开启开发者选项",
                action = { val isDebug = DebugStatusChecker.isDebugRelatedEnabled(App.appContext)
                    Log.d("task", "检查开发者 $isDebug")})
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