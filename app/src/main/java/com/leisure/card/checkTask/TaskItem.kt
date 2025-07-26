package com.leisure.card.checkTask

/**
 *  Create by hwy on 2025/7/26
 **/
data class TaskItem(
    val title: String,
    val action: suspend () -> String,
    val judgeStatus: ((String) -> TaskStatus)? = null, // 可选的状态判断函数
    val status: TaskStatus = TaskStatus.LOADING,
    val result: String? = null
)

enum class TaskStatus {
    LOADING, SUCCESS, FAIL
}