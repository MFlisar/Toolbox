package com.michaelflisar.toolbox.tasks.ui.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.michaelflisar.toolbox.tasks.execution.TaskStatus
import com.michaelflisar.toolbox.tasks.plan.TaskMessage

@Stable
internal sealed class TaskNodeState(
    val startedAt: Long,
    expanded: Boolean,
) {
    var expanded by mutableStateOf(expanded)
        internal set

    var finishedAt by mutableStateOf<Long?>(null)
        internal set

    val isFinished: Boolean
        get() = finishedAt != null

    fun durationMs(nowMs: Long): Long =
        ((finishedAt ?: nowMs) - startedAt).coerceAtLeast(0L)
}

@Stable
internal class TaskGroupState(
    startedAt: Long,
    expanded: Boolean,
    val skipped: Boolean = false,
) : TaskNodeState(startedAt, expanded)

@Stable
internal class TaskState(
    startedAt: Long,
    expanded: Boolean,
) : TaskNodeState(startedAt, expanded) {
    var status: TaskStatus by mutableStateOf(TaskStatus.Running)
        internal set

    var subtitle by mutableStateOf<String?>(null)
        internal set

    private val messageState = mutableStateListOf<TaskMessage>()

    val messages: List<TaskMessage>
        get() = messageState.toList()

    internal fun addMessage(message: TaskMessage) {
        messageState.add(message)
    }
}
