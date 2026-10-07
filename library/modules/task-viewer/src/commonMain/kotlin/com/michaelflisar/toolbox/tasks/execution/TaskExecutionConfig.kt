package com.michaelflisar.toolbox.tasks.execution

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.michaelflisar.toolbox.tasks.plan.TaskMessageColors
import com.michaelflisar.toolbox.tasks.plan.rememberTaskMessageColors

@Stable
data class TaskExecutionConfig(
    val taskMessageColors: TaskMessageColors,
    val errorBehavior: ErrorBehavior = ErrorBehavior.Continue,
) {
    enum class ErrorBehavior {
        /** Nach Fehlern weiter ausfuehren. */
        Continue,
        /** Rest der unmittelbar betroffenen Gruppe ueberspringen. */
        StopGroup,
        /** Rest der obersten Gruppe ueberspringen, danach weiter. */
        StopRootGroup,
        /** Rest des gesamten Plans ueberspringen. */
        StopAll,
    }
}

@Composable
fun rememberTaskExecutionConfig(
    taskMessageColors: TaskMessageColors = rememberTaskMessageColors(),
    errorBehavior: TaskExecutionConfig.ErrorBehavior = TaskExecutionConfig.ErrorBehavior.Continue,
): TaskExecutionConfig {
    return remember(taskMessageColors, errorBehavior) { TaskExecutionConfig(taskMessageColors, errorBehavior) }
}