package com.michaelflisar.toolbox.tasks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import kotlin.uuid.Uuid

class TaskReporter(
    internal val config: Config = Config(),
) {

    data class Config(

        /**
         * Beim Erstellen automatisch öffnen.
         */
        val autoExpandNewTasks: Boolean = true,

        /**
         * Nur ein Pfad gleichzeitig offen.
         */
        val expandSinglePathOnly: Boolean = false,
    )

    val hasRunningTasks by derivedStateOf {
        tasks.anyRunning()
    }

    val isIdle: Boolean
        get() = tasks.isEmpty()

    val hasFinishedTasks: Boolean
        get() = tasks.isNotEmpty() && !hasRunningTasks

    val tasks = mutableStateListOf<TaskNode>()

    fun reset() {
        tasks.clear()
    }

    suspend fun runTask(
        title: String,
        clearBeforeStart: Boolean = true,
        block: suspend (TaskContext) -> TaskResult,
    ) {
        if (clearBeforeStart) {
            reset()
        }
        val task = beginTask(title)
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

    private fun beginTask(
        title: String,
    ): TaskContext {

        val task = TaskNode(
            id = Uuid.random().toString(),
            title = title
        )

        tasks += task

        return TaskContext(
            reporter = this,
            taskId = task.id
        )
    }

    internal fun updateTask(
        id: String,
        update: TaskNode.() -> TaskNode,
    ) {
        replaceRecursive(
            tasks = tasks,
            id = id,
            update = update
        )
    }

    fun toggleExpanded(taskId: String) {
        if (!config.expandSinglePathOnly) {
            updateTask(taskId) { copy(expanded = !expanded) }
            return
        }
        val task = findTask(taskId) ?: return
        if (task.expanded) {
            updateTask(taskId) { copy(expanded = false) }
        } else {
            expandSinglePath(taskId)
        }
    }

    private fun findTask(
        taskId: String,
    ): TaskNode? {

        fun find(
            tasks: List<TaskNode>,
        ): TaskNode? {

            tasks.forEach { task ->
                if (task.id == taskId) {
                    return task
                }
                find(task.children)?.let { return it }
            }
            return null
        }
        return find(tasks)
    }

    private fun expandSinglePath(
        taskId: String,
    ) {

        val path = mutableSetOf<String>()

        fun findPath(
            tasks: List<TaskNode>,
            targetId: String,
        ): Boolean {

            tasks.forEach { task ->

                if (task.id == targetId) {
                    path += task.id
                    return true
                }

                if (findPath(task.children, targetId)) {
                    path += task.id
                    return true
                }
            }

            return false
        }

        findPath(tasks, taskId)

        fun updateTree(
            tasks: MutableList<TaskNode>,
        ) {

            tasks.indices.forEach { index ->

                val task = tasks[index]

                val children = task.children.toMutableList()

                updateTree(children)

                tasks[index] = task.copy(
                    expanded = task.id in path,
                    children = children
                )
            }
        }

        updateTree(tasks)
    }

    private fun replaceRecursive(
        tasks: MutableList<TaskNode>,
        id: String,
        update: TaskNode.() -> TaskNode,
    ): Boolean {

        tasks.indices.forEach { index ->

            val task = tasks[index]

            if (task.id == id) {
                tasks[index] = task.update()
                return true
            }

            val children = task.children.toMutableList()

            if (
                replaceRecursive(
                    tasks = children,
                    id = id,
                    update = update
                )
            ) {
                tasks[index] = task.copy(
                    children = children
                )
                return true
            }
        }

        return false
    }
}

@Composable
fun rememberTaskReporter(
    autoExpandNewTasks: Boolean = true,
    expandSinglePathOnly: Boolean = true,
): TaskReporter {
    return remember {
        TaskReporter(
            config = TaskReporter.Config(
                autoExpandNewTasks = autoExpandNewTasks,
                expandSinglePathOnly = expandSinglePathOnly
            )
        )
    }
}

private fun List<TaskNode>.anyRunning(): Boolean {
    return any { task ->
        task.status == Status.Running ||
                task.children.anyRunning()
    }
}