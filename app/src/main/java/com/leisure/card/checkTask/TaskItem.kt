package com.leisure.card.checkTask

/**
 *  Create by hwy on 2025/7/26
 **/
data class TaskItem(
    val name: String,
    val action: suspend () -> String, // 返回执行结果
    val status: TaskStatus = TaskStatus.LOADING,
    val result: String? = null        // 新增字段：执行结果
)

enum class TaskStatus {
    LOADING, SUCCESS, FAIL
}