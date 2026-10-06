package com.michaelflisar.toolbox.tasks

data class TaskMessage(
    val text: String,
    val type: Type = Type.Info,
) {
    enum class Type {
        Info,
        Warning,
        Error
    }
}

