package com.michaelflisar.toolbox.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.components.MyTextButton
import com.michaelflisar.toolbox.spacing

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
    val colorSuccess: Color,
    val colorWarning: Color,
    val colorError: Color,
    val autoScrollToBottom: Boolean,
)

@Composable
fun rememberTaskViewerConfig(
    colorSuccess: Color = Color(0xFF4CAF50),
    colorWarning: Color = Color(0xFFFFC107),
    colorError: Color = Color(0xFFF44336),
    autoScrollToBottom: Boolean = true,
): TaskViewerConfig {
    return remember(colorSuccess, colorWarning, colorError) {
        TaskViewerConfig(
            colorSuccess = colorSuccess,
            colorWarning = colorWarning,
            colorError = colorError,
            autoScrollToBottom = autoScrollToBottom
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
    if (scrollable) {
        val state = rememberLazyListState()

        if (config.autoScrollToBottom) {
            val totalEntries = reporter.tasks.sumOf { it.totalEntries }
            LaunchedEffect(totalEntries) {
                if (reporter.tasks.isNotEmpty()) {
                    state.animateScrollToItem(
                        index = reporter.tasks.lastIndex,
                        scrollOffset = Int.MAX_VALUE
                    )
                }
            }
        }

        LazyColumn(
            modifier = modifier,
            state = state,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            items(
                items = reporter.tasks,
                key = { it.id }
            ) { task ->
                TaskItem(
                    task = task,
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
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            reporter.tasks.forEach { task ->
                TaskItem(
                    task = task,
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
        AnimatedVisibility(!reporter.hasRunningTasks && !reporter.hasFinishedTasks) {
            content()
        }
        AnimatedVisibility(reporter.hasRunningTasks || reporter.hasFinishedTasks) {
            MyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                TaskViewer(
                    reporter = reporter,
                    config = config,
                    modifier = if (scrollable) Modifier.weight(1f) else Modifier,
                    scrollable = scrollable
                )
                if (reporter.hasFinishedTasks && !reporter.hasRunningTasks) {
                    MyTextButton(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
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
    task: TaskNode,
    level: Int,
    viewerConfig: TaskViewerConfig,
    config: TaskReporter.Config,
    onToggleExpanded: (String) -> Unit,
) {
    val expandable = task.hasMessages || task.hasChildren
    val expanded = task.expanded

    Column(
        modifier = Modifier.padding(start = if (level == 0) 0.dp else (TASK_INDENT_PER_LEVEL + ICON_SPACING))
    ) {
        TaskItemContainer(
            task = task,
            config = config,
            onToggleExpanded = onToggleExpanded
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = TASK_HORIZONTAL_PADDING,
                    vertical = TASK_VERTICAL_PADDING
                ),
                verticalAlignment = Alignment.CenterVertically
            )
            {
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

                Spacer(Modifier.width(ICON_SPACING))

                StatusIcon(viewerConfig, task.status)

                Spacer(Modifier.width(ICON_SPACING))

                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (task.subtitle != null) {
                        Text(
                            text = task.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = LocalContentColor.current.copy(alpha = .8f)
                        )
                    }
                }

            }
        }

        AnimatedVisibility(
            visible = expanded && expandable,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {

            Column(
                modifier = Modifier.padding(vertical = MaterialTheme.spacing.default),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {

                task.messages.forEach { message ->
                    MessageItem(
                        modifier = Modifier.padding(
                            start = MESSAGE_INDENT
                        ),
                        config = viewerConfig,
                        message = message
                    )
                }

                task.children.forEach { child ->
                    TaskItem(
                        task = child,
                        level = level + 1,
                        viewerConfig = viewerConfig,
                        config = config,
                        onToggleExpanded = onToggleExpanded
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskItemContainer(
    task: TaskNode,
    config: TaskReporter.Config,
    onToggleExpanded: (String) -> Unit,
    content: @Composable () -> Unit,
) {

    val expandable = task.hasMessages || task.hasChildren
    val expanded = task.expanded

    val highlighted = config.expandSinglePathOnly && expanded
    val color = animateColorAsState(
        targetValue =
            if (highlighted) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.background
            }
    )
    val onColor = animateColorAsState(
        targetValue =
            if (highlighted) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onBackground
            }
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            if (expandable) {
                onToggleExpanded(task.id)
            }
        },
        border = when (task.status) {
            TaskStatus.Running -> BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
            )

            else -> null
        },
        colors = CardDefaults.cardColors(
            containerColor = color.value,
            contentColor = onColor.value
        ),
    ) {
        content()
    }
}

@Composable
private fun StatusIcon(
    config: TaskViewerConfig,
    status: TaskStatus,
) {
    when (status) {

        TaskStatus.Running -> {
            CircularProgressIndicator(
                modifier = Modifier.size(STATUS_ICON_SIZE),
                strokeWidth = 2.dp
            )
        }

        is TaskStatus.Success -> {
            Icon(
                modifier = Modifier.size(STATUS_ICON_SIZE),
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = config.colorSuccess
            )
        }

        is TaskStatus.Warning -> {
            Icon(
                modifier = Modifier.size(STATUS_ICON_SIZE),
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = config.colorWarning
            )
        }

        is TaskStatus.Error -> {
            Icon(
                modifier = Modifier.size(STATUS_ICON_SIZE),
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = config.colorError
            )
        }
    }
}

@Composable
private fun MessageItem(
    modifier: Modifier,
    config: TaskViewerConfig,
    message: TaskMessage,
) {
    val color = when (message.type) {
        TaskMessage.Type.Info -> LocalContentColor.current
        TaskMessage.Type.Warning -> config.colorWarning
        TaskMessage.Type.Error -> config.colorError
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