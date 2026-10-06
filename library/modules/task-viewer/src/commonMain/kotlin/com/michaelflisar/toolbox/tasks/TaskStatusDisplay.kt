package com.michaelflisar.toolbox.tasks

import androidx.compose.runtime.Stable

@Stable
data class FinishedTaskBehavior(
    val success: Display = Display.Closed,
    val warning: Display = Display.Closed,
    val error: Display = Display.Open,
) {
    enum class Display {
        Open,
        Closed
    }

    fun get(status: TaskStatus.Finished): Display {
        return when (status) {
            is TaskStatus.Success -> success
            is TaskStatus.Warning -> warning
            is TaskStatus.Error -> error
        }
    }
}



