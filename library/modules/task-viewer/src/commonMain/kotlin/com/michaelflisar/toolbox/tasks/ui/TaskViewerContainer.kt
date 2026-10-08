package com.michaelflisar.toolbox.tasks.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.components.MyTextButton
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
                MyColumn(
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