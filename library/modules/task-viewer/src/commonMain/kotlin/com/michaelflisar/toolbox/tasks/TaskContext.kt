package com.michaelflisar.toolbox.tasks

import kotlin.uuid.Uuid

class TaskContext internal constructor(
    private val reporter: TaskReporter,
    private val taskId: String,
) {
    fun throwWarning(message: String? = null): Nothing =
        throw TaskAbort.Warning(message)

    fun throwError(exception: Exception): Nothing =
        throw TaskAbort.Error(exception)

    fun throwError(message: String): Nothing =
        throw TaskAbort.Error(Exception(message))

    fun setStatus(
        text: String?,
    ) {
        reporter.updateTask(taskId) {
            copy(
                subtitle = text
            )
        }
    }

    fun addInfo(
        text: String,
        type: TaskMessage.Type = TaskMessage.Type.Info,
    ) {
        reporter.updateTask(taskId) {
            copy(messages = messages + TaskMessage(text, type))
        }
    }

    suspend fun runSubTask(
        title: String,
        block: suspend TaskContext.() -> Unit,
    ) {
        val task = beginSubTask(title)
        task.runTaskInternal(block = block)
    }

    private fun beginSubTask(
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

    private fun endWithSuccess(
        collapseIfLeaf: Boolean = true,
    ) {
        reporter.updateTask(taskId) {
            copy(
                status = TaskStatus.Success,
                expanded = if (collapseIfLeaf && children.isEmpty()) {
                    false
                } else {
                    expanded
                }
            )
        }
    }

    private fun endWithState(
        status: TaskStatus.Finished,
        collapseIfLeaf: Boolean = true,
    ) {
        reporter.updateTask(taskId) {
            copy(
                status = status,
                expanded = when {
                    status is TaskStatus.Error -> reporter.config.autoExpandError
                    status is TaskStatus.Warning -> reporter.config.autoExpandWarning
                    collapseIfLeaf && children.isEmpty() -> false
                    else -> expanded
                },
                subtitle = when (status) {
                    is TaskStatus.Error -> status.exception.message ?: "Task failed with error"
                    is TaskStatus.Warning -> status.message ?: "Task finished with warning"
                    else -> subtitle
                }
            )
        }
    }

    internal suspend fun runTaskInternal(
        block: suspend TaskContext.() -> Unit,
    ) {
        try {
            block()
            endWithSuccess()
        } catch (e: TaskAbort.Warning) {
            endWithState(TaskStatus.Warning(e.message))
        } catch (e: TaskAbort.Error) {
            endWithState(TaskStatus.Error(e.exception))
        } catch (e: Exception) {
            endWithState(TaskStatus.Error(e))
        }
    }
}