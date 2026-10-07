package com.michaelflisar.toolbox.tasks

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.michaelflisar.toolbox.tasks.execution.TaskStatus
import com.michaelflisar.toolbox.tasks.plan.TaskMessageColors
import com.michaelflisar.toolbox.tasks.plan.TaskPlanSummary
import com.michaelflisar.toolbox.tasks.plan.rememberTaskMessageColors
import com.michaelflisar.toolbox.tasks.ui.TaskViewerConfigDefaults
import com.michaelflisar.toolbox.tasks.ui.TaskViewerLayout

@Stable
data class TaskConfig(
    val execution: ExecutionConfig,
    val viewer: ViewerConfig,
    val viewState: ViewStateConfig,
) {
    enum class ErrorBehavior {
        /** Nach Fehlern weiter ausführen. */
        Continue,
        /** Rest der unmittelbar betroffenen Gruppe überspringen. */
        StopGroup,
        /** Rest der obersten Gruppe überspringen, danach weiter. */
        StopRootGroup,
        /** Rest des gesamten Plans überspringen. */
        StopAll,
    }

    enum class FinishedTaskDisplay {
        Open,
        Closed
    }

    @Stable
    data class FinishedTaskBehavior(
        val success: FinishedTaskDisplay = FinishedTaskDisplay.Closed,
        val warning: FinishedTaskDisplay = FinishedTaskDisplay.Closed,
        val error: FinishedTaskDisplay = FinishedTaskDisplay.Open,
        val cancelled: FinishedTaskDisplay = FinishedTaskDisplay.Closed,
    ) {
        companion object {
            val CLOSE_ALL = FinishedTaskBehavior(
                success = FinishedTaskDisplay.Closed,
                warning = FinishedTaskDisplay.Closed,
                error = FinishedTaskDisplay.Closed,
                cancelled = FinishedTaskDisplay.Closed,
            )
            val OPEN_ALL = FinishedTaskBehavior(
                success = FinishedTaskDisplay.Open,
                warning = FinishedTaskDisplay.Open,
                error = FinishedTaskDisplay.Open,
                cancelled = FinishedTaskDisplay.Open,
            )
        }

        fun get(status: TaskStatus.Finished): FinishedTaskDisplay {
            return when (status) {
                is TaskStatus.Success -> success
                is TaskStatus.Warning -> warning
                is TaskStatus.Error -> error
                TaskStatus.Cancelled -> cancelled
            }
        }
    }

    @Stable
    data class ExecutionConfig(
        val taskMessageColors: TaskMessageColors,
        val errorBehavior: ErrorBehavior = ErrorBehavior.Continue,
    )

    @Stable
    data class ViewerConfig(
        val taskMessageColors: TaskMessageColors,
        val containerColor: Color,
        val contentColor: Color,
        val autoScrollToBottom: Boolean,
        val showTaskTimes: Boolean,
        val expandSinglePathOnly: Boolean,
        val layout: TaskViewerLayout,
        val groupSummaryFormatter: (TaskPlanSummary) -> String,
        val timeFormatter: (isFinished: Boolean, millis: Long) -> String,
        val skippedTaskSubtitle: String,
        val prefixWarning: String,
        val prefixError: String,
        val showHeaderNumbers: Boolean = false,
        val showMessageNumbers: Boolean = false,
        val headerTasksLabel: String = "tasks",
        val headerRunningLabel: String = "running",
        val headerPassedLabel: String = "passed",
        val headerWarningsLabel: String = "warnings",
        val headerFailedLabel: String = "failed",
        val headerSkippedLabel: String = "skipped",
        val headerDurationLabel: String = "duration",
    )

    @Stable
    data class ViewStateConfig(
        /**
         * Neue Tasks automatisch öffnen.
         */
        val autoExpandNewTasks: Boolean,

        /**
         * Laufende Tasks automatisch öffnen.
         */
        val expandRunningTasks: Boolean,

        /**
         * Verhalten nach Abschluss.
         */
        val finishedTaskBehavior: FinishedTaskBehavior,
    ) {
        companion object {
            val Default = ViewStateConfig(
                autoExpandNewTasks = true,
                expandRunningTasks = true,
                finishedTaskBehavior = FinishedTaskBehavior.CLOSE_ALL,
            )
        }
    }
}

/**
 * Erstellt eine gemerkte Konfiguration für Ausführung, Anzeige und View-State.
 *
 * @param taskMessageColors Farben für Task-Nachrichten.
 * @param errorBehavior Verhalten bei Task-Fehlern.
 * @param autoExpandNewTasks Öffnet neue Gruppen automatisch.
 * @param expandRunningTasks Öffnet laufende Tasks automatisch.
 * @param finishedTaskBehavior Legt fest, welche fertigen Tasks geöffnet bleiben.
 * @param containerColor Hintergrundfarbe des Viewers.
 * @param contentColor Inhaltsfarbe des Viewers.
 * @param autoScrollToBottom Scrollt bei neuen Tasks nach unten.
 * @param showTaskTimes Zeigt Laufzeiten an.
 * @param expandSinglePathOnly Öffnet nur den Pfad zum ausgewählten Eintrag.
 * @param layout Layout und Abstände der Einträge.
 * @param groupSummaryFormatter Formatiert die Gruppen-Zusammenfassung.
 * @param timeFormatter Formatiert die Laufzeit.
 * @param skippedTaskSubtitle Text für übersprungene Tasks.
 * @param prefixWarning Präfix für Warnmeldungen.
 * @param prefixError Präfix für Fehlermeldungen.
 * @param showHeaderNumbers Nummeriert Task- und Gruppenüberschriften.
 * @param showMessageNumbers Nummeriert Task-Nachrichten.
 * @param headerTasksLabel Beschriftung der Task-Anzahl.
 * @param headerRunningLabel Beschriftung laufender Tasks.
 * @param headerPassedLabel Beschriftung erfolgreicher Tasks.
 * @param headerWarningsLabel Beschriftung der Warnungen.
 * @param headerFailedLabel Beschriftung fehlgeschlagener Tasks.
 * @param headerSkippedLabel Beschriftung übersprungener Tasks.
 * @param headerDurationLabel Beschriftung der Gesamtdauer.
 */
@Composable
fun rememberTaskConfig(
    // Gemeinsame Nachrichten
    taskMessageColors: TaskMessageColors = rememberTaskMessageColors(),
    // Ausführung
    errorBehavior: TaskConfig.ErrorBehavior = TaskConfig.ErrorBehavior.Continue,
    // View-State
    autoExpandNewTasks: Boolean = true,
    expandRunningTasks: Boolean = true,
    finishedTaskBehavior: TaskConfig.FinishedTaskBehavior = TaskConfig.FinishedTaskBehavior.CLOSE_ALL,
    // Viewer
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    autoScrollToBottom: Boolean = true,
    showTaskTimes: Boolean = true,
    expandSinglePathOnly: Boolean = true,
    layout: TaskViewerLayout = TaskViewerLayout.Compact,
    groupSummaryFormatter: (TaskPlanSummary) -> String = TaskViewerConfigDefaults::groupSummaryFormatter,
    timeFormatter: (isFinished: Boolean, millis: Long) -> String = TaskViewerConfigDefaults::timeFormatter,
    skippedTaskSubtitle: String = "Task skipped",
    prefixWarning: String = "Warning: ",
    prefixError: String = "Error: ",
    showHeaderNumbers: Boolean = false,
    showMessageNumbers: Boolean = true,
    // Texte im Viewer-Header
    headerTasksLabel: String = "tasks",
    headerRunningLabel: String = "running",
    headerPassedLabel: String = "passed",
    headerWarningsLabel: String = "warnings",
    headerFailedLabel: String = "failed",
    headerSkippedLabel: String = "skipped",
    headerDurationLabel: String = "duration",
): TaskConfig {
    return remember(
        taskMessageColors,
        errorBehavior,
        autoExpandNewTasks,
        expandRunningTasks,
        finishedTaskBehavior,
        containerColor,
        contentColor,
        autoScrollToBottom,
        showTaskTimes,
        expandSinglePathOnly,
        layout,
        groupSummaryFormatter,
        timeFormatter,
        skippedTaskSubtitle,
        prefixWarning,
        prefixError,
        showHeaderNumbers,
        showMessageNumbers,
        headerTasksLabel,
        headerRunningLabel,
        headerPassedLabel,
        headerWarningsLabel,
        headerFailedLabel,
        headerSkippedLabel,
        headerDurationLabel,
    ) {
        TaskConfig(
            execution = TaskConfig.ExecutionConfig(
                taskMessageColors = taskMessageColors,
                errorBehavior = errorBehavior,
            ),
            viewer = TaskConfig.ViewerConfig(
                taskMessageColors = taskMessageColors,
                containerColor = containerColor,
                contentColor = contentColor,
                autoScrollToBottom = autoScrollToBottom,
                showTaskTimes = showTaskTimes,
                expandSinglePathOnly = expandSinglePathOnly,
                layout = layout,
                groupSummaryFormatter = groupSummaryFormatter,
                timeFormatter = timeFormatter,
                skippedTaskSubtitle = skippedTaskSubtitle,
                prefixWarning = prefixWarning,
                prefixError = prefixError,
                showHeaderNumbers = showHeaderNumbers,
                showMessageNumbers = showMessageNumbers,
                headerTasksLabel = headerTasksLabel,
                headerRunningLabel = headerRunningLabel,
                headerPassedLabel = headerPassedLabel,
                headerWarningsLabel = headerWarningsLabel,
                headerFailedLabel = headerFailedLabel,
                headerSkippedLabel = headerSkippedLabel,
                headerDurationLabel = headerDurationLabel,
            ),
            viewState = TaskConfig.ViewStateConfig(
                autoExpandNewTasks = autoExpandNewTasks,
                expandRunningTasks = expandRunningTasks,
                finishedTaskBehavior = finishedTaskBehavior,
            ),
        )
    }
}
