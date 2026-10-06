package com.michaelflisar.toolbox.tasks

import kotlinx.coroutines.CancellationException

internal sealed class TaskAbort(
    message: String,
) : CancellationException(message) {

    class Warning(
        override val message: String?,
    ) : TaskAbort(message ?: "Task aborted with warning")

    class Error(
        val exception: Exception
    ) : TaskAbort(exception.message ?: "Task aborted with error")
}
