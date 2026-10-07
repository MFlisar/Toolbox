package com.michaelflisar.toolbox.tasks.plan

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