package com.michaelflisar.toolbox.classes

sealed class Loaded<out T> {

    data class Loaded<T>(val data: T) : com.michaelflisar.toolbox.classes.Loaded<T>()
    object NotLoaded : com.michaelflisar.toolbox.classes.Loaded<Nothing>()

    fun isLoaded(): Boolean = this is Loaded<T>

    /**
     * Returns the data if loaded, or null otherwise
     */
    fun dataOrNull(): T? = if (this is Loaded<T>) this.data else null

    /**
     * Returns the data if loaded, or throws an IllegalStateException otherwise
     */
    fun requireData(): T =
        if (this is Loaded<T>) this.data else throw IllegalStateException("Data is not loaded")
}