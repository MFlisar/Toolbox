package com.michaelflisar.toolbox.tasks

import kotlin.time.Clock

data class TaskNode(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val status: TaskStatus = TaskStatus.Running,
    val children: List<TaskNode> = emptyList(),
    val messages: List<TaskMessage> = emptyList(),
    val expanded: Boolean = true,
    val startedAt: Long = Clock.System.now().toEpochMilliseconds(),
    val finishedAt: Long? = null,
) {
    val isFinished: Boolean
        get() = status != TaskStatus.Running

    val isFailed: Boolean
        get() = status is TaskStatus.Error

    val hasChildren: Boolean
        get() = children.isNotEmpty()

    val hasMessages: Boolean
        get() = messages.isNotEmpty()

    val totalEntries: Int
        get() = 1 + messages.size + children.sumOf { it.totalEntries }

    val durationMs: Long
        get() = (finishedAt ?: Clock.System.now().toEpochMilliseconds()) - startedAt
}

