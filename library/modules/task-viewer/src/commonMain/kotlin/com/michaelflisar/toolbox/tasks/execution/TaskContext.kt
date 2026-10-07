package com.michaelflisar.toolbox.tasks.execution

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import com.michaelflisar.toolbox.tasks.plan.TaskExecutionListener
import com.michaelflisar.toolbox.tasks.plan.TaskMessage
import com.michaelflisar.toolbox.tasks.plan.TaskMessageColors
import com.michaelflisar.toolbox.tasks.plan.TaskMessageType
import com.michaelflisar.toolbox.tasks.plan.TaskPlanDsl

@TaskPlanDsl
class TaskExecutionContext internal constructor(
    private val colors: TaskMessageColors,
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

    fun addWarning(
        text: String,
    ) {
        addInfo(text = text, type = TaskMessageType.Warning)
    }

    fun addError(
        text: String,
    ) {
        addInfo(text = text, type = TaskMessageType.Error)
    }

    fun addCustom(
        block: TaskMessageColors.() -> AnnotatedString,
    ) {
        listener?.onTaskMessage(
            taskId = taskId,
            message = TaskMessage.Rich(
                text = with(colors) { block() }
            )
        )
    }

    fun addInfo(
        text: String,
        type: TaskMessageType = TaskMessageType.Info,
    ) {
        listener?.onTaskMessage(
            taskId = taskId,
            message = TaskMessage.Text(
                text = text,
                type = type,
            ),
        )
    }
}