package com.michaelflisar.toolbox.tasks.execution

sealed interface TaskResult {

    val status: String

    data class Success(
        override val status: String,
    ) : TaskResult

    data class Warning(
        override val status: String,
    ) : TaskResult

    data class Error(
        val exception: Exception,
    ) : TaskResult {

        constructor(errorMessage: String) : this(Exception(errorMessage))
        override val status: String
            get() = exception.message ?: "Error"
    }
}