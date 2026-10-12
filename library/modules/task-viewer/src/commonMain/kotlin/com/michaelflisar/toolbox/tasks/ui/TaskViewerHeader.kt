package com.michaelflisar.toolbox.tasks.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.tasks.TaskConfig
import com.michaelflisar.toolbox.tasks.execution.TaskStatus
import com.michaelflisar.toolbox.tasks.plan.TaskPlan
import com.michaelflisar.toolbox.tasks.plan.TaskPlanExecutable
import com.michaelflisar.toolbox.tasks.plan.TaskPlanGroupNode
import com.michaelflisar.toolbox.tasks.plan.TaskPlanTask
import com.michaelflisar.toolbox.tasks.ui.parts.ProgressIcon
import com.michaelflisar.toolbox.tasks.ui.state.TaskNodeState
import com.michaelflisar.toolbox.tasks.ui.state.TaskViewState
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun TaskViewerHeader(
    plan: TaskPlan,
    state: TaskViewState,
    config: TaskConfig.ViewerConfig,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(state.hasRunningTasks) {
        while (state.hasRunningTasks) {
            delay(1000.milliseconds)
            state.tick()
        }
    }

    val summary by remember(plan, state) {
        derivedStateOf { calculateHeaderSummary(plan, state) }
    }
    val headerContainerColor = MaterialTheme.colorScheme.surfaceContainerLow

    Card(
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(
            containerColor = headerContainerColor,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            if (maxWidth >= 720.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HeaderOverview(plan.name, summary, config)
                    Spacer(Modifier.weight(1f))
                    HeaderMetrics(summary, config, headerContainerColor)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    HeaderOverview(plan.name, summary, config)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        HeaderMetrics(summary, config, headerContainerColor)
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderOverview(
    planName: String,
    summary: TaskViewerHeaderSummary,
    config: TaskConfig.ViewerConfig,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Progress
        AnimatedVisibility(
            visible = summary.running > 0,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally(),
        ) {
            Row {
                ProgressIcon()
                Spacer(modifier = Modifier.width(16.dp))
            }

        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = planName,
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = config.headerSummaryFormatter(
                    summary.isFinished,
                    summary.executedTasks,
                    summary.totalTasks,
                    config.timeFormatter(summary.isFinished, summary.durationMs),
                ),
                style = MaterialTheme.typography.bodySmall,
                color = LocalContentColor.current.copy(alpha = .7f),
            )
        }
    }
}

@Composable
private fun HeaderMetrics(
    summary: TaskViewerHeaderSummary,
    config: TaskConfig.ViewerConfig,
    headerContainerColor: Color,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (summary.pending > 0) {
            HeaderMetric(
                value = summary.pending.toString(),
                label = config.headerPendingLabel,
                icon = Icons.Default.HourglassEmpty
            )
        }
        // es läuft immer nur eine Aufgabe pro Gruppe, daher ist es nicht nötig, die Anzahl der laufenden Aufgaben anzuzeigen
        //if (summary.running > 0) {
        //    HeaderMetric(
        //        summary.running.toString(),
        //        config.headerRunningLabel,
        //        color = MaterialTheme.colorScheme.primary
        //    )
        //}
        if (summary.success > 0) {
            HeaderMetric(
                value = summary.success.toString(),
                label = config.headerPassedLabel,
                icon = Icons.Default.CheckCircle,
                color = config.taskMessageColors.colorSuccess(headerContainerColor),
            )
        }
        if (summary.warnings > 0) {
            HeaderMetric(
                value = summary.warnings.toString(),
                label = config.headerWarningsLabel,
                icon = Icons.Default.Warning,
                color = config.taskMessageColors.colorWarning(headerContainerColor),
            )
        }
        if (summary.errors > 0) {
            HeaderMetric(
                value = summary.errors.toString(),
                label = config.headerFailedLabel,
                icon = Icons.Default.Error,
                color = config.taskMessageColors.colorError(headerContainerColor),
            )
        }
        if (summary.skipped > 0) {
            HeaderMetric(
                value = summary.skipped.toString(),
                label = config.headerSkippedLabel,
                icon = Icons.Default.Cancel
            )
        }
    }
}

@Composable
private fun HeaderMetric(
    value: String,
    label: String,
    icon: ImageVector? = null,
    color: Color = LocalContentColor.current,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = color,
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = color,
            softWrap = false,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = LocalContentColor.current.copy(alpha = .7f),
            softWrap = false,
        )
    }
}

private data class TaskViewerHeaderSummary(
    val totalTasks: Int,
    val hasStarted: Boolean,
    val pending: Int,
    val running: Int,
    val success: Int,
    val warnings: Int,
    val errors: Int,
    val skipped: Int,
    val durationMs: Long,
    val isFinished: Boolean,
) {
    val executedTasks: Int
        get() = success + warnings + errors
}

private fun calculateHeaderSummary(
    plan: TaskPlan,
    state: TaskViewState,
): TaskViewerHeaderSummary {
    var total = 0
    var pending = 0
    var running = 0
    var success = 0
    var warnings = 0
    var errors = 0
    var skipped = 0
    var firstStart: Long? = null
    var lastFinish: Long? = null
    var hasStarted = false

    fun includeTiming(runtime: TaskNodeState?) {
        if (runtime == null) return
        hasStarted = true
        firstStart = minOf(firstStart ?: runtime.startedAt, runtime.startedAt)
        runtime.finishedAt?.let { finishedAt ->
            lastFinish = maxOf(lastFinish ?: finishedAt, finishedAt)
        }
    }

    fun visit(node: TaskPlanExecutable) {
        includeTiming(state.getNodeState(node.id))
        when (node) {
            is TaskPlanTask -> {
                total++
                when (state.getTaskState(node.id)?.status) {
                    TaskStatus.Running -> running++
                    is TaskStatus.Success -> success++
                    is TaskStatus.Warning -> warnings++
                    is TaskStatus.Error -> errors++
                    TaskStatus.Cancelled -> skipped++
                    null -> pending++
                }
            }

            is TaskPlanGroupNode -> node.children.forEach(::visit)
        }
    }

    plan.children.forEach(::visit)

    val isFinished = hasStarted && running == 0 && success + warnings + errors + skipped == total
    val endTime = if (isFinished) lastFinish else state.nowMs.takeIf { hasStarted }
    val duration = firstStart?.let { start ->
        endTime?.let { end -> (end - start).coerceAtLeast(0L) }
    } ?: 0L

    return TaskViewerHeaderSummary(
        totalTasks = total,
        hasStarted = hasStarted,
        pending = pending,
        running = running,
        success = success,
        warnings = warnings,
        errors = errors,
        skipped = skipped,
        durationMs = duration,
        isFinished = isFinished,
    )
}
