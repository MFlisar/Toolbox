package com.michaelflisar.toolbox.tasks

import kotlin.time.Clock
import kotlin.uuid.Uuid

sealed interface TaskPlanNode {
    val id: String
    val title: String
}

sealed interface TaskPlanExecutable : TaskPlanNode {
    suspend fun execute(
        listener: TaskExecutionListener = EmptyTaskExecutionListener,
        parentId: String?,
    )
}

data class TaskPlanRoot(
    val children: List<TaskPlanExecutable>,
) {
    suspend fun execute(
        listener: TaskExecutionListener = EmptyTaskExecutionListener,
    ) {
        children.forEach {
            it.execute(
                listener = listener,
                parentId = null,
            )
        }
    }
}

data class TaskPlanSummary(
    val running: Int = 0,
    val success: Int = 0,
    val warnings: Int = 0,
    val errors: Int = 0,
) {
    internal fun toStatus(): TaskStatus {
        return when {
            running > 0 ->
                TaskStatus.Running

            errors > 0 ->
                TaskStatus.Error(Exception())

            warnings > 0 ->
                TaskStatus.Warning("")

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
        listener: TaskExecutionListener,
        parentId: String?,
    ) {
        val start = Clock.System.now().toEpochMilliseconds()
        listener.onGroupStarted(
            id = id,
            title = title,
            parentId = parentId,
            startTimeMs = start
        )
        children.forEach {
            it.execute(
                listener = listener,
                parentId = id,
            )
        }
        val end = Clock.System.now().toEpochMilliseconds()
        listener.onGroupFinished(
            id = id,
            endTimeMs = end
        )
    }
}

data class TaskPlanTask(
    override val id: String,
    override val title: String,
    val block: suspend TaskExecutionContext.() -> TaskResult,
) : TaskPlanExecutable {

    override suspend fun execute(
        listener: TaskExecutionListener,
        parentId: String?,
    ) {
        val start = Clock.System.now().toEpochMilliseconds()
        listener.onTaskStarted(
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
        } catch (e: Exception) {
            TaskResult.Error(e)
        }

        val end = Clock.System.now().toEpochMilliseconds()
        listener.onTaskFinished(
            taskId = id,
            result = result,
            endTimeMs = end
        )
    }
}

fun taskPlan(
    block: TaskPlanBuilder.() -> Unit,
): TaskPlanRoot {
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

    internal fun build(): TaskPlanRoot {
        return TaskPlanRoot(
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