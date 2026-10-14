package com.michaelflisar.toolbox.tasks.plan

import com.michaelflisar.toolbox.tasks.TaskConfig
import com.michaelflisar.toolbox.tasks.execution.TaskDataStore
import com.michaelflisar.toolbox.tasks.execution.TaskExecutionContext
import com.michaelflisar.toolbox.tasks.execution.TaskHandle
import com.michaelflisar.toolbox.tasks.execution.TaskKey
import com.michaelflisar.toolbox.tasks.execution.TaskResult
import com.michaelflisar.toolbox.tasks.execution.TaskStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.uuid.Uuid

sealed interface TaskPlanNode {
    val id: String
    val title: String
}

sealed interface TaskPlanExecutable : TaskPlanNode {
    /** Liefert true, wenn dieser Knoten oder ein Kind einen Fehler hatte. */
    suspend fun execute(
        config: TaskConfig.ExecutionConfig,
        listener: TaskExecutionListener?,
        parentId: String?,
    ): Boolean
}

@DslMarker
annotation class TaskPlanDsl

/** Shared structure for the invisible root group and nested visible groups. */
interface TaskPlanGroupNode {
    val children: List<TaskPlanExecutable>
}

data class TaskPlan(
    val name: String,
    override val children: List<TaskPlanExecutable>,
) : TaskPlanGroupNode {

    /**
     * führt einen TaskPlan aus. Liefert true, wenn ein Fehler aufgetreten ist.
     *
     * @param config die Konfiguration für die Ausführung
     * @param listener optionaler Listener, der über den Fortschritt informiert (z.b. [com.michaelflisar.toolbox.tasks.ui.state.TaskViewState])
     * @return true, wenn ein Fehler aufgetreten ist
     */
    suspend fun execute(
        config: TaskConfig.ExecutionConfig,
        listener: TaskExecutionListener? = null
    ) = withContext(TaskDataStore()) {
        children.executeChildren(config, listener, null)
    }
}

data class TaskPlanSummary(
    val running: Int = 0,
    val success: Int = 0,
    val info: Int = 0,
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

            info > 0 ->
                TaskStatus.Info(null)

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
    override val children: List<TaskPlanExecutable>,
) : TaskPlanExecutable, TaskPlanGroupNode {

    override suspend fun execute(
        config: TaskConfig.ExecutionConfig,
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
    val block: suspend TaskExecutionContext.() -> TaskResult<*>,
) : TaskPlanExecutable {

    override suspend fun execute(
        config: TaskConfig.ExecutionConfig,
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
            colors = config.taskMessageColors,
            listener = listener,
            taskId = id,
            taskData = currentCoroutineContext()[TaskDataStore] ?: TaskDataStore(),
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
    config: TaskConfig.ExecutionConfig,
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
                TaskConfig.ErrorBehavior.Continue -> false
                TaskConfig.ErrorBehavior.StopGroup -> child is TaskPlanTask
                TaskConfig.ErrorBehavior.StopRootGroup ->
                    parentId != null || child is TaskPlanTask
                TaskConfig.ErrorBehavior.StopAll -> true
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

/**
 * der Root DSL Builder für einen TaskPlan.
 *
 * @param name der Name des Plans, der in der UI angezeigt wird
 * @param block der Block, in dem Tasks und Gruppen definiert werden
 * @return der erstellte TaskPlan
 */
fun taskPlan(
    name: String,
    block: TaskPlanBuilder.() -> Unit,
): TaskPlan {
    return TaskPlanBuilder()
        .apply(block)
        .buildPlan(name)
}

@TaskPlanDsl
class TaskPlanBuilder internal constructor() {

    private val children = mutableListOf<TaskPlanExecutable>()

    /**
     * Adds a task with typed output and returns a reference for later tasks.
     *
     * ```kotlin
     * val filesTask = task("Create files") {
     *     TaskResult.Success("Files created", data = listOf("a.txt", "b.txt"))
     * }
     * task("Upload files") {
     *     val files = filesTask.requireData()
     *     TaskResult.Success("Uploading ${files.size} files")
     * }
     * ```
     *
     * Output is local to one plan execution and shared across nested groups.
     * Success, Info, Warning and Skipped publish output; Error and cancellation do not.
     * Skipped does not trigger error behavior and is shown as skipped in the viewer.
     * requireData() fails the consumer on missing output; data returns null instead.
     * Tasks returning Success, Info, Warning or Skipped without output have result type Unit.
     * Bei Fehlern behandeln StopGroup und StopRootGroup den Plan als Gruppe.
     */
    fun <T> task(
        title: String,
        block: suspend TaskExecutionContext.() -> TaskResult<T>,
    ): TaskHandle<T> {
        val key = TaskKey<T>(title)
        val task = createTask(title) {
            val result = block()
            currentCoroutineContext().ensureActive()
            when (result) {
                is TaskResult.Success -> storeResult(key, result.data)
                is TaskResult.Info -> storeResult(key, result.data)
                is TaskResult.Warning -> storeResult(key, result.data)
                is TaskResult.Skipped -> storeResult(key, result.data)
                is TaskResult.Error -> Unit
            }
            result
        }
        children += task
        return TaskHandle(task.id, task.title, key)
    }

    fun group(
        title: String,
        block: TaskPlanBuilder.() -> Unit,
    ) {
        children += TaskPlanBuilder()
            .apply(block)
            .buildGroup(title)
    }

    internal fun buildPlan(name: String): TaskPlan {
        return TaskPlan(
            name = name,
            children = children.toList()
        )
    }

    internal fun buildGroup(title: String): TaskPlanGroup {
        return TaskPlanGroup(
            id = Uuid.random().toString(),
            title = title,
            children = children.toList(),
        )
    }
}

private fun createTask(
    title: String,
    block: suspend TaskExecutionContext.() -> TaskResult<*>,
): TaskPlanTask = TaskPlanTask(
    id = Uuid.random().toString(),
    title = title,
    block = block,
)