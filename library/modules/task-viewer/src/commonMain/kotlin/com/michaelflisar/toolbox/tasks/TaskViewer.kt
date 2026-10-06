package com.michaelflisar.toolbox.tasks

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.MattColors
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.components.MyTextButton
import com.michaelflisar.toolbox.spacing

private val EXPAND_ICON_SIZE = 18.dp
private val STATUS_ICON_SIZE = 16.dp
private val TREE_INDENT = 20.dp

@Stable
data class TaskViewerConfig(
    val colorSuccess: Color,
    val colorWarning: Color,
    val colorError: Color,
)

@Composable
fun rememberTaskViewerConfig(
    colorSuccess: Color = Color(0xFF4CAF50),
    colorWarning: Color = Color(0xFFFFC107),
    colorError: Color = Color(0xFFF44336),
): TaskViewerConfig {
    return remember(colorSuccess, colorWarning, colorError) {
        TaskViewerConfig(
            colorSuccess = colorSuccess,
            colorWarning = colorWarning,
            colorError = colorError
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
        LazyColumn(
            modifier = modifier,
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

    Column {
        TaskItemContainer(
            task = task,
            config = config,
            onToggleExpanded = onToggleExpanded
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            )
            {

                Spacer(
                    modifier = Modifier.width(TREE_INDENT * level)
                )

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

                Spacer(Modifier.width(8.dp))

                StatusIcon(viewerConfig, task.status)

                Spacer(Modifier.width(8.dp))

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

            Row {

                Spacer(
                    modifier = Modifier.width(
                        TREE_INDENT * level + 21.dp
                    )
                )

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                )

                Column(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .padding(vertical = MaterialTheme.spacing.default)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {

                    task.messages.forEach { message ->

                        MessageItem(
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
            Status.Running -> BorderStroke(
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
    status: Status,
) {
    when (status) {

        Status.Running -> {
            CircularProgressIndicator(
                modifier = Modifier.size(STATUS_ICON_SIZE),
                strokeWidth = 2.dp
            )
        }

        is Status.Success -> {
            Icon(
                modifier = Modifier.size(STATUS_ICON_SIZE),
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = config.colorSuccess
            )
        }

        is Status.Warning -> {
            Icon(
                modifier = Modifier.size(STATUS_ICON_SIZE),
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = config.colorWarning
            )
        }

        is Status.Error -> {
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
    config: TaskViewerConfig,
    message: Message,
) {
    val color = when (message.type) {
        MessageType.Info -> LocalContentColor.current
        MessageType.Warning -> config.colorWarning
        MessageType.Error -> config.colorError
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            color = color,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(12.dp)
        )

        Text(
            modifier = Modifier.weight(1f),
            text = message.text,
            color = color,
            style = MaterialTheme.typography.bodySmall
        )
    }
}