package com.michaelflisar.toolbox.tasks.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.michaelflisar.toolbox.tasks.plan.TaskPlanSummary
import com.michaelflisar.toolbox.utils.TimeUtil

object TaskViewerConfigDefaults {

    fun groupSummaryFormatter(summary: TaskPlanSummary): String {
        return buildString {
            if (summary.running > 0) {
                append("Running")
            }
            if (summary.success > 0) {
                if (isNotEmpty()) append(", ")
                append("${summary.success} successful")
            }
            if (summary.warnings > 0) {
                if (isNotEmpty()) append(", ")
                append("${summary.warnings} warning")
            }
            if (summary.errors > 0) {
                if (isNotEmpty()) append(", ")
                append("${summary.errors} error")
            }
            if (summary.skipped > 0) {
                if (isNotEmpty()) append(", ")
                append("${summary.skipped} skipped")
            }
        }
    }

    fun timeFormatter(isFinished: Boolean, millis: Long): String {
        return TimeUtil.getTimeString(
            millis = millis,
            secondFractionDigits = if (isFinished && millis < 60_000L) {
                1
            } else {
                0
            },
        )
    }
}

@Stable
data class TaskViewerConfig(
    val containerColor: Color,
    val contentColor: Color,
    val autoScrollToBottom: Boolean,
    val showTaskTimes: Boolean,
    val expandSinglePathOnly: Boolean,
    val groupSummaryFormatter: (TaskPlanSummary) -> String,
    val timeFormatter: (isFinished: Boolean, millis: Long) -> String,
    val skippedTaskSubtitle: String,
)

@Composable
fun rememberTaskViewerConfig(
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    autoScrollToBottom: Boolean = true,
    showTaskTimes: Boolean = true,
    expandSinglePathOnly: Boolean = false,
    groupSummaryFormatter: (TaskPlanSummary) -> String = TaskViewerConfigDefaults::groupSummaryFormatter,
    timeFormatter: (isFinished: Boolean, millis: Long) -> String = TaskViewerConfigDefaults::timeFormatter,
    skippedTaskSubtitle: String = "Task skipped",
): TaskViewerConfig {
    return remember(
        containerColor,
        contentColor,
        autoScrollToBottom,
        showTaskTimes,
        expandSinglePathOnly,
        groupSummaryFormatter,
        timeFormatter,
        skippedTaskSubtitle,
    ) {
        TaskViewerConfig(
            containerColor = containerColor,
            contentColor = contentColor,
            autoScrollToBottom = autoScrollToBottom,
            showTaskTimes = showTaskTimes,
            expandSinglePathOnly = expandSinglePathOnly,
            groupSummaryFormatter = groupSummaryFormatter,
            timeFormatter = timeFormatter,
            skippedTaskSubtitle = skippedTaskSubtitle,
        )
    }
}