package com.michaelflisar.toolbox.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.components.MyTextButton
import com.michaelflisar.toolbox.extensions.isDark
import com.michaelflisar.toolbox.spacing
import com.michaelflisar.toolbox.utils.TimeUtil
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

private val EXPAND_ICON_SIZE = 18.dp
private val STATUS_ICON_SIZE = 18.dp
private val TASK_INDENT_PER_LEVEL = EXPAND_ICON_SIZE
private val ICON_SPACING = 8.dp
private val TASK_HORIZONTAL_PADDING = 12.dp
private val TASK_VERTICAL_PADDING = 8.dp
private val MESSAGE_BULLET_WIDTH = 12.dp
private val MESSAGE_INDENT =
    TASK_HORIZONTAL_PADDING +
            EXPAND_ICON_SIZE +
            ICON_SPACING +
            STATUS_ICON_SIZE +
            ICON_SPACING

@Stable
data class TaskViewerConfig(
    val containerColor: Color,
    val contentColor: Color,
    val autoScrollToBottom: Boolean,
    val showTaskTimes: Boolean,
    val groupSummaryFormatter: (TaskPlanSummary) -> String,
    val timeFormatter: (isFinished: Boolean, millis: Long) -> String,
)

@Composable
fun rememberTaskViewerConfig(
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    autoScrollToBottom: Boolean = true,
    showTaskTimes: Boolean = true,
    groupSummaryFormatter: (TaskPlanSummary) -> String = { summary ->
        buildString {
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
        }
    },
    timeFormatter: (isFinished: Boolean, millis: Long) -> String = { isFinished, millis ->
        TimeUtil.getTimeString(
            millis = millis,
            secondFractionDigits = if (isFinished && millis < 60_000L) {
                1
            } else {
                0
            },
        )
    },
): TaskViewerConfig {
    return remember(
        containerColor,
        contentColor,
        autoScrollToBottom,
        showTaskTimes,
        groupSummaryFormatter,
        timeFormatter
    ) {
        TaskViewerConfig(
            containerColor = containerColor,
            contentColor = contentColor,
            autoScrollToBottom = autoScrollToBottom,
            showTaskTimes = showTaskTimes,
            groupSummaryFormatter = groupSummaryFormatter,
            timeFormatter = timeFormatter
        )
    }
}

@Composable
fun TaskViewer(
    reporter: TaskReporter,
    config: TaskViewerConfig = rememberTaskViewerConfig(),
    scrollable: Boolean = true,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(reporter.hasRunningTasks) {
        while (reporter.hasRunningTasks) {
            delay(1000.milliseconds)
            reporter.tick()
        }
    }

    if (scrollable) {

        val state = rememberLazyListState()

        if (config.autoScrollToBottom) {

            val totalEntries = reporter.runtime.size

            LaunchedEffect(totalEntries) {
                if (totalEntries > 0) {
                    state.animateScrollToItem(
                        index = reporter.plan.children.lastIndex,
                        scrollOffset = Int.MAX_VALUE
                    )
                }
            }
        }

        LazyColumn(
            modifier = modifier,
            state = state,
            verticalArrangement = Arrangement.spacedBy(
                MaterialTheme.spacing.small
            )
        ) {
            items(
                items = reporter.plan.children,
                key = { it.id }
            ) { node ->

                TaskItem(
                    node = node,
                    reporter = reporter,
                    level = 0,
                    viewerConfig = config,
                    config = reporter.config,
                    onToggleExpanded = reporter::toggleExpanded
                )
            }
        }
    } else {

        Column(
            modifier = modifier.animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(
                MaterialTheme.spacing.small
            )
        ) {

            reporter.plan.children.forEach { node ->

                TaskItem(
                    node = node,
                    reporter = reporter,
                    level = 0,
                    viewerConfig = config,
                    config = reporter.config,
                    onToggleExpanded = reporter::toggleExpanded
                )
            }
        }
    }
}

@Composable
fun TaskViewerContainer(
    reporter: TaskReporter,
    config: TaskViewerConfig = rememberTaskViewerConfig(),
    reset: String = "Neu starten",
    scrollable: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
    ) {

        AnimatedVisibility(
            !reporter.hasRunningTasks &&
                    !reporter.hasFinishedTasks
        ) {
            content()
        }

        AnimatedVisibility(
            reporter.hasRunningTasks ||
                    reporter.hasFinishedTasks
        ) {

            MyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {

                TaskViewer(
                    reporter = reporter,
                    config = config,
                    modifier =
                        if (scrollable) {
                            Modifier.weight(1f)
                        } else {
                            Modifier
                        },
                    scrollable = scrollable,
                )

                if (
                    reporter.hasFinishedTasks &&
                    !reporter.hasRunningTasks
                ) {

                    MyTextButton(
                        modifier = Modifier.align(
                            Alignment.CenterHorizontally
                        ),
                        text = reset
                    ) {
                        reporter.reset()
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskItem(
    node: TaskPlanExecutable,
    reporter: TaskReporter,
    level: Int,
    viewerConfig: TaskViewerConfig,
    config: TaskReporter.Config,
    onToggleExpanded: (String) -> Unit,
) {
    when (node) {

        is TaskPlanTask -> {
            TaskItemTask(
                task = node,
                reporter = reporter,
                level = level,
                viewerConfig = viewerConfig,
                config = config,
                onToggleExpanded = onToggleExpanded,
            )
        }

        is TaskPlanGroup -> {
            TaskItemGroup(
                group = node,
                reporter = reporter,
                level = level,
                viewerConfig = viewerConfig,
                config = config,
                onToggleExpanded = onToggleExpanded,
            )
        }
    }
}

@Composable
private fun TaskItemTask(
    task: TaskPlanTask,
    reporter: TaskReporter,
    level: Int,
    viewerConfig: TaskViewerConfig,
    config: TaskReporter.Config,
    onToggleExpanded: (String) -> Unit,
) {
    val runtime = reporter.runtime[task.id] as? TaskReporter.TaskRuntimeTask ?: return
    val expandable = runtime.messages.isNotEmpty()
    val expanded = runtime.expanded

    Column(
        modifier = Modifier.padding(
            start = if (level == 0) {
                0.dp
            } else {
                TASK_INDENT_PER_LEVEL + ICON_SPACING
            }
        )
    ) {

        TaskItemContainer(
            expanded = expanded,
            expandable = expandable,
            running = runtime.status == TaskStatus.Running,
            config = config,
            viewerConfig = viewerConfig,
            onToggleExpanded = { onToggleExpanded(task.id) }
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = TASK_HORIZONTAL_PADDING,
                    vertical = TASK_VERTICAL_PADDING,
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Box(
                    modifier = Modifier.size(EXPAND_ICON_SIZE),
                    contentAlignment = Alignment.Center,
                ) {
                    if (expandable) {
                        val rotation by animateFloatAsState(targetValue = if (expanded) 90f else 0f)
                        Icon(
                            modifier = Modifier
                                .size(EXPAND_ICON_SIZE)
                                .graphicsLayer { rotationZ = rotation },
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                        )
                    }
                }

                Spacer(Modifier.width(ICON_SPACING))

                StatusIcon(
                    status = runtime.status,
                    viewerConfig = viewerConfig
                )

                Spacer(Modifier.width(ICON_SPACING))

                Column(
                    modifier = Modifier.weight(1f),
                ) {

                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    runtime.subtitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = LocalContentColor.current.copy(alpha = .8f),
                        )
                    }
                }

                if (viewerConfig.showTaskTimes) {

                    Spacer(Modifier.width(ICON_SPACING))

                    val millis = runtime.durationMs(reporter.nowMs)
                    Text(
                        text = viewerConfig.timeFormatter(
                            runtime.isFinished,
                            millis,
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = LocalContentColor.current.copy(alpha = .6f),
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = expanded && expandable,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {

            Column(
                modifier = Modifier.padding(
                    vertical = MaterialTheme.spacing.default,
                ),
                verticalArrangement = Arrangement.spacedBy(
                    MaterialTheme.spacing.small,
                ),
            ) {

                runtime.messages.forEach { message ->

                    MessageItem(
                        modifier = Modifier.padding(
                            start = MESSAGE_INDENT,
                        ),
                        message = message,
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskItemGroup(
    group: TaskPlanGroup,
    reporter: TaskReporter,
    level: Int,
    viewerConfig: TaskViewerConfig,
    config: TaskReporter.Config,
    onToggleExpanded: (String) -> Unit,
) {
    val runtime = reporter.runtime[group.id] ?: return
    val expandable = group.children.isNotEmpty()
    val expanded = runtime.expanded

    Column(
        modifier = Modifier.padding(
            start = if (level == 0) {
                0.dp
            } else {
                TASK_INDENT_PER_LEVEL + ICON_SPACING
            }
        )
    ) {
        TaskItemContainer(
            expanded = expanded,
            expandable = expandable,
            running = false,
            config = config,
            viewerConfig = viewerConfig,
            onToggleExpanded = { onToggleExpanded(group.id) }
        ) {

            Row(
                modifier = Modifier.padding(
                    horizontal = TASK_HORIZONTAL_PADDING,
                    vertical = TASK_VERTICAL_PADDING
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier.size(EXPAND_ICON_SIZE),
                    contentAlignment = Alignment.Center
                ) {

                    if (expandable) {
                        Icon(
                            modifier = Modifier.size(EXPAND_ICON_SIZE),
                            imageVector =
                                if (expanded) Icons.Default.ExpandMore
                                else Icons.Default.ChevronRight,
                            contentDescription = null,
                        )
                    }
                }

                val summary = remember(
                    group,
                    reporter.runtime,
                ) {
                    reporter.getSummary(group)
                }
                val status = summary.toStatus()

                Spacer(Modifier.width(ICON_SPACING))

                StatusIcon(
                    status = status,
                    viewerConfig = viewerConfig
                )

                Spacer(Modifier.width(ICON_SPACING))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = group.title,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    val summary =
                        viewerConfig.groupSummaryFormatter(summary).takeIf { it.isNotEmpty() }
                    summary?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = LocalContentColor.current.copy(alpha = .7f)
                        )
                    }
                }

                Spacer(Modifier.width(ICON_SPACING))

                Text(
                    text = viewerConfig.timeFormatter(
                        runtime.isFinished,
                        runtime.durationMs(reporter.nowMs)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalContentColor.current.copy(alpha = .6f)
                )
            }
        }

        AnimatedVisibility(
            visible = expanded && expandable,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {

            Column(
                modifier = Modifier.padding(
                    vertical = MaterialTheme.spacing.default
                ),
                verticalArrangement = Arrangement.spacedBy(
                    MaterialTheme.spacing.small
                )
            ) {

                group.children.forEach { child ->
                    TaskItem(
                        node = child,
                        reporter = reporter,
                        level = level + 1,
                        viewerConfig = viewerConfig,
                        config = config,
                        onToggleExpanded = onToggleExpanded,
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskItemContainer(
    expanded: Boolean,
    expandable: Boolean,
    running: Boolean,
    config: TaskReporter.Config,
    viewerConfig: TaskViewerConfig,
    onToggleExpanded: () -> Unit,
    content: @Composable () -> Unit,
) {
    /*
    val highlighted = config.expandSinglePathOnly && expanded
    val borderColor by animateColorAsState(
        targetValue = when {
            highlighted -> MaterialTheme.colorScheme.primary
            running -> MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
            else -> Color.Transparent
        }
    )
    val borderWidth by animateDpAsState(
        targetValue = when {
            highlighted -> 2.dp
            running -> 1.dp
            else -> 0.dp
        }
    )*/

    val borderColor = Color.Transparent
    val borderWidth = 0.dp

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            if (expandable) {
                onToggleExpanded()
            }
        },
        border = if (
            borderWidth > 0.dp
        ) {
            BorderStroke(
                width = borderWidth,
                color = borderColor
            )
        } else {
            null
        },
        colors = CardDefaults.cardColors(
            containerColor = viewerConfig.containerColor,
            contentColor = viewerConfig.contentColor
        )
    ) {
        content()
    }
}

@Composable
private fun StatusIcon(
    status: TaskStatus,
    viewerConfig: TaskViewerConfig,
) {
    when (status) {

        TaskStatus.Running -> {
            CircularProgressIndicator(
                modifier = Modifier.size(STATUS_ICON_SIZE),
                strokeWidth = 2.dp,
                color = LocalContentColor.current,
                trackColor = LocalContentColor.current.copy(alpha = 0.2f)
            )
        }

        is TaskStatus.Success -> {
            Icon(
                modifier = Modifier.size(STATUS_ICON_SIZE),
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = colorSuccess(viewerConfig.containerColor)
            )
        }

        is TaskStatus.Warning -> {
            Icon(
                modifier = Modifier.size(STATUS_ICON_SIZE),
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = colorWarning(viewerConfig.containerColor)
            )
        }

        is TaskStatus.Error -> {
            Icon(
                modifier = Modifier.size(STATUS_ICON_SIZE),
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = colorError(viewerConfig.containerColor)
            )
        }
    }
}

@Composable
private fun MessageItem(
    modifier: Modifier,
    message: TaskMessage,
) {
    val color = when (message.type) {
        TaskMessage.Type.Info -> LocalContentColor.current
        TaskMessage.Type.Warning -> colorWarning()
        TaskMessage.Type.Error -> colorError()
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            color = color,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(MESSAGE_BULLET_WIDTH)
        )

        Text(
            modifier = Modifier.weight(1f),
            text = message.text,
            color = color,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun colorWarning(
    background: Color = MaterialTheme.colorScheme.background,
) = if (background.isDark()) {
    Color(0xFFFFB74D) // Orange 300
} else {
    Color(0xFFF57C00) // Orange 700
}

@Composable
fun colorError(
    background: Color = MaterialTheme.colorScheme.background,
) = if (background.isDark()) {
    Color(0xFFE57373) // Red 300
} else {
    Color(0xFFD32F2F) // Red 700
}

@Composable
fun colorSuccess(
    background: Color = MaterialTheme.colorScheme.background,
) = if (background.isDark()) {
    Color(0xFF81C784) // Green 300
} else {
    Color(0xFF388E3C) // Green 700
}