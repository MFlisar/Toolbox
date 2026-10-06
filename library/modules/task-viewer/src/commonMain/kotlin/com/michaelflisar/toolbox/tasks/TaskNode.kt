package com.michaelflisar.toolbox.tasks

import kotlin.time.Clock

internal sealed interface TaskNode {
    val id: String
    val title: String
    val expanded: Boolean

    val totalEntries: Int
        get() = when (this) {
            is Task -> 1 + messages.size
            is TaskGroup -> 1 + children.sumOf { it.totalEntries }
        }
}

internal data class TaskGroup(
    override val id: String,
    override val title: String,
    val children: List<TaskNode> = emptyList(),
    override val expanded: Boolean = true,
) : TaskNode {

    val durationMs: Long
        get() = children.maxOfOrNull {
            when (it) {
                is Task -> it.durationMs
                is TaskGroup -> it.durationMs
            }
        } ?: 0L
}

internal data class Task(
    override val id: String,
    override val title: String,
    val subtitle: String? = null,
    val status: TaskStatus = TaskStatus.Running,
    val messages: List<TaskMessage> = emptyList(),
    val startedAt: Long = Clock.System.now().toEpochMilliseconds(),
    val finishedAt: Long? = null,
    override val expanded: Boolean = true,
) : TaskNode {

    val isFinished: Boolean
        get() = status != TaskStatus.Running

    val isFailed: Boolean
        get() = status is TaskStatus.Error

    val durationMs: Long
        get() = (finishedAt ?: Clock.System.now().toEpochMilliseconds()) - startedAt
}

