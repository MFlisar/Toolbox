package com.michaelflisar.demo.pages.tests

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.kmp.platformcontext.PlatformIO
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.components.MyButton
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.tasks.TaskReporter
import com.michaelflisar.toolbox.tasks.TaskResult
import com.michaelflisar.toolbox.tasks.TaskViewerContainer
import com.michaelflisar.toolbox.tasks.rememberTaskReporter
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
    val reporter = rememberTaskReporter()

    TaskViewerContainer(
        reporter = reporter,
        modifier = Modifier.fillMaxSize().padding(all = 8.dp)
    ) {
        MyButton(
            onClick = {
                reporter.reset()
                scope.launch { runTest(reporter) }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Run Test")
        }
    }
}

private suspend fun runTest(
    reporter: TaskReporter,
    taskDurationMs: Long = 50,
) {
    suspend fun pause() {
        delay(taskDurationMs.milliseconds)
    }


    withContext(Dispatchers.PlatformIO) {

        reporter.runTask("Deploy Test System") { deployment ->

            deployment.runSubTask("Copy Files") { copy ->
                repeat(30) {
                    copy.setSubtitle("${it + 1}/30 files")
                    copy.reportStep("Copied file_${it + 1}.dat")
                    pause()
                }
                TaskResult.Success
            }

            deployment.runSubTask("Move Files") { move ->
                repeat(20) {
                    move.reportStep("Moved document_${it + 1}.pdf")
                    pause()
                }
                TaskResult.Success
            }

            deployment.runSubTask("Check Online Users") { users ->
                listOf(
                    "SERVER01" to true,
                    "SERVER02" to true,
                    "SERVER03" to false,
                    "SERVER04" to true,
                    "SERVER05" to false
                ).forEach { (server, online) ->
                    users.reportStep(
                        if (online) {
                            "$server is online"
                        } else {
                            "$server is offline"
                        }
                    )
                    pause()
                }
                TaskResult.Success
            }

            deployment.runSubTask("Create ZIP") { createZip ->
                repeat(20) {
                    createZip.setSubtitle("${it + 1}/20 files")
                    createZip.reportStep("Added file_${it + 1}.dat to deployment.zip")
                    pause()
                }
                TaskResult.Success
            }

            deployment.runSubTask("Transfer ZIP") { copyZip ->
                copyZip.reportStep("deployment.zip -> \\\\SERVER01\\Deploy")
                pause()
                copyZip.reportStep("Transfer completed")
                pause()
                TaskResult.Success
            }

            deployment.runSubTask("Extract ZIP") { extractZip ->
                repeat(20) {
                    extractZip.setSubtitle("${it + 1}/20 files")
                    extractZip.reportStep("Extracted file_${it + 1}.dat")
                    pause()
                }
                TaskResult.Success
            }

            deployment.runSubTask("Delete ZIP") { deleteZip ->

                deleteZip.reportStep("Deleting deployment.zip")
                pause()
                deleteZip.reportStep("deployment.zip removed")
                pause()
                TaskResult.Success
            }

            deployment.runSubTask("Error Example") { errorTask ->
                errorTask.reportStep("Doing something")
                pause()
                TaskResult.Error("Something went wrong")
            }

            TaskResult.Success
        }
    }
}