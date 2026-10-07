package com.michaelflisar.toolbox.tasks.plan

import com.michaelflisar.toolbox.tasks.execution.TaskResult

interface TaskExecutionListener {

    fun onTaskSkipped(
        id: String,
        title: String,
        parentId: String?,
        timeMs: Long,
    ) {}

    fun onGroupSkipped(
        id: String,
        title: String,
        parentId: String?,
        timeMs: Long,
    ) {}

    fun onGroupStarted(
        id: String,
        title: String,
        parentId: String?,
        startTimeMs: Long,
    )

    fun onTaskStarted(
        id: String,
        title: String,
        parentId: String?,
        startTimeMs: Long,
    )

    fun onTaskStatusChanged(
        taskId: String,
        status: String?,
    )

    fun onTaskMessage(
        taskId: String,
        message: TaskMessage,
    )

    fun onTaskFinished(
        taskId: String,
        result: TaskResult,
        endTimeMs: Long,
    )

    fun onGroupFinished(
        id: String,
        endTimeMs: Long,
    )
}