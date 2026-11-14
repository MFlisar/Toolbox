package com.michaelflisar.demo.pages.tests

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.composestyled.adaptive.StyledAdaptiveInfo
import com.michaelflisar.composestyled.ui.components.StyledButton
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.demo.pages.DemoToolbarConfig
import com.michaelflisar.toolbox.app.features.navigation.NavScreen
import com.michaelflisar.toolbox.tasks.execution.TaskResult
import com.michaelflisar.toolbox.tasks.plan.taskPlan
import com.michaelflisar.toolbox.tasks.rememberTaskConfig
import com.michaelflisar.toolbox.tasks.ui.TaskViewerContainer
import com.michaelflisar.toolbox.tasks.ui.state.rememberTaskViewState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data object PageTestTaskViewer : NavScreen {

    @Composable
    override fun Content(
        paddingValues: PaddingValues,
        adaptiveInfo: State<StyledAdaptiveInfo>,
    ) = PageTestTaskViewerContent(paddingValues)

    @Composable
    override fun Toolbar() = DemoToolbarConfig("Task Viewer")
}

@Composable
private fun PageTestTaskViewerContent(paddingValues: PaddingValues) {
    val scope = rememberCoroutineScope()
    val plan = remember {
        taskPlan("Demo task plan") {
            task("Prepare") {
                TaskResult.Success("Ready")
            }
            group("Work") {
                task("Process data") {
                    delay(250)
                    TaskResult.Success("Processed")
                }
                task("Finish") {
                    TaskResult.Success("Done")
                }
            }
        }
    }
    val config = rememberTaskConfig(expandSinglePathOnly = false)
    val viewState = rememberTaskViewState(config.viewState)

    TaskViewerContainer(
        plan = plan,
        state = viewState,
        config = config.viewer,
        modifier = Modifier.fillMaxSize().padding(paddingValues).padding(8.dp),
    ) {
        StyledButton(onClick = { scope.launch { viewState.execute(plan, config) } }) {
            StyledText("Run test plan")
        }
    }
}
