package com.michaelflisar.toolbox.tasks.execution

sealed interface TaskResult<out T> {

    val status: String

    data class Success<out T>(
        override val status: String,
        val result: T,
    ) : TaskResult<T> {
        companion object {
            operator fun invoke(status: String): Success<Unit> = Success(status, Unit)
        }
    }

    data class Warning<out T>(
        override val status: String,
        val result: T,
    ) : TaskResult<T> {
        companion object {
            operator fun invoke(status: String): Warning<Unit> = Warning(status, Unit)
        }
    }

    data class Error(
        val exception: Exception,
    ) : TaskResult<Nothing> {

        constructor(errorMessage: String) : this(Exception(errorMessage))
        override val status: String
            get() = exception.message ?: "Error"
    }
}