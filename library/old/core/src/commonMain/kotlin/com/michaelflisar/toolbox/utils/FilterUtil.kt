package com.michaelflisar.toolbox.utils

object FilterUtil {

    private val splitRegex = "\\s+".toRegex()

    fun <T> filterContainsAllWords(items: List<T>, search: String, ignoreCase: Boolean = true, mapper: (T) -> String): List<T> {
        val words = search.split(splitRegex)
        return items.filter { e ->
            val matches = words.map { mapper(e).contains(it, ignoreCase) }
            !matches.contains(false)
        }
    }
}