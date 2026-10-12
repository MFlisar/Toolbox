package com.michaelflisar.toolbox.tasks.ui

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.extensions.isDark
import com.michaelflisar.toolbox.tasks.plan.TaskPlanSummary
import com.michaelflisar.toolbox.utils.TimeUtil

object TaskViewerConfigDefaults {

    /** Counts executed and skipped tasks as completed in the displayed summary. */
    fun headerSummaryFormatter(
        isFinished: Boolean,
        executedTasks: Int,
        skippedTasks: Int,
        totalTasks: Int,
        duration: String,
    ): AnnotatedString {
        return buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                append("${executedTasks + skippedTasks} von $totalTasks")
            }
            if (isFinished) {
                append(" Aufgaben erledigt · Abgeschlossen in ")
            } else {
                append(" Aufgaben erledigt · Läuft seit ")
            }
            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                append(duration)
            }
        }
    }

    fun groupSummaryFormatter(summary: TaskPlanSummary): String {
        return buildString {
            // es läuft immer nur eine Aufgabe pro Gruppe, daher ist es nicht nötig, die Anzahl der laufenden Aufgaben anzuzeigen
            //if (summary.running > 0) {
            //    append("Läuft")
            //}
            if (summary.success > 0) {
                if (isNotEmpty()) append(", ")
                append("${summary.success} erfolgreich")
            }
            if (summary.warnings > 0) {
                if (isNotEmpty()) append(", ")
                append("${summary.warnings} Warnungen")
            }
            if (summary.errors > 0) {
                if (isNotEmpty()) append(", ")
                append("${summary.errors} Fehler")
            }
            if (summary.skipped > 0) {
                if (isNotEmpty()) append(", ")
                append("${summary.skipped} übersprungen")
            }
        }
    }

    fun timeFormatter(isFinished: Boolean, millis: Long): String {
        return TimeUtil.getTimeString(
            millis = millis,
            secondFractionDigits = if (isFinished && millis < 60_000L) {
                1
            } else {
                0
            },
        )
    }
}

@Stable
data class TaskViewerLayout(
    val itemSpacing: Dp = 8.dp,
    val horizontalPadding: Dp = 12.dp,
    val verticalPadding: Dp = 8.dp,
    val iconSpacing: Dp = 8.dp,
    val expandIconSize: Dp = 18.dp,
    val statusIconSize: Dp = 18.dp,
    val indentPerLevel: Dp = expandIconSize + iconSpacing,
    val messageBulletWidth: Dp = 12.dp,
    val expandedContentSpacing: Dp = 8.dp,
    val expandedContentVerticalPadding: Dp = 8.dp,
    val minItemHeight: Dp = 48.dp,
    val messageNumberSpacing: Dp = 4.dp
) {
    companion object {
        val Default = TaskViewerLayout()
        val Compact = TaskViewerLayout(
            itemSpacing = 2.dp,
            horizontalPadding = 8.dp,
            verticalPadding = 2.dp,
            iconSpacing = 4.dp,
            expandIconSize = 16.dp,
            statusIconSize = 16.dp,
            indentPerLevel = 20.dp,
            messageBulletWidth = 10.dp,
            expandedContentSpacing = 2.dp,
            expandedContentVerticalPadding = 4.dp,
            minItemHeight = 24.dp,
            messageNumberSpacing = 4.dp
        )
    }
}
