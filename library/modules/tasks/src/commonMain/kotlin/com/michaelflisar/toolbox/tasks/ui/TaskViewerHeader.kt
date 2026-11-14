package com.michaelflisar.toolbox.tasks.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.classes.CustomColors
import com.michaelflisar.composestyled.classes.toIconComposable
import com.michaelflisar.composestyled.tokens.LocalStyledContentColor
import com.michaelflisar.composestyled.ui.components.StyledCard
import com.michaelflisar.composestyled.ui.components.StyledCardDefaults
import com.michaelflisar.composestyled.ui.components.StyledIcon
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.toolbox.tasks.TaskConfig
import com.michaelflisar.toolbox.tasks.execution.TaskStatus
import com.michaelflisar.toolbox.tasks.plan.TaskPlan
import com.michaelflisar.toolbox.tasks.plan.TaskPlanExecutable
import com.michaelflisar.toolbox.tasks.plan.TaskPlanGroupNode
import com.michaelflisar.toolbox.tasks.plan.TaskPlanTask
import com.michaelflisar.toolbox.tasks.ui.parts.ProgressDone
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
    var showDone by remember { mutableStateOf(false) }
    LaunchedEffect(summary.running, summary.isFinished) {
        if (summary.running > 0) {
            showDone = false
        } else if (summary.isFinished) {
            showDone = true
            delay(2000.milliseconds)
            showDone = false
        } else {
            showDone = false
        }
    }

    val headerContainerColor = StyledTheme.colorScheme.surfaceVariant
    val headerContentColor = StyledTheme.colorScheme.onSurfaceVariant

    StyledCard(
        modifier = modifier.fillMaxWidth(),
        variant = StyledCard.Variant.Filled,
        contentPadding = PaddingValues(
            horizontal = StyledTheme.padding.medium,
            vertical = StyledTheme.padding.small,
        ),
        customization = StyledCardDefaults.customize(
            colors = CustomColors(
                filledBackground = headerContainerColor,
                filledForeground = headerContentColor,
                textForeground = headerContentColor,
            ),
            border = StyledTheme.colorScheme.outlineVariant,
        ),
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (maxWidth >= 720.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HeaderOverview(plan.name, summary, config, showDone)
                    Spacer(Modifier.weight(1f))
                    HeaderMetrics(summary, config, headerContainerColor)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    HeaderOverview(plan.name, summary, config, showDone)
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
    showDone: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(
            visible = summary.running > 0 || showDone,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally(),
        ) {
            Row {
                Crossfade(
                    targetState = summary.running > 0
                ) {
                    if (it)
                        ProgressIcon()
                    else
                        ProgressDone()
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(StyledTheme.spacing.small)) {
            StyledText(
                text = planName,
                style = StyledTheme.typography.titleSmall,
            )
            StyledText(
                text = config.headerSummaryFormatter(
                    summary.isFinished,
                    summary.executedTasks,
                    summary.skipped,
                    summary.totalTasks,
                    config.timeFormatter(summary.isFinished, summary.durationMs),
                ),
                style = StyledTheme.typography.bodySmall,
                color = LocalStyledContentColor.current.copy(alpha = .7f),
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
        horizontalArrangement = Arrangement.spacedBy(StyledTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (summary.pending > 0) {
            HeaderMetric(
                value = summary.pending.toString(),
                label = config.headerPendingLabel(summary.pending),
                icon = Icons.Default.HourglassEmpty
            )
        }
        // es läuft immer nur eine Aufgabe pro Gruppe, daher ist es nicht nötig, die Anzahl der laufenden Aufgaben anzuzeigen
        if (summary.success > 0) {
            HeaderMetric(
                value = summary.success.toString(),
                label = config.headerPassedLabel(summary.success),
                icon = Icons.Default.CheckCircle,
                color = config.taskMessageColors.colorSuccess(headerContainerColor),
            )
        }
        if (summary.info > 0) {
            HeaderMetric(
                value = summary.info.toString(),
                label = config.headerInfoLabel(summary.info),
                icon = Icons.Default.Info,
                color = config.taskMessageColors.colorInfo(headerContainerColor),
            )
        }
        if (summary.warnings > 0) {
            HeaderMetric(
                value = summary.warnings.toString(),
                label = config.headerWarningsLabel(summary.warnings),
                icon = Icons.Default.Warning,
                color = config.taskMessageColors.colorWarning(headerContainerColor),
            )
        }
        if (summary.errors > 0) {
            HeaderMetric(
                value = summary.errors.toString(),
                label = config.headerFailedLabel(summary.errors),
                icon = Icons.Default.Error,
                color = config.taskMessageColors.colorError(headerContainerColor),
            )
        }
        if (summary.skipped > 0) {
            HeaderMetric(
                value = summary.skipped.toString(),
                label = config.headerSkippedLabel(summary.skipped),
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
    color: Color = LocalStyledContentColor.current,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(StyledTheme.spacing.small),
    ) {
        if (icon != null) {
            StyledIcon(
                icon = icon.toIconComposable(),
                contentDescription = null,
                size = StyledTheme.sizes.iconSize,
                tint = color,
            )
        }
        StyledText(
            text = value,
            style = StyledTheme.typography.labelLarge,
            color = color,
            softWrap = false,
        )
        StyledText(
            text = label,
            style = StyledTheme.typography.bodySmall,
            color = LocalStyledContentColor.current.copy(alpha = .7f),
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
    val info: Int,
    val warnings: Int,
    val errors: Int,
    val skipped: Int,
    val durationMs: Long,
    val isFinished: Boolean,
) {
    val executedTasks: Int
        get() = success + info + warnings + errors
}

private fun calculateHeaderSummary(
    plan: TaskPlan,
    state: TaskViewState,
): TaskViewerHeaderSummary {
    var total = 0
    var pending = 0
    var running = 0
    var success = 0
    var info = 0
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
                    is TaskStatus.Info -> info++
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

    val isFinished =
        hasStarted && running == 0 && success + info + warnings + errors + skipped == total
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
        info = info,
        warnings = warnings,
        errors = errors,
        skipped = skipped,
        durationMs = duration,
        isFinished = isFinished,
    )
}
