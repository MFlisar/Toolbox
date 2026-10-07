package com.michaelflisar.toolbox.tasks.ui

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
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
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.components.MyTextButton
import com.michaelflisar.toolbox.tasks.execution.TaskStatus
import com.michaelflisar.toolbox.tasks.plan.TaskMessage
import com.michaelflisar.toolbox.tasks.plan.TaskMessageType
import com.michaelflisar.toolbox.tasks.plan.TaskPlan
import com.michaelflisar.toolbox.tasks.plan.TaskPlanExecutable
import com.michaelflisar.toolbox.tasks.plan.TaskPlanGroup
import com.michaelflisar.toolbox.tasks.plan.TaskPlanTask
import com.michaelflisar.toolbox.tasks.ui.state.TaskViewState
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun TaskViewer(
    plan: TaskPlan,
    state: TaskViewState,
    config: TaskViewerConfig = rememberTaskViewerConfig(),
    scrollable: Boolean = true,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(state.hasRunningTasks) {
        while (state.hasRunningTasks) {
            delay(1000.milliseconds)
            state.tick()
        }
    }

    if (scrollable) {

        val listState = rememberLazyListState()
        val startedNodes =
            plan.children.withIndex().filter { state.getNodeState(it.value.id) != null }
        val onManualExpansion = rememberTaskAutoScroll(
            listState = listState,
            execution = startedNodes.firstOrNull()?.let { state.getNodeState(it.value.id) },
            enabled = config.autoScrollToBottom,
        )

        LazyColumn(
            modifier = modifier,
            state = listState,
            verticalArrangement = Arrangement.spacedBy(
                config.layout.itemSpacing
            )
        ) {
            items(
                items = startedNodes,
                key = { it.value.id }
            ) { (index, node) ->

                TaskItem(
                    node = node,
                    state = state,
                    level = 0,
                    number = "${index + 1}",
                    viewerConfig = config,
                    config = state.config,
                    onToggleExpanded = {
                        onManualExpansion()
                        state.toggleExpanded(plan, it, config.expandSinglePathOnly)
                    }
                )
            }
        }
    } else {

        Column(
            modifier = modifier.animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(
                config.layout.itemSpacing
            )
        ) {

            plan.children.forEachIndexed { index, node ->

                TaskItem(
                    node = node,
                    state = state,
                    level = 0,
                    number = "${index + 1}",
                    viewerConfig = config,
                    config = state.config,
                    onToggleExpanded = {
                        state.toggleExpanded(plan, it, config.expandSinglePathOnly)
                    }
                )
            }
        }
    }
}

@Composable
fun TaskViewerContainer(
    plan: TaskPlan?,
    state: TaskViewState,
    config: TaskViewerConfig = rememberTaskViewerConfig(),
    reset: String = "Neu starten",
    scrollable: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val showTasks = plan != null && (state.hasRunningTasks || state.hasFinishedTasks)

    Column(
        modifier = modifier
    ) {

        AnimatedVisibility(
            !showTasks
        ) {
            content()
        }

        AnimatedVisibility(
            showTasks
        ) {
            if (plan != null) {
                MyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    TaskViewer(
                        plan = plan,
                        state = state,
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
                        state.hasFinishedTasks &&
                        !state.hasRunningTasks
                    ) {

                        MyTextButton(
                            modifier = Modifier.align(
                                Alignment.CenterHorizontally
                            ),
                            text = reset
                        ) {
                            state.reset()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskItem(
    node: TaskPlanExecutable,
    state: TaskViewState,
    level: Int,
    number: String,
    viewerConfig: TaskViewerConfig,
    config: TaskViewStateConfig,
    onToggleExpanded: (String) -> Unit,
) {
    when (node) {

        is TaskPlanTask -> {
            TaskItemTask(
                task = node,
                state = state,
                level = level,
                number = number,
                viewerConfig = viewerConfig,
                config = config,
                onToggleExpanded = onToggleExpanded,
            )
        }

        is TaskPlanGroup -> {
            TaskItemGroup(
                group = node,
                state = state,
                level = level,
                number = number,
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
    state: TaskViewState,
    level: Int,
    number: String,
    viewerConfig: TaskViewerConfig,
    config: TaskViewStateConfig,
    onToggleExpanded: (String) -> Unit,
) {
    val runtime = state.getTaskState(task.id) ?: return
    val expandable = runtime.messages.isNotEmpty()
    val expanded = runtime.expanded
    val layout = viewerConfig.layout

    Column(
        modifier = Modifier.padding(
            start = if (level == 0) {
                0.dp
            } else {
                layout.indentPerLevel
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
        )
        {
            Row(
                modifier = Modifier
                    .heightIn(min = layout.minItemHeight)
                    .padding(
                        horizontal = layout.horizontalPadding,
                        vertical = layout.verticalPadding,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Box(
                    modifier = Modifier.size(layout.expandIconSize),
                    contentAlignment = Alignment.Center,
                ) {
                    if (expandable) {
                        val rotation by animateFloatAsState(targetValue = if (expanded) 90f else 0f)
                        Icon(
                            modifier = Modifier
                                .size(layout.expandIconSize)
                                .graphicsLayer { rotationZ = rotation },
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                        )
                    }
                }

                Spacer(Modifier.width(layout.iconSpacing))

                StatusIcon(
                    status = runtime.status,
                    viewerConfig = viewerConfig,
                    size = layout.statusIconSize,
                )

                Spacer(Modifier.width(layout.iconSpacing))

                Column(
                    modifier = Modifier.weight(1f),
                ) {

                    Text(
                        text = if (viewerConfig.showHeaderNumbers) "$number ${task.title}" else task.title,
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    val subtitle = if (runtime.status == TaskStatus.Cancelled) {
                        viewerConfig.skippedTaskSubtitle
                    } else {
                        runtime.subtitle
                    }
                    subtitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = LocalContentColor.current.copy(alpha = .8f),
                        )
                    }
                }

                if (viewerConfig.showTaskTimes) {

                    Spacer(Modifier.width(layout.iconSpacing))

                    val millis = runtime.durationMs(state.nowMs)
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
                    vertical = layout.expandedContentVerticalPadding,
                ),
                verticalArrangement = Arrangement.spacedBy(
                    layout.expandedContentSpacing,
                ),
            ) {

                val numberStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                val measureMarkerWidth = true // genau auf Anzahl der Ziffern messen?
                val markerWidth = if (viewerConfig.showMessageNumbers && measureMarkerWidth) {
                    val textMeasurer = rememberTextMeasurer()
                    val width = textMeasurer.measure(
                        text = "0".repeat(runtime.messages.size.toString().length),
                        style = numberStyle,
                        softWrap = false,
                    ).size.width
                    with(LocalDensity.current) { width.toDp() }
                } else {
                    layout.messageBulletWidth
                }

                runtime.messages.forEachIndexed { index, message ->

                    MessageItem(
                        viewerConfig = viewerConfig,
                        modifier = Modifier.padding(
                            start = layout.horizontalPadding +
                                    layout.expandIconSize +
                                    layout.iconSpacing +
                                    layout.statusIconSize +
                                    layout.iconSpacing,
                        ),
                        message = message,
                        bulletWidth = markerWidth,
                        number = if (viewerConfig.showMessageNumbers) index + 1 else null,
                        numberSpacing = layout.messageNumberSpacing,
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskItemGroup(
    group: TaskPlanGroup,
    state: TaskViewState,
    level: Int,
    number: String,
    viewerConfig: TaskViewerConfig,
    config: TaskViewStateConfig,
    onToggleExpanded: (String) -> Unit,
) {
    val runtime = state.getGroupState(group.id) ?: return
    val expandable = group.children.isNotEmpty()
    val expanded = runtime.expanded
    val layout = viewerConfig.layout

    Column(
        modifier = Modifier.padding(
            start = if (level == 0) {
                0.dp
            } else {
                layout.indentPerLevel
            }
        )
    ) {
        TaskItemContainer(
            expanded = expanded,
            expandable = expandable,
            running = false,
            config = config,
            viewerConfig = viewerConfig,
            onToggleExpanded = { onToggleExpanded(group.id) },
        ) {

            Row(
                modifier = Modifier
                    .heightIn(min = layout.minItemHeight)
                    .padding(
                        horizontal = layout.horizontalPadding,
                        vertical = layout.verticalPadding
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier.size(layout.expandIconSize),
                    contentAlignment = Alignment.Center
                ) {

                    if (expandable) {
                        Icon(
                            modifier = Modifier.size(layout.expandIconSize),
                            imageVector =
                                if (expanded) Icons.Default.ExpandMore
                                else Icons.Default.ChevronRight,
                            contentDescription = null,
                        )
                    }
                }

                val summary by remember(group, state) {
                    derivedStateOf { state.getSummary(group) }
                }
                val status = if (runtime.skipped) TaskStatus.Cancelled else summary.toStatus()

                Spacer(Modifier.width(layout.iconSpacing))

                StatusIcon(
                    status = status,
                    viewerConfig = viewerConfig,
                    size = layout.statusIconSize,
                )

                Spacer(Modifier.width(layout.iconSpacing))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (viewerConfig.showHeaderNumbers) "$number ${group.title}" else group.title,
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

                Spacer(Modifier.width(layout.iconSpacing))

                Text(
                    text = viewerConfig.timeFormatter(
                        runtime.isFinished,
                        runtime.durationMs(state.nowMs)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalContentColor.current.copy(alpha = .6f)
                )
            }
        }

        AnimatedVisibility(
            visible = expanded && expandable,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {

            Column(
                modifier = Modifier.padding(
                    top = layout.itemSpacing
                ),
                verticalArrangement = Arrangement.spacedBy(
                    layout.itemSpacing
                )
            ) {
                group.children.forEachIndexed { index, child ->
                    TaskItem(
                        node = child,
                        state = state,
                        level = level + 1,
                        number = "$number.${index + 1}",
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
    config: TaskViewStateConfig,
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

    CompositionLocalProvider(
        LocalMinimumInteractiveComponentSize provides 0.dp
    ) {
        if (expandable) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onToggleExpanded() },
                border = if (borderWidth > 0.dp) {
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
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = if (borderWidth > 0.dp) {
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
    }
}

@Composable
private fun StatusIcon(
    status: TaskStatus,
    viewerConfig: TaskViewerConfig,
    size: Dp,
) {
    when (status) {

        TaskStatus.Running -> {
            CircularProgressIndicator(
                modifier = Modifier.size(size),
                strokeWidth = 2.dp,
                color = LocalContentColor.current,
                trackColor = LocalContentColor.current.copy(alpha = 0.2f)
            )
        }

        TaskStatus.Cancelled -> {
            Icon(
                modifier = Modifier.size(size),
                imageVector = Icons.Default.Cancel,
                contentDescription = null,
                tint = LocalContentColor.current.copy(alpha = .6f),
            )
        }

        is TaskStatus.Success -> {
            Icon(
                modifier = Modifier.size(size),
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = viewerConfig.taskMessageColors.colorSuccess(viewerConfig.containerColor)
            )
        }

        is TaskStatus.Warning -> {
            Icon(
                modifier = Modifier.size(size),
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = viewerConfig.taskMessageColors.colorWarning(viewerConfig.containerColor)
            )
        }

        is TaskStatus.Error -> {
            Icon(
                modifier = Modifier.size(size),
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = viewerConfig.taskMessageColors.colorError(viewerConfig.containerColor)
            )
        }
    }
}

@Composable
private fun MessageItem(
    viewerConfig: TaskViewerConfig,
    modifier: Modifier,
    message: TaskMessage,
    bulletWidth: Dp,
    number: Int?,
    numberSpacing: Dp,
) {
    val bulletColor = if (number != null) {
        LocalContentColor.current.copy(alpha = .6f)
    } else LocalContentColor.current

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = number?.toString() ?: "•",
            color = bulletColor,
            style = MaterialTheme.typography.bodySmall,
            textAlign = if (number != null) TextAlign.End else TextAlign.Start,
            modifier = Modifier.width(bulletWidth)
        )

        if (number != null) {
            Spacer(Modifier.width(numberSpacing))
        }

        Text(
            modifier = Modifier.weight(1f),
            text = when (message) {
                is TaskMessage.Text -> message.annotated(viewerConfig, MaterialTheme.colorScheme.background)
                is TaskMessage.Rich -> message.text
            },
            style = MaterialTheme.typography.bodySmall
        )
    }
}