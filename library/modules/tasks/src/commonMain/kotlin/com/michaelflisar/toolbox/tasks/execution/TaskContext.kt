package com.michaelflisar.toolbox.tasks.execution

import androidx.compose.ui.text.AnnotatedString
import com.michaelflisar.toolbox.tasks.plan.TaskExecutionListener
import com.michaelflisar.toolbox.tasks.plan.TaskMessage
import com.michaelflisar.toolbox.tasks.plan.TaskMessageColors
import com.michaelflisar.toolbox.tasks.plan.TaskMessageType
import com.michaelflisar.toolbox.tasks.plan.TaskPlanDsl

/**
 * der Kontext, der an die Task übergeben wird, damit diese ihren Status und ihre Log-Messages an den Listener weitergeben kann
 *
 * biete **log*** und **updateStatus** Funktionen an
 */
@TaskPlanDsl
class TaskExecutionContext internal constructor(
    private val colors: TaskMessageColors,
    private val listener: TaskExecutionListener?,
    private val taskId: String,
    private val taskData: TaskDataStore,
) {
    /** Reads an earlier task's output or fails the current task if no output was stored. */
    fun <T> TaskHandle<T>.requireData(): T =
        taskData.require(key)

    /** Returns null if the referenced task has not stored an output. */
    val <T> TaskHandle<T>.data: T?
        get() = taskData.getOrNull(key)

    internal fun <T> storeResult(key: TaskKey<T>, value: T) {
        taskData.provide(key, value)
    }

    fun updateStatus(
        text: String?,
    ) {
        listener?.onTaskStatusChanged(
            taskId = taskId,
            status = text,
        )
    }

    fun logRichText(
        block: TaskMessageColors.() -> AnnotatedString,
    ) {
        listener?.onTaskMessage(
            taskId = taskId,
            message = TaskMessage.Rich(
                text = with(colors) { block() }
            )
        )
    }

    fun log(text: String) = log(text = text, type = TaskMessageType.Info)
    fun logWarning(text: String) = log(text = text, type = TaskMessageType.Warning)
    fun logError(text: String) = log(text = text, type = TaskMessageType.Error)

    fun log(items: List<String>, title: String? = null) =
        log(items = items, title = title, type = TaskMessageType.Info)

    fun logWarning(items: List<String>, title: String? = null) =
        log(items = items, title = title, type = TaskMessageType.Warning)

    fun logError(items: List<String>, title: String? = null) =
        log(items = items, title = title, type = TaskMessageType.Error)

    // ----------------------
    // private Funktionen
    // ----------------------

    private fun log(
        text: String,
        type: TaskMessageType,
    ) {
        log(
            message = TaskMessage.Text(
                text = text,
                type = type,
            )
        )
    }

    private fun log(
        items: List<String>,
        title: String?,
        type: TaskMessageType
    ) {
        log(
            message = TaskMessage.ItemList(
                items = items.toList(),
                title = title,
                type = type,
            )
        )
    }

    private fun log(
        message: TaskMessage,
    ) {
        listener?.onTaskMessage(
            taskId = taskId,
            message = message,
        )
    }
}