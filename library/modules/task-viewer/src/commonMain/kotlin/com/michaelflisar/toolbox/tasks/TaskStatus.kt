package com.michaelflisar.toolbox.tasks

sealed interface TaskStatus {
    data object Running : TaskStatus

    sealed interface Finished : TaskStatus {
        val message: String?
    }
    data object Success : Finished {
        override val message: String? = null
    }
    data class Warning(
        override val message: String?,
    ) : Finished

    data class Error(
        val exception: Exception
    ) : Finished {
        override val message: String? = exception.message
    }
}