package com.michaelflisar.toolbox.tasks.execution

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/** A typed reference to a task's output, separate from its execution status in [TaskResult]. */
class TaskHandle<T> internal constructor(
    val id: String,
    val title: String,
    internal val key: TaskKey<T>,
)

internal class TaskKey<T>(
    val name: String,
)

internal class TaskDataStore : AbstractCoroutineContextElement(Key) {

    companion object Key : CoroutineContext.Key<TaskDataStore>

    private val values = mutableMapOf<TaskKey<*>, Any?>()

    fun <T> provide(
        key: TaskKey<T>,
        value: T,
    ) {
        values[key] = value
    }

    fun <T> require(key: TaskKey<T>): T {
        check(values.containsKey(key)) {
            "No result stored for task '${key.name}'."
        }
        return value(key)
    }

    fun <T> getOrNull(key: TaskKey<T>): T? =
        if (values.containsKey(key)) value(key) else null

    @Suppress("UNCHECKED_CAST")
    private fun <T> value(key: TaskKey<T>): T =
        values[key] as T
}
