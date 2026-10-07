package com.michaelflisar.demo.pages.tests

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.kmp.platformcontext.PlatformIO
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.components.MyButton
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.tasks.execution.TaskExecutionConfig
import com.michaelflisar.toolbox.tasks.ui.state.TaskViewState
import com.michaelflisar.toolbox.tasks.execution.TaskResult
import com.michaelflisar.toolbox.tasks.execution.rememberTaskExecutionConfig
import com.michaelflisar.toolbox.tasks.plan.TaskPlan
import com.michaelflisar.toolbox.tasks.plan.taskPlan
import com.michaelflisar.toolbox.tasks.ui.state.rememberTaskViewState
import com.michaelflisar.toolbox.tasks.ui.TaskViewerContainer
import com.michaelflisar.toolbox.tasks.ui.rememberTaskViewStateConfig
import com.michaelflisar.toolbox.tasks.ui.rememberTaskViewerConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

@Parcelize
object PageTestTaskViewer : NavScreen() {

    @Composable
    override fun provideData() = rememberNavScreenData(
        name = "Task Viewer",
        icon = Icons.Default.Task.toIconComposable()
    )

    @Composable
    override fun Screen() {
        Page()
    }

}

@Composable
private fun Page() {

    val scope = rememberCoroutineScope()

    val plan = remember { createTestPlan() }
    val viewStateConfig = rememberTaskViewStateConfig(
        autoExpandNewTasks = true,
        expandRunningTasks = true
    )
    val viewState = rememberTaskViewState(
        config = viewStateConfig
    )
    val viewerConfig = rememberTaskViewerConfig(
        //containerColor = MaterialTheme.colorScheme.primaryContainer, // MaterialTheme.colorScheme.surfaceContainerHighest,
        //contentColor =  MaterialTheme.colorScheme.onPrimaryContainer, // MaterialTheme.colorScheme.onSurface,
        autoScrollToBottom = true,
        showTaskTimes = true,
        expandSinglePathOnly = true
    )
    val executionConfig = rememberTaskExecutionConfig(
        errorBehavior = TaskExecutionConfig.ErrorBehavior.StopRootGroup
    )

    TaskViewerContainer(
        plan = plan,
        state = viewState,
        config = viewerConfig,
        modifier = Modifier.fillMaxSize().padding(all = 8.dp)
    ) {
        MyButton(
            onClick = {
                scope.launch { runTest(plan, viewState, executionConfig) }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Run Test")
        }
    }
}

private fun createTestPlan(
    taskDurationMs: Long = 50,
): TaskPlan {
    suspend fun pause() {
        delay(taskDurationMs.milliseconds)
    }

    val plan = taskPlan {

        task("Initialize") {
            addInfo("Initializing test plan")
            pause()
            TaskResult.Success("Initialization completed")
        }

        group("Deploy Test System") {

            task("Copy Files") {

                repeat(30) {
                    setStatus("${it + 1}/30 files")
                    addInfo("Copied file_${it + 1}.dat")
                    pause()
                }

                TaskResult.Success("Copied 30 files")
            }

            task("Longer task") {
                repeat(10) {
                    setStatus("Status $it...")
                    delay(1000.milliseconds)
                }
                TaskResult.Success("Longer task completed")
            }

            task("Move Files") {

                repeat(20) {
                    addInfo("Moved document_${it + 1}.pdf")
                    pause()
                }

                TaskResult.Success("Moved 20 files")
            }

            task("Check Online Users") {

                listOf(
                    "SERVER01" to true,
                    "SERVER02" to true,
                    "SERVER03" to false,
                    "SERVER04" to true,
                    "SERVER05" to false,
                ).forEach { (server, online) ->

                    addInfo(
                        if (online) {
                            "$server is online"
                        } else {
                            "$server is offline"
                        }
                    )

                    pause()
                }

                TaskResult.Success("Checked 5 servers")
            }

            task("Create ZIP") {

                repeat(20) {
                    setStatus("${it + 1}/20 files")
                    addInfo("Added file_${it + 1}.dat to deployment.zip")
                    pause()
                }

                TaskResult.Success("Created deployment.zip with 20 files")
            }

            task("Transfer ZIP") {

                addInfo("deployment.zip -> \\\\SERVER01\\Deploy")
                pause()

                addInfo("Transfer completed")
                pause()

                TaskResult.Success("Transferred deployment.zip to \\\\SERVER01\\Deploy")
            }

            group("Extract ZIP") {

                task("Extract Sub ZIP 1") {

                    repeat(20) {
                        setStatus("${it + 1}/20 files")
                        addInfo("Extracted file_${it + 1}.dat")
                        pause()
                    }

                    TaskResult.Success("Extracted 20 files from sub ZIP 1")
                }

                task("Extract Sub ZIP 2") {

                    repeat(20) {
                        setStatus("${it + 1}/20 files")
                        addInfo("Extracted file_${it + 1}.dat")
                        pause()
                    }

                    TaskResult.Success("Extracted 20 files from sub ZIP 2")
                }
            }

            task("Delete ZIP") {

                addInfo("Deleting deployment.zip")
                pause()

                addInfo("deployment.zip removed")
                pause()

                TaskResult.Warning("Test warning")
            }

            task("Error Example") {
                addInfo("Doing something")
                pause()
                TaskResult.Error(Exception("Something went wrong"))
            }

            task("Final Task") {
                addInfo("Finalizing deployment")
                pause()
                TaskResult.Success("Deployment finalized")
            }
        }

        group("Deploy Test System 2 (EMPTY)") {

        }
    }

    return plan
}

private suspend fun runTest(
    plan: TaskPlan,
    viewState: TaskViewState,
    executionConfig: TaskExecutionConfig,
) {
    withContext(Dispatchers.PlatformIO) {
        viewState.reset()
        plan.execute(executionConfig, viewState)
    }
}