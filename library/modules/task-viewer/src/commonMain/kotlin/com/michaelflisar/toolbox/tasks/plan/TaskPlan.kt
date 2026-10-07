package com.michaelflisar.toolbox.tasks.plan

import com.michaelflisar.toolbox.tasks.execution.TaskExecutionConfig
import com.michaelflisar.toolbox.tasks.execution.TaskExecutionContext
import com.michaelflisar.toolbox.tasks.execution.TaskResult
import com.michaelflisar.toolbox.tasks.execution.TaskStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlin.time.Clock
import kotlin.uuid.Uuid

sealed interface TaskPlanNode {
    val id: String
    val title: String
}

sealed interface TaskPlanExecutable : TaskPlanNode {
    /** Liefert true, wenn dieser Knoten oder ein Kind einen Fehler hatte. */
    suspend fun execute(
        config: TaskExecutionConfig,
        listener: TaskExecutionListener?,
        parentId: String?,
    ): Boolean
}

data class TaskPlan(
    val children: List<TaskPlanExecutable>,
) {
    suspend fun execute(
        config: TaskExecutionConfig,
        listener: TaskExecutionListener? = null
    ) {
        children.executeChildren(config, listener, null)
    }
}

data class TaskPlanSummary(
    val running: Int = 0,
    val success: Int = 0,
    val warnings: Int = 0,
    val errors: Int = 0,
    val skipped: Int = 0,
) {
    internal fun toStatus(): TaskStatus {
        return when {
            running > 0 ->
                TaskStatus.Running

            errors > 0 ->
                TaskStatus.Error(Exception())

            warnings > 0 ->
                TaskStatus.Warning("")

            skipped > 0 ->
                TaskStatus.Cancelled

            else ->
                TaskStatus.Success
        }
    }
}

internal data class TaskPlanGroup(
    override val id: String,
    override val title: String,
    val children: List<TaskPlanExecutable>,
) : TaskPlanExecutable {

    override suspend fun execute(
        config: TaskExecutionConfig,
        listener: TaskExecutionListener?,
        parentId: String?,
    ): Boolean {
        currentCoroutineContext().ensureActive()
        val start = Clock.System.now().toEpochMilliseconds()
        listener?.onGroupStarted(
            id = id,
            title = title,
            parentId = parentId,
            startTimeMs = start
        )
        val failed = children.executeChildren(config, listener, id)
        val end = Clock.System.now().toEpochMilliseconds()
        listener?.onGroupFinished(
            id = id,
            endTimeMs = end
        )
        return failed
    }
}

data class TaskPlanTask(
    override val id: String,
    override val title: String,
    val block: suspend TaskExecutionContext.() -> TaskResult,
) : TaskPlanExecutable {

    override suspend fun execute(
        config: TaskExecutionConfig,
        listener: TaskExecutionListener?,
        parentId: String?,
    ): Boolean {
        currentCoroutineContext().ensureActive()
        val start = Clock.System.now().toEpochMilliseconds()
        listener?.onTaskStarted(
            id = id,
            title = title,
            parentId = parentId,
            startTimeMs = start
        )

        val context = TaskExecutionContext(
            listener = listener,
            taskId = id,
        )

        val result = try {
            context.block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            TaskResult.Error(e)
        }
        currentCoroutineContext().ensureActive()

        val end = Clock.System.now().toEpochMilliseconds()
        listener?.onTaskFinished(
            taskId = id,
            result = result,
            endTimeMs = end
        )
        return result is TaskResult.Error
    }
}

private suspend fun List<TaskPlanExecutable>.executeChildren(
    config: TaskExecutionConfig,
    listener: TaskExecutionListener?,
    parentId: String?,
): Boolean {
    var failed = false
    var stopped = false
    for (child in this) {
        currentCoroutineContext().ensureActive()
        if (stopped) {
            child.skip(listener, parentId, Clock.System.now().toEpochMilliseconds())
        } else {
            val childFailed = child.execute(config, listener, parentId)
            failed = failed || childFailed
            stopped = childFailed && when (config.errorBehavior) {
                TaskExecutionConfig.ErrorBehavior.Continue -> false
                TaskExecutionConfig.ErrorBehavior.StopGroup -> child is TaskPlanTask
                TaskExecutionConfig.ErrorBehavior.StopRootGroup ->
                    parentId != null || child is TaskPlanTask
                TaskExecutionConfig.ErrorBehavior.StopAll -> true
            }
        }
    }
    return failed
}

private fun TaskPlanExecutable.skip(
    listener: TaskExecutionListener?,
    parentId: String?,
    timeMs: Long,
) {
    when (this) {
        is TaskPlanTask -> listener?.onTaskSkipped(id, title, parentId, timeMs)
        is TaskPlanGroup -> {
            listener?.onGroupSkipped(id, title, parentId, timeMs)
            children.forEach { it.skip(listener, id, timeMs) }
        }
    }
}

fun taskPlan(
    block: TaskPlanBuilder.() -> Unit,
): TaskPlan {
    return TaskPlanBuilder()
        .apply(block)
        .build()
}

class TaskPlanBuilder {

    private val children = mutableListOf<TaskPlanExecutable>()

    fun group(
        title: String,
        block: TaskPlanGroupBuilder.() -> Unit,
    ) {
        children += TaskPlanGroupBuilder(title)
            .apply(block)
            .build()
    }

    internal fun build(): TaskPlan {
        return TaskPlan(
            children = children.toList()
        )
    }
}

class TaskPlanGroupBuilder internal constructor(
    private val title: String,
) {

    private val children = mutableListOf<TaskPlanExecutable>()

    fun task(
        title: String,
        block: suspend TaskExecutionContext.() -> TaskResult,
    ) {
        children += TaskPlanTask(
            id = Uuid.random().toString(),
            title = title,
            block = block,
        )
    }

    fun group(
        title: String,
        block: TaskPlanGroupBuilder.() -> Unit,
    ) {
        children += TaskPlanGroupBuilder(title)
            .apply(block)
            .build()
    }

    internal fun build(): TaskPlanGroup {
        return TaskPlanGroup(
            id = Uuid.random().toString(),
            title = title,
            children = children.toList(),
        )
    }
}