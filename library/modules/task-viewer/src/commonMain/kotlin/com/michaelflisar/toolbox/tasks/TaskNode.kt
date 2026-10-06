package com.michaelflisar.toolbox.tasks

data class TaskNode(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val status: Status = Status.Running,
    val children: List<TaskNode> = emptyList(),
    val messages: List<Message> = emptyList(),
    val expanded: Boolean = true,
) {
    val isFinished: Boolean
        get() = status != Status.Running

    val isFailed: Boolean
        get() = status is Status.Error

    val hasChildren: Boolean
        get() = children.isNotEmpty()

    val hasMessages: Boolean
        get() = messages.isNotEmpty()
}

sealed interface Status {
    data object Running : Status
    data object Success : Status
    data object Warning : Status
    data class Error(val message: String, val exception: Exception?) : Status
}

data class Message(
    val text: String,
    val type: MessageType = MessageType.Info,
)

enum class MessageType {
    Info,
    Warning,
    Error
}

sealed interface TaskResult {

    data object Success : TaskResult
    data object Warning : TaskResult

    data class Error(
        val message: String,
    ) : TaskResult
}