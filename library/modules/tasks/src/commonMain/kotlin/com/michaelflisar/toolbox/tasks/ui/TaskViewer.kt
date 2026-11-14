package com.michaelflisar.toolbox.tasks.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
import com.michaelflisar.toolbox.tasks.plan.TaskMessage
import com.michaelflisar.toolbox.tasks.plan.TaskMessageType
import com.michaelflisar.toolbox.tasks.plan.TaskPlan
import com.michaelflisar.toolbox.tasks.plan.TaskPlanExecutable
import com.michaelflisar.toolbox.tasks.plan.TaskPlanGroup
import com.michaelflisar.toolbox.tasks.plan.TaskPlanTask
import com.michaelflisar.toolbox.tasks.ui.parts.ProgressIcon
import com.michaelflisar.toolbox.tasks.ui.state.TaskViewState
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun TaskViewer(
    plan: TaskPlan,
    state: TaskViewState,
    config: TaskConfig.ViewerConfig,
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
private fun TaskItem(
    node: TaskPlanExecutable,
    state: TaskViewState,
    level: Int,
    number: String,
    viewerConfig: TaskConfig.ViewerConfig,
    config: TaskConfig.ViewStateConfig,
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
    viewerConfig: TaskConfig.ViewerConfig,
    config: TaskConfig.ViewStateConfig,
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
            group = false,
            expandable = expandable,
            viewerConfig = viewerConfig,
            onToggleExpanded = { onToggleExpanded(task.id) }
        )
        {
            TaskItemHeader(
                group = false,
                title = task.title,
                number = number,
                subtitle = if (runtime.status == TaskStatus.Cancelled) {
                    runtime.subtitle ?: viewerConfig.skippedTaskSubtitle
                } else {
                    runtime.subtitle
                },
                status = runtime.status,
                expanded = expanded,
                expandable = expandable,
                isFinished = runtime.isFinished,
                durationMs = runtime.durationMs(state.nowMs),
                viewerConfig = viewerConfig,
            )
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

                val numberStyle =
                    StyledTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
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
    viewerConfig: TaskConfig.ViewerConfig,
    config: TaskConfig.ViewStateConfig,
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
            group = true,
            expandable = expandable,
            viewerConfig = viewerConfig,
            onToggleExpanded = { onToggleExpanded(group.id) },
        ) {
            val summary by remember(group, state) {
                derivedStateOf { state.getSummary(group) }
            }
            TaskItemHeader(
                group = true,
                title = group.title,
                number = number,
                subtitle = viewerConfig.groupSummaryFormatter(summary).takeIf { it.isNotEmpty() },
                status = if (runtime.skipped) TaskStatus.Cancelled else summary.toStatus(),
                expanded = expanded,
                expandable = expandable,
                isFinished = runtime.isFinished,
                durationMs = runtime.durationMs(state.nowMs),
                viewerConfig = viewerConfig,
            )
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
private fun TaskItemHeader(
    group: Boolean,
    title: String,
    number: String,
    subtitle: String?,
    status: TaskStatus,
    expanded: Boolean,
    expandable: Boolean,
    isFinished: Boolean,
    durationMs: Long,
    viewerConfig: TaskConfig.ViewerConfig,
) {
    val layout = viewerConfig.layout

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
                StyledIcon(
                    modifier = Modifier
                        .size(layout.expandIconSize)
                        .graphicsLayer { rotationZ = rotation },
                    icon = Icons.Default.ChevronRight.toIconComposable(),
                    contentDescription = null,
                )
            }
        }

        Spacer(Modifier.width(layout.iconSpacing))

        StatusIcon(
            group = group,
            status = status,
            viewerConfig = viewerConfig,
            size = layout.statusIconSize,
        )

        Spacer(Modifier.width(layout.iconSpacing))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            StyledText(
                text = if (viewerConfig.showHeaderNumbers) "$number $title" else title,
                style = StyledTheme.typography.bodyMedium,
            )
            subtitle?.let {
                StyledText(
                    text = it,
                    style = StyledTheme.typography.bodySmall,
                    color = LocalStyledContentColor.current.copy(alpha = if (group) .7f else .8f),
                )
            }
        }

        if (group || viewerConfig.showTaskTimes) {
            Spacer(Modifier.width(layout.iconSpacing))

            StyledText(
                text = viewerConfig.timeFormatter(isFinished, durationMs),
                style = StyledTheme.typography.bodySmall,
                color = LocalStyledContentColor.current.copy(alpha = .6f),
            )
        }
    }
}

@Composable
private fun TaskItemContainer(
    group: Boolean,
    expandable: Boolean,
    viewerConfig: TaskConfig.ViewerConfig,
    onToggleExpanded: () -> Unit,
    content: @Composable () -> Unit,
) {
    val containerColor =
        if (group) viewerConfig.groupContainerColor else viewerConfig.taskContainerColor
    val contentColor =
        if (group) viewerConfig.groupContentColor else viewerConfig.taskContentColor

    StyledCard(
        modifier = Modifier.fillMaxWidth(),
        variant = StyledCard.Variant.Filled,
        onClick = if (expandable) onToggleExpanded else null,
        contentPadding = PaddingValues(vertical = StyledTheme.padding.small),
        customization = StyledCardDefaults.customize(
            colors = CustomColors(
                filledBackground = containerColor,
                filledForeground = contentColor,
                textForeground = contentColor,
            ),
        ),
    ) {
        content()
    }
}

@Composable
private fun StatusIcon(
    group: Boolean,
    status: TaskStatus,
    viewerConfig: TaskConfig.ViewerConfig,
    size: Dp,
) {
    when (status) {

        TaskStatus.Running -> {
            ProgressIcon(size)
        }

        TaskStatus.Cancelled -> {
            StyledIcon(
                modifier = Modifier.size(size),
                icon = Icons.Default.Cancel.toIconComposable(),
                contentDescription = null,
                tint = LocalStyledContentColor.current.copy(alpha = .8f),
            )
        }

        is TaskStatus.Success -> {
            StyledIcon(
                modifier = Modifier.size(size),
                icon = Icons.Default.CheckCircle.toIconComposable(),
                contentDescription = null,
                tint = viewerConfig.taskMessageColors.colorSuccess(if (group) viewerConfig.groupContainerColor else viewerConfig.taskContainerColor)
            )
        }

        is TaskStatus.Info -> {
            StyledIcon(
                modifier = Modifier.size(size),
                icon = Icons.Default.Info.toIconComposable(),
                contentDescription = null,
                tint = viewerConfig.taskMessageColors.colorInfo(if (group) viewerConfig.groupContainerColor else viewerConfig.taskContainerColor)
            )
        }

        is TaskStatus.Warning -> {
            StyledIcon(
                modifier = Modifier.size(size),
                icon = Icons.Default.Warning.toIconComposable(),
                contentDescription = null,
                tint = viewerConfig.taskMessageColors.colorWarning(if (group) viewerConfig.groupContainerColor else viewerConfig.taskContainerColor)
            )
        }

        is TaskStatus.Error -> {
            StyledIcon(
                modifier = Modifier.size(size),
                icon = Icons.Default.Error.toIconComposable(),
                contentDescription = null,
                tint = viewerConfig.taskMessageColors.colorError(if (group) viewerConfig.groupContainerColor else viewerConfig.taskContainerColor)
            )
        }
    }
}

@Composable
private fun MessageItem(
    viewerConfig: TaskConfig.ViewerConfig,
    modifier: Modifier,
    message: TaskMessage,
    bulletWidth: Dp,
    number: Int?,
    numberSpacing: Dp,
) {
    val bulletColor = if (number != null) {
        LocalStyledContentColor.current.copy(alpha = .6f)
    } else LocalStyledContentColor.current

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        StyledText(
            text = number?.toString() ?: "•",
            color = bulletColor,
            style = StyledTheme.typography.bodySmall,
            textAlign = if (number != null) TextAlign.End else TextAlign.Start,
            modifier = Modifier.width(bulletWidth)
        )

        if (number != null) {
            Spacer(Modifier.width(numberSpacing))
        }

        when (message) {
            is TaskMessage.Text -> StyledText(
                modifier = Modifier.weight(1f),
                text = message.annotated(
                    viewerConfig,
                    StyledTheme.colorScheme.background,
                ),
                style = StyledTheme.typography.bodySmall,
            )

            is TaskMessage.Rich -> StyledText(
                modifier = Modifier.weight(1f),
                text = message.text,
                style = StyledTheme.typography.bodySmall,
            )

            is TaskMessage.ItemList -> Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(viewerConfig.layout.expandedContentSpacing),
            ) {
                val prefix = when (message.type) {
                    TaskMessageType.Info -> ""
                    TaskMessageType.Warning -> viewerConfig.prefixWarning
                    TaskMessageType.Error -> viewerConfig.prefixError
                }
                if (message.title != null || prefix.isNotEmpty()) {
                    StyledText(
                        text = TaskMessage.Text(
                            text = message.title.orEmpty(),
                            type = message.type,
                        ).annotated(viewerConfig, StyledTheme.colorScheme.background),
                        style = StyledTheme.typography.bodySmall,
                    )
                }
                message.items.forEach { item ->
                    MessageItem(
                        viewerConfig = viewerConfig,
                        modifier = Modifier,
                        message = TaskMessage.Text(item),
                        bulletWidth = viewerConfig.layout.messageBulletWidth,
                        number = null,
                        numberSpacing = numberSpacing,
                    )
                }
            }
        }
    }
}