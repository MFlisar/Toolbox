package com.michaelflisar.toolbox.tasks.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.michaelflisar.toolbox.tasks.execution.TaskStatus

@Stable
data class TaskViewStateConfig(
    /**
     * Neue Tasks automatisch öffnen.
     */
    val autoExpandNewTasks: Boolean,

    /**
     * Laufende Tasks automatisch öffnen.
     */
    val expandRunningTasks: Boolean,

    /**
     * Verhalten nach Abschluss.
     */
    val finishedTaskBehavior: FinishedBehavior,
) {

    @Stable
    data class FinishedBehavior(
        val success: Display = Display.Closed,
        val warning: Display = Display.Closed,
        val error: Display = Display.Open,
        val cancelled: Display = Display.Closed,
    ) {
        companion object {
            val CLOSE_ALL = FinishedBehavior(
                success = Display.Closed,
                warning = Display.Closed,
                error = Display.Closed,
                cancelled = Display.Closed,
            )
            val OPEN_ALL = FinishedBehavior(
                success = Display.Open,
                warning = Display.Open,
                error = Display.Open,
                cancelled = Display.Open,
            )
        }
        enum class Display {
            Open,
            Closed
        }

        fun get(status: TaskStatus.Finished): Display {
            return when (status) {
                is TaskStatus.Success -> success
                is TaskStatus.Warning -> warning
                is TaskStatus.Error -> error
                TaskStatus.Cancelled -> cancelled
            }
        }
    }
}

@Composable
fun rememberTaskViewStateConfig(
    autoExpandNewTasks: Boolean = true,
    expandRunningTasks: Boolean = true,
    finishedTaskBehavior: TaskViewStateConfig.FinishedBehavior = TaskViewStateConfig.FinishedBehavior.CLOSE_ALL
): TaskViewStateConfig {
    return remember(
        autoExpandNewTasks,
        expandRunningTasks,
        finishedTaskBehavior
    ) {
        TaskViewStateConfig(
            autoExpandNewTasks = autoExpandNewTasks,
            expandRunningTasks = expandRunningTasks,
            finishedTaskBehavior = finishedTaskBehavior
        )
    }
}