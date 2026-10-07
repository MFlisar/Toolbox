package com.michaelflisar.toolbox.tasks.execution

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember

@Stable
data class TaskExecutionConfig(
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
    errorBehavior: TaskExecutionConfig.ErrorBehavior = TaskExecutionConfig.ErrorBehavior.Continue,
): TaskExecutionConfig {
    return remember(errorBehavior) { TaskExecutionConfig(errorBehavior) }
}