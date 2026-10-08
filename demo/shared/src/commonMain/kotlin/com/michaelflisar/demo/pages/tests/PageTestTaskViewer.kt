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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.michaelflisar.kmp.platformcontext.PlatformIO
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.components.MyButton
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.tasks.TaskConfig
import com.michaelflisar.toolbox.tasks.execution.TaskResult
import com.michaelflisar.toolbox.tasks.plan.TaskPlan
import com.michaelflisar.toolbox.tasks.plan.taskPlan
import com.michaelflisar.toolbox.tasks.rememberTaskConfig
import com.michaelflisar.toolbox.tasks.ui.TaskViewerContainer
import com.michaelflisar.toolbox.tasks.ui.state.TaskViewState
import com.michaelflisar.toolbox.tasks.ui.state.rememberTaskViewState
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
    val config = rememberTaskConfig(
        expandSinglePathOnly = false
    )
    val viewState = rememberTaskViewState(config = config.viewState)

    TaskViewerContainer(
        plan = plan,
        state = viewState,
        config = config.viewer,
        modifier = Modifier.fillMaxSize().padding(all = 8.dp)
    ) {
        MyButton(
            onClick = {
                scope.launch { viewState.execute(plan, config) }
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

        // Test 1: direkter Task in Root
        task("Initialize") {
            log("Direct task in root")
            logWarning("Some warning")
            logRichText {
                buildAnnotatedString {
                    withStyle(SpanStyle(color = colorSuccess(), fontWeight = FontWeight.Bold)) {
                        append("✓ ")
                    }
                    append("Custom message with ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("custom style")
                    }
                }
            }
            logWarning("Some multi-line warning\nwith details")

            val files = List(10) { "file_${it + 1}.dat" }
            log(items = files, title = "Processed files")

            val exceptionWithStackTrace = Exception("This is a test exception with stack trace")
            logError("An error occurred: ${exceptionWithStackTrace.message}\n${exceptionWithStackTrace.stackTraceToString()}",)

            TaskResult.Success("Initialization completed")
        }

        // Test 2: Gruppe mit erfolgreichen Sub Tasks
        group("Copying Files") {
            repeat(5) { index ->
                task("Copy Files ${index + 1}") {
                    repeat(30) { fileIndex ->
                        updateStatus("${fileIndex + 1}/30 files")
                        log("Copied file_${fileIndex + 1}.dat")
                        pause()
                    }
                    TaskResult.Success("Copied 30 files")
                }
            }
        }

        // Test 3: Leere Gruppe
        group("Empty Group") {

        }

        // Test 4: Gruppe mit Fehler in einem Sub Task
        group("Group with Error") {
            task("Task 1") {
                log("Doing something")
                pause()
                TaskResult.Success("Task 1 completed")
            }
            task("Task 2 (Error)") {
                log("Doing something")
                pause()
                TaskResult.Error(Exception("Something went wrong in Task 2"))
            }
            task("Task 3") {
                log("Doing something")
                pause()
                TaskResult.Success("Task 3 completed")
            }
        }

        // Test 5: Gruppe mit Warnung in einem Sub Task
        group("Group with Warning") {
            task("Task 1") {
                log("Doing something")
                pause()
                TaskResult.Success("Task 1 completed")
            }
            task("Task 2 (Warning)") {
                log("Doing something")
                pause()
                TaskResult.Warning("Something might be wrong in Task 2")
            }
            task("Task 3") {
                log("Doing something")
                pause()
                TaskResult.Success("Task 3 completed")
            }
        }

        // Test 6: Komplexe Gruppe mit mehreren Sub Tasks und Untergruppen
        group("Complex Group") {
            task("Task 1") {
                log("Doing something")
                pause()
                TaskResult.Success("Task 1 completed")
            }
            group("Subgroup 1") {
                task("Subtask 1.1") {
                    log("Doing something")
                    pause()
                    TaskResult.Success("Subtask 1.1 completed")
                }
                task("Subtask 1.2") {
                    log("Doing something")
                    pause()
                    TaskResult.Success("Subtask 1.2 completed")
                }
            }
            group("Subgroup 2") {
                task("Subtask 2.1") {
                    log("Doing something")
                    pause()
                    TaskResult.Success("Subtask 2.1 completed")
                }
                task("Subtask 2.2 (Error)") {
                    log("Doing something")
                    pause()
                    TaskResult.Error(Exception("Something went wrong in Subtask 2.2"))
                }
            }
        }
    }

    return plan
}

private suspend fun runTest(
    plan: TaskPlan,
    viewState: TaskViewState,
    config: TaskConfig,
) {
    withContext(Dispatchers.PlatformIO) {
        viewState.reset()
        viewState.execute(plan, config)
    }
}