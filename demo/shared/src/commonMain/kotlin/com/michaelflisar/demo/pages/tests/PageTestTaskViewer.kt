package com.michaelflisar.demo.pages.tests

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import com.michaelflisar.toolbox.tasks.TaskViewerContainer
import com.michaelflisar.toolbox.tasks.rememberTaskReporter
import com.michaelflisar.toolbox.tasks.rememberTaskViewerConfig
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
    val config = rememberTaskViewerConfig()

    TaskViewerContainer(
        reporter = reporter,
        config = config,
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

        reporter.runTask("Deploy Test System") {

            runSubTask("Copy Files") {
                repeat(30) {
                    setStatus("${it + 1}/30 files")
                    addInfo("Copied file_${it + 1}.dat")
                    pause()
                }
            }

            runSubTask("Move Files") {
                repeat(20) {
                    addInfo("Moved document_${it + 1}.pdf")
                    pause()
                }
            }

            runSubTask("Check Online Users") {
                listOf(
                    "SERVER01" to true,
                    "SERVER02" to true,
                    "SERVER03" to false,
                    "SERVER04" to true,
                    "SERVER05" to false
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
            }

            runSubTask("Create ZIP") {
                repeat(20) {
                    setStatus("${it + 1}/20 files")
                    addInfo("Added file_${it + 1}.dat to deployment.zip")
                    pause()
                }
            }

            runSubTask("Transfer ZIP") {
                addInfo("deployment.zip -> \\\\SERVER01\\Deploy")
                pause()
                addInfo("Transfer completed")
                pause()
            }

            runSubTask("Extract ZIP") {
                runSubTask("Extract Sub ZIP 1") {
                    repeat(20) {
                        setStatus("${it + 1}/20 files")
                        addInfo("Extracted file_${it + 1}.dat")
                        pause()
                    }
                }
                runSubTask("Extract Sub ZIP 2") {
                    repeat(20) {
                        setStatus("${it + 1}/20 files")
                        addInfo("Extracted file_${it + 1}.dat")
                        pause()
                    }
                }
            }

            runSubTask("Delete ZIP") {

                addInfo("Deleting deployment.zip")
                pause()
                addInfo("deployment.zip removed")
                pause()
                throwWarning("Test warning")
            }

            runSubTask("Error Example") {
                addInfo("Doing something")
                pause()
                throwError("Something went wrong")
            }
        }
    }
}