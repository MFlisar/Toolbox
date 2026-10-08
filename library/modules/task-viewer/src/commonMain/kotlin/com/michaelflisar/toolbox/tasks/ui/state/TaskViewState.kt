package com.michaelflisar.toolbox.tasks.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import com.michaelflisar.toolbox.tasks.TaskConfig
import com.michaelflisar.toolbox.tasks.execution.TaskResult
import com.michaelflisar.toolbox.tasks.execution.TaskStatus
import com.michaelflisar.toolbox.tasks.plan.TaskExecutionListener
import com.michaelflisar.toolbox.tasks.plan.TaskMessage
import com.michaelflisar.toolbox.tasks.plan.TaskPlan
import com.michaelflisar.toolbox.tasks.plan.TaskPlanExecutable
import com.michaelflisar.toolbox.tasks.plan.TaskPlanGroup
import com.michaelflisar.toolbox.tasks.plan.TaskPlanGroupNode
import com.michaelflisar.toolbox.tasks.plan.TaskPlanSummary
import com.michaelflisar.toolbox.tasks.plan.TaskPlanTask
import kotlin.time.Clock

@Composable
fun rememberTaskViewState(
    config: TaskConfig.ViewStateConfig = TaskConfig.ViewStateConfig.Default,
): TaskViewState {
    return remember(config) {
        TaskViewState(config)
    }
}

@Stable
class TaskViewState internal constructor(
    internal val config: TaskConfig.ViewStateConfig,
) : TaskExecutionListener {

    private val states = mutableStateMapOf<String, TaskNodeState>()
    private val groupParents = mutableMapOf<String, String?>()

    internal fun getNodeState(id: String): TaskNodeState? = states[id]

    internal fun getTaskState(id: String): TaskState? = states[id] as? TaskState

    internal fun getGroupState(id: String): TaskGroupState? = states[id] as? TaskGroupState

    var nowMs by mutableLongStateOf(
        Clock.System.now().toEpochMilliseconds()
    )
        private set

    internal fun tick() {
        nowMs = Clock.System.now()
            .toEpochMilliseconds()
    }

    val hasRunningTasks by derivedStateOf {
        states.values.any { !it.isFinished }
    }

    val hasFinishedTasks by derivedStateOf {
        states.values.any { it.isFinished }
    }

    fun reset() {
        states.clear()
        groupParents.clear()
    }

    suspend fun execute(
        plan: TaskPlan,
        config: TaskConfig
    ) = plan.execute(config.execution, this)

    internal fun getSummary(
        group: TaskPlanGroupNode,
    ): TaskPlanSummary {
        fun collect(
            group: TaskPlanGroupNode,
        ): TaskPlanSummary {
            return group.children
                .fold(TaskPlanSummary()) { acc, node ->
                    val current = when (node) {
                        is TaskPlanTask -> {
                            val state = getTaskState(node.id)
                            when (state?.status) {
                                TaskStatus.Running -> TaskPlanSummary(running = 1)
                                is TaskStatus.Success -> TaskPlanSummary(success = 1)
                                is TaskStatus.Warning -> TaskPlanSummary(warnings = 1)
                                is TaskStatus.Error -> TaskPlanSummary(errors = 1)
                                TaskStatus.Cancelled -> TaskPlanSummary(skipped = 1)
                                null -> TaskPlanSummary()
                            }
                        }

                        is TaskPlanGroup -> collect(node)
                    }

                    acc.copy(
                        running = acc.running + current.running,
                        success = acc.success + current.success,
                        warnings = acc.warnings + current.warnings,
                        errors = acc.errors + current.errors,
                        skipped = acc.skipped + current.skipped,
                    )
                }
        }

        return collect(group)
    }

    override fun onTaskSkipped(
        id: String,
        title: String,
        parentId: String?,
        timeMs: Long,
    ) {
        Snapshot.withMutableSnapshot {
            states[id] = TaskState(
                startedAt = timeMs,
                expanded = config.finishedTaskBehavior.get(TaskStatus.Cancelled) ==
                        TaskConfig.FinishedTaskDisplay.Open,
            ).apply {
                status = TaskStatus.Cancelled
                finishedAt = timeMs
            }
        }
    }

    override fun onGroupSkipped(
        id: String,
        title: String,
        parentId: String?,
        timeMs: Long,
    ) {
        groupParents[id] = parentId
        Snapshot.withMutableSnapshot {
            states[id] = TaskGroupState(
                startedAt = timeMs,
                expanded = config.autoExpandGroups,
                skipped = true,
            ).apply {
                finishedAt = timeMs
            }
        }
    }

    override fun onGroupStarted(
        id: String,
        title: String,
        parentId: String?,
        startTimeMs: Long,
    ) {
        groupParents[id] = parentId
        states[id] = TaskGroupState(
            startedAt = startTimeMs,
            expanded = config.autoExpandGroups,
        )
    }

    override fun onTaskStarted(
        id: String,
        title: String,
        parentId: String?,
        startTimeMs: Long,
    ) {
        Snapshot.withMutableSnapshot {
            states[id] = TaskState(
                startedAt = startTimeMs,
                expanded = config.autoExpandRunningTaskPath,
            )
            if (config.autoExpandRunningTaskPath) {
                var ancestorId = parentId
                while (ancestorId != null) {
                    getGroupState(ancestorId)?.expanded = true
                    ancestorId = groupParents[ancestorId]
                }
            }
        }
    }

    override fun onTaskStatusChanged(
        taskId: String,
        status: String?,
    ) {
        getTaskState(taskId)?.subtitle = status
    }

    override fun onTaskMessage(
        taskId: String,
        message: TaskMessage,
    ) {
        getTaskState(taskId)?.addMessage(message)
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

        Snapshot.withMutableSnapshot {
            getTaskState(taskId)?.let { task ->
                task.status = status
                task.finishedAt = endTimeMs
                task.expanded = config.finishedTaskBehavior.get(status) ==
                        TaskConfig.FinishedTaskDisplay.Open
                task.subtitle = result.status
            }
        }
    }

    override fun onGroupFinished(
        id: String,
        endTimeMs: Long,
    ) {
        getGroupState(id)?.finishedAt = endTimeMs
    }

    internal fun toggleExpanded(
        plan: TaskPlan,
        id: String,
        expandSinglePathOnly: Boolean,
    ) {
        Snapshot.withMutableSnapshot {
            val current = states[id] ?: return@withMutableSnapshot
            if (!expandSinglePathOnly || current.expanded) {
                current.expanded = !current.expanded
            } else {
                expandSinglePath(plan, id)
            }
        }
    }

    private fun expandSinglePath(
        plan: TaskPlan,
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

            if (node is TaskPlanGroupNode) {

                node.children.forEach { child ->

                    if (findPath(child)) {
                        path += node.id
                        return true
                    }
                }
            }

            return false
        }

        if (plan.children.any(::findPath)) {
            states.forEach { (nodeId, state) ->
                state.expanded = nodeId in path
            }
        }
    }
}
