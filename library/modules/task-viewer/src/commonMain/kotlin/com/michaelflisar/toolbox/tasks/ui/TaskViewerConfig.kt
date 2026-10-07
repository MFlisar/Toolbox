package com.michaelflisar.toolbox.tasks.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.extensions.isDark
import com.michaelflisar.toolbox.tasks.plan.TaskMessageColors
import com.michaelflisar.toolbox.tasks.plan.TaskPlanSummary
import com.michaelflisar.toolbox.tasks.plan.rememberTaskMessageColors
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
    val taskMessageColors: TaskMessageColors,
    val containerColor: Color,
    val contentColor: Color,
    val autoScrollToBottom: Boolean,
    val showTaskTimes: Boolean,
    val expandSinglePathOnly: Boolean,
    val layout: TaskViewerLayout,
    val groupSummaryFormatter: (TaskPlanSummary) -> String,
    val timeFormatter: (isFinished: Boolean, millis: Long) -> String,
    val skippedTaskSubtitle: String,
    val prefixWarning: String,
    val prefixError: String,
    val showHeaderNumbers: Boolean = false,
    val showMessageNumbers: Boolean = false,
)

@Stable
data class TaskViewerLayout(
    val itemSpacing: Dp = 8.dp,
    val horizontalPadding: Dp = 12.dp,
    val verticalPadding: Dp = 8.dp,
    val iconSpacing: Dp = 8.dp,
    val expandIconSize: Dp = 18.dp,
    val statusIconSize: Dp = 18.dp,
    val indentPerLevel: Dp = expandIconSize + iconSpacing,
    val messageBulletWidth: Dp = 12.dp,
    val expandedContentSpacing: Dp = 8.dp,
    val expandedContentVerticalPadding: Dp = 8.dp,
    val minItemHeight: Dp = 48.dp,
    val messageNumberSpacing: Dp = 4.dp
) {
    companion object {
        val Default = TaskViewerLayout()
        val Compact = TaskViewerLayout(
            itemSpacing = 2.dp,
            horizontalPadding = 8.dp,
            verticalPadding = 2.dp,
            iconSpacing = 4.dp,
            expandIconSize = 16.dp,
            statusIconSize = 16.dp,
            indentPerLevel = 20.dp,
            messageBulletWidth = 10.dp,
            expandedContentSpacing = 2.dp,
            expandedContentVerticalPadding = 4.dp,
            minItemHeight = 24.dp,
            messageNumberSpacing = 4.dp
        )
    }
}

@Composable
fun rememberTaskViewerConfig(
    taskMessageColors: TaskMessageColors = rememberTaskMessageColors(),
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    autoScrollToBottom: Boolean = true,
    showTaskTimes: Boolean = true,
    expandSinglePathOnly: Boolean = false,
    layout: TaskViewerLayout = TaskViewerLayout.Default,
    groupSummaryFormatter: (TaskPlanSummary) -> String = TaskViewerConfigDefaults::groupSummaryFormatter,
    timeFormatter: (isFinished: Boolean, millis: Long) -> String = TaskViewerConfigDefaults::timeFormatter,
    skippedTaskSubtitle: String = "Task skipped",
    prefixWarning: String = "Warning: ",
    prefixError: String = "Error: ",
    showHeaderNumbers: Boolean = false,
    showMessageNumbers: Boolean = true,
): TaskViewerConfig {
    return remember(
        taskMessageColors,
        containerColor,
        contentColor,
        autoScrollToBottom,
        showTaskTimes,
        expandSinglePathOnly,
        layout,
        groupSummaryFormatter,
        timeFormatter,
        skippedTaskSubtitle,
        prefixWarning,
        prefixError,
        showHeaderNumbers,
        showMessageNumbers,
    ) {
        TaskViewerConfig(
            taskMessageColors = taskMessageColors,
            containerColor = containerColor,
            contentColor = contentColor,
            autoScrollToBottom = autoScrollToBottom,
            showTaskTimes = showTaskTimes,
            expandSinglePathOnly = expandSinglePathOnly,
            layout = layout,
            groupSummaryFormatter = groupSummaryFormatter,
            timeFormatter = timeFormatter,
            skippedTaskSubtitle = skippedTaskSubtitle,
            prefixWarning = prefixWarning,
            prefixError = prefixError,
            showHeaderNumbers = showHeaderNumbers,
            showMessageNumbers = showMessageNumbers,
        )
    }
}