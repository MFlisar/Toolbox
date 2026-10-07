package com.michaelflisar.toolbox.tasks.execution

import com.michaelflisar.toolbox.tasks.plan.TaskExecutionListener
import com.michaelflisar.toolbox.tasks.plan.TaskMessage

class TaskExecutionContext internal constructor(
    private val listener: TaskExecutionListener?,
    private val taskId: String,
) {
    fun setStatus(
        text: String?,
    ) {
        listener?.onTaskStatusChanged(
            taskId = taskId,
            status = text,
        )
    }

    fun addInfo(
        text: String,
        type: TaskMessage.Type = TaskMessage.Type.Info,
    ) {
        listener?.onTaskMessage(
            taskId = taskId,
            message = TaskMessage(
                text = text,
                type = type,
            ),
        )
    }
}