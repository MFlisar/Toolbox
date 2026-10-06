package com.michaelflisar.toolbox.tasks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.time.Clock

@Composable
fun rememberTaskReporterConfig(
    autoExpandNewTasks: Boolean = true,
    expandRunningTasks: Boolean = true,
    finishedTaskBehavior: FinishedTaskBehavior =
        FinishedTaskBehavior(
            success = FinishedTaskBehavior.Display.Closed,
            warning = FinishedTaskBehavior.Display.Closed,
            error = FinishedTaskBehavior.Display.Open,
        ),
    expandSinglePathOnly: Boolean = false,
): TaskReporter.Config {
    return remember(
        autoExpandNewTasks,
        expandRunningTasks,
        finishedTaskBehavior,
        expandSinglePathOnly
    ) {
        TaskReporter.Config(
            autoExpandNewTasks = autoExpandNewTasks,
            expandRunningTasks = expandRunningTasks,
            finishedTaskBehavior = finishedTaskBehavior,
            expandSinglePathOnly = expandSinglePathOnly
        )
    }
}

@Composable
fun rememberTaskReporter(
    plan: TaskPlanRoot,
    config: TaskReporter.Config = rememberTaskReporterConfig(),
): TaskReporter {
    return remember(plan, config) {
        TaskReporter(plan, config)
    }
}

class TaskReporter internal constructor(
    val plan: TaskPlanRoot,
    internal val config: Config,
) : TaskExecutionListener {

    @Stable
    data class Config(
        val autoExpandNewTasks: Boolean,
        val expandRunningTasks: Boolean,
        val finishedTaskBehavior: FinishedTaskBehavior,
        val expandSinglePathOnly: Boolean,
    )

    internal sealed interface TaskRuntime {
        val id: String
        val parentId: String?
        val expanded: Boolean
        val startedAt: Long
        val finishedAt: Long?

        fun durationMs(
            nowMs: Long,
        ): Long {
            return ((finishedAt ?: nowMs) - startedAt).coerceAtLeast(0L)
        }

        val isFinished: Boolean
    }

    private fun TaskRuntime.withExpanded(
        expanded: Boolean,
    ): TaskRuntime {
        return when (this) {
            is TaskRuntimeTask -> copy(
                expanded = expanded,
            )

            is TaskRuntimeGroup -> copy(
                expanded = expanded,
            )
        }
    }

    internal data class TaskRuntimeTask(
        override val id: String,
        override val parentId: String?,
        val status: TaskStatus = TaskStatus.Running,
        val subtitle: String? = null,
        val messages: List<TaskMessage> = emptyList(),
        override val expanded: Boolean = false,
        override val startedAt: Long,
        override val finishedAt: Long? = null,
    ) : TaskRuntime {
        override val isFinished: Boolean
            get() = status != TaskStatus.Running
    }

    private data class TaskRuntimeGroup(
        override val id: String,
        override val parentId: String?,
        override val expanded: Boolean = false,
        override val startedAt: Long,
        override val finishedAt: Long? = null,
    ) : TaskRuntime {
        override val isFinished: Boolean
            get() = finishedAt != null
    }

    var nowMs by mutableLongStateOf(
        Clock.System.now().toEpochMilliseconds()
    )
        private set

    internal fun tick() {
        nowMs = Clock.System.now()
            .toEpochMilliseconds()
    }

    internal var runtime by mutableStateOf<Map<String, TaskRuntime>>(
        emptyMap()
    )
        private set

    val hasRunningTasks by derivedStateOf {
        runtime.values.any {
            when (it) {
                is TaskRuntimeTask -> it.status == TaskStatus.Running
                is TaskRuntimeGroup -> it.finishedAt == null
            }
        }
    }

    val hasFinishedTasks by derivedStateOf {
        runtime.values.any {
            when (it) {
                is TaskRuntimeTask -> it.status is TaskStatus.Finished
                is TaskRuntimeGroup -> it.finishedAt != null
            }
        }
    }

    fun reset() {
        runtime = emptyMap()
    }

    internal fun getSummary(
        group: TaskPlanGroup,
    ): TaskPlanSummary {

        fun collect(
            node: TaskPlanExecutable,
        ): TaskPlanSummary {

            return when (node) {

                is TaskPlanTask -> {

                    val state = runtime[node.id] as? TaskRuntimeTask

                    when (state?.status) {
                        TaskStatus.Running -> TaskPlanSummary(running = 1)
                        is TaskStatus.Success -> TaskPlanSummary(success = 1)
                        is TaskStatus.Warning -> TaskPlanSummary(warnings = 1)
                        is TaskStatus.Error -> TaskPlanSummary(errors = 1)
                        null -> TaskPlanSummary()
                    }
                }

                is TaskPlanGroup -> {

                    node.children
                        .map(::collect)
                        .fold(TaskPlanSummary()) { acc, current ->

                            acc.copy(
                                running = acc.running + current.running,
                                success = acc.success + current.success,
                                warnings = acc.warnings + current.warnings,
                                errors = acc.errors + current.errors,
                            )
                        }
                }
            }
        }

        return collect(group)
    }

    override fun onGroupStarted(
        id: String,
        title: String,
        parentId: String?,
        startTimeMs: Long,
    ) {
        runtime += id to TaskRuntimeGroup(
            id = id,
            parentId = parentId,
            startedAt = startTimeMs,
            expanded = config.autoExpandNewTasks,
        )
    }

    override fun onTaskStarted(
        id: String,
        title: String,
        parentId: String?,
        startTimeMs: Long,
    ) {
        runtime += id to TaskRuntimeTask(
            id = id,
            parentId = parentId,
            startedAt = startTimeMs,
            expanded = config.expandRunningTasks,
        )
    }

    override fun onTaskStatusChanged(
        taskId: String,
        status: String?,
    ) {
        updateTask(taskId) {
            copy(
                subtitle = status,
            )
        }
    }

    override fun onTaskMessage(
        taskId: String,
        message: TaskMessage,
    ) {
        updateTask(taskId) {
            copy(
                messages = messages + message,
            )
        }
    }

    override fun onTaskFinished(
        taskId: String,
        result: TaskResult,
        endTimeMs: Long,
    ) {
        val status = when (result) {
            is TaskResult.Success -> TaskStatus.Success
            is TaskResult.Warning -> TaskStatus.Warning(result.status)
            is TaskResult.Error -> TaskStatus.Error(result.exception)
        }

        updateTask(taskId) {
            copy(
                status = status,
                finishedAt = endTimeMs,
                expanded = config.finishedTaskBehavior.get(status) == FinishedTaskBehavior.Display.Open,
                subtitle = result.status
            )
        }
    }

    override fun onGroupFinished(
        id: String,
        endTimeMs: Long,
    ) {
        updateGroup(id) {
            copy(
                finishedAt = endTimeMs,
            )
        }
    }

    internal fun toggleExpanded(
        id: String,
    ) {
        if (!config.expandSinglePathOnly) {

            val current = runtime[id] ?: return

            runtime = runtime + (
                    id to current.withExpanded(
                        expanded = !current.expanded,
                    )
                    )

            return
        }

        val expanded = runtime[id]?.expanded ?: return

        if (expanded) {

            val current = runtime[id] ?: return

            runtime = runtime + (
                    id to current.withExpanded(
                        expanded = false,
                    )
                    )

        } else {

            expandSinglePath(
                id = id,
            )
        }
    }

    private fun expandSinglePath(
        id: String,
    ) {

        val path = mutableSetOf<String>()

        fun findPath(
            node: TaskPlanExecutable,
        ): Boolean {

            if (node.id == id) {
                path += node.id
                return true
            }

            if (node is TaskPlanGroup) {

                node.children.forEach { child ->

                    if (findPath(child)) {
                        path += node.id
                        return true
                    }
                }
            }

            return false
        }

        plan.children.forEach(::findPath)

        runtime = runtime.mapValues { (nodeId, state) ->
            state.withExpanded(
                expanded = nodeId in path,
            )
        }
    }

    private fun updateTask(
        id: String,
        block: TaskRuntimeTask.() -> TaskRuntimeTask,
    ) {
        val current = runtime[id] as? TaskRuntimeTask ?: return

        runtime = runtime + (
                id to current.block()
                )
    }

    private fun updateGroup(
        id: String,
        block: TaskRuntimeGroup.() -> TaskRuntimeGroup,
    ) {
        val current = runtime[id] as? TaskRuntimeGroup ?: return

        runtime = runtime + (
                id to current.block()
                )
    }
}

