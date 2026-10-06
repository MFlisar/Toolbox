package com.michaelflisar.toolbox.tasks

import kotlin.uuid.Uuid

class TaskContext internal constructor(
    private val reporter: TaskReporter,
    private val taskId: String,
) {
    suspend fun runSubTask(
        title: String,
        block: suspend (TaskContext) -> TaskResult,
    ) {
        val task = beginSubTask(title)
        try {
            val result = block(task)
            when (result) {
                is TaskResult.Success -> task.endWithSuccess()
                is TaskResult.Warning -> task.endWithWarning()
                is TaskResult.Error -> task.endWithError(result.message)
            }
        } catch (e: Exception) {
            task.endWithError(e.message ?: "Unknown Error", e)
        }
    }

    fun reportStep(
        text: String,
        type: MessageType = MessageType.Info,
    ) {
        reporter.updateTask(taskId) {
            copy(messages = messages + Message(text, type))
        }
    }

    internal fun beginSubTask(
        title: String,
    ): TaskContext {

        val child = TaskNode(
            id = Uuid.random().toString(),
            title = title
        )

        reporter.updateTask(taskId) {
            copy(
                children = children + child,
                expanded = expanded || reporter.config.autoExpandNewTasks
            )
        }

        return TaskContext(
            reporter = reporter,
            taskId = child.id
        )
    }

    internal fun endWithSuccess(
        collapseIfLeaf: Boolean = true,
    ) {
        reporter.updateTask(taskId) {
            copy(
                status = Status.Success,
                expanded = if (collapseIfLeaf && children.isEmpty()) {
                    false
                } else {
                    expanded
                }
            )
        }
    }

    internal fun endWithWarning(
        collapseIfLeaf: Boolean = true,
    ) {
        reporter.updateTask(taskId) {
            copy(
                status = Status.Warning,
                expanded = if (collapseIfLeaf && children.isEmpty()) {
                    false
                } else {
                    expanded
                }
            )
        }
    }

    internal fun endWithError(
        message: String,
        exception: Exception? = null,
        collapseIfLeaf: Boolean = false,
    ) {
        reporter.updateTask(taskId) {
            copy(
                status = Status.Error(message, exception),
                expanded = if (collapseIfLeaf && children.isEmpty()) {
                    false
                } else {
                    expanded
                },
                messages = messages + Message(
                    text = message,
                    type = MessageType.Error
                )
            )
        }
    }

    fun setSubtitle(
        text: String?,
    ) {
        reporter.updateTask(taskId) {
            copy(
                subtitle = text
            )
        }
    }
}