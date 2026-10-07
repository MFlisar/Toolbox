package com.michaelflisar.toolbox.tasks.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.michaelflisar.toolbox.tasks.execution.TaskStatus

object TaskViewStateConfigDefaults {
    val finishedTaskBehavior: TaskViewStateConfig.FinishedBehavior =
        TaskViewStateConfig.FinishedBehavior(
            success = TaskViewStateConfig.FinishedBehavior.Display.Closed,
            warning = TaskViewStateConfig.FinishedBehavior.Display.Closed,
            error = TaskViewStateConfig.FinishedBehavior.Display.Open,
        )
}

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
    finishedTaskBehavior: TaskViewStateConfig.FinishedBehavior = TaskViewStateConfigDefaults.finishedTaskBehavior,
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