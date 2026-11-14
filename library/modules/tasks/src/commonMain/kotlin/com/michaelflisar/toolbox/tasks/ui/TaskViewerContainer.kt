package com.michaelflisar.toolbox.tasks.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.ui.components.StyledTextButton
import com.michaelflisar.composestyled.ui.layout.StyledColumn
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.toolbox.tasks.TaskConfig
import com.michaelflisar.toolbox.tasks.plan.TaskPlan
import com.michaelflisar.toolbox.tasks.ui.state.TaskViewState

@Composable
fun TaskViewerContainer(
    plan: TaskPlan?,
    state: TaskViewState,
    config: TaskConfig.ViewerConfig,
    reset: String = "Neu starten",
    scrollable: Boolean = true,
    showHeader: Boolean = true,
    modifier: Modifier = Modifier.Companion,
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
                StyledColumn(
                    itemSpacing = StyledTheme.spacing.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    if (showHeader) {
                        TaskViewerHeader(
                            plan = plan,
                            state = state,
                            config = config,
                        )
                    }

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
                        StyledTextButton(
                            onClick = { state.reset() },
                            modifier = Modifier.align(
                                Alignment.CenterHorizontally
                            ),
                        ) {
                            StyledText(reset)
                        }
                    }
                }
            }
        }
    }
}