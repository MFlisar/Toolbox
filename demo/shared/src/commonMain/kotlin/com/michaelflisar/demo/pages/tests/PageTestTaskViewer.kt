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
import com.michaelflisar.toolbox.tasks.TaskPlanRoot
import com.michaelflisar.toolbox.tasks.TaskReporter
import com.michaelflisar.toolbox.tasks.TaskResult
import com.michaelflisar.toolbox.tasks.TaskViewerContainer
import com.michaelflisar.toolbox.tasks.rememberTaskReporter
import com.michaelflisar.toolbox.tasks.rememberTaskReporterConfig
import com.michaelflisar.toolbox.tasks.rememberTaskViewerConfig
import com.michaelflisar.toolbox.tasks.taskPlan
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
    val reporterConfig = rememberTaskReporterConfig(
        autoExpandNewTasks = true,
        expandRunningTasks = true,
        expandSinglePathOnly = false
    )
    val reporter = rememberTaskReporter(
        plan = plan,
        config = reporterConfig
    )
    val viewerConfig = rememberTaskViewerConfig(
        autoScrollToBottom = false,
        showTaskTimes = true
    )

    TaskViewerContainer(
        reporter = reporter,
        config = viewerConfig,
        modifier = Modifier.fillMaxSize().padding(all = 8.dp)
    ) {
        MyButton(
            onClick = {
                scope.launch { runTest(plan, reporter) }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Run Test")
        }
    }
}

private fun createTestPlan(
    taskDurationMs: Long = 50
) : TaskPlanRoot {
    suspend fun pause() {
        delay(taskDurationMs.milliseconds)
    }

    val plan = taskPlan {

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
        }

        group("Deploy Test System 2") {

        }
    }

    return plan
}

private suspend fun runTest(
    plan: TaskPlanRoot,
    reporter: TaskReporter
) {
    withContext(Dispatchers.PlatformIO) {
        reporter.reset()
        plan.execute(reporter)
    }
}