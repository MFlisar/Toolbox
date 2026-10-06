package com.michaelflisar.toolbox.tasks

interface TaskExecutionListener {

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

object EmptyTaskExecutionListener : TaskExecutionListener {

    override fun onGroupStarted(
        id: String,
        title: String,
        parentId: String?,
        startTimeMs: Long,
    ) = Unit

    override fun onTaskStarted(
        id: String,
        title: String,
        parentId: String?,
        startTimeMs: Long,
    ) = Unit

    override fun onTaskStatusChanged(
        taskId: String,
        status: String?,
    ) = Unit

    override fun onTaskMessage(
        taskId: String,
        message: TaskMessage,
    ) = Unit

    override fun onTaskFinished(
        taskId: String,
        result: TaskResult,
        endTimeMs: Long,
    ) = Unit

    override fun onGroupFinished(
        id: String,
        endTimeMs: Long,
    ) = Unit
}