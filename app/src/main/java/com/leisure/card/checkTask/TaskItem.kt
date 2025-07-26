package com.leisure.card.checkTask

/**
 *  Create by hwy on 2025/7/26
 **/
data class TaskItem(
    val name: String,
    val action: suspend () -> Unit,
    val status: TaskStatus = TaskStatus.LOADING
)

enum class TaskStatus {
    LOADING, SUCCESS, FAIL // 可扩展失败状态
}