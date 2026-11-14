package com.michaelflisar.toolbox.extensions

fun String.between(from: String, to: String): String? {
    val indexFrom = indexOf(from)
    if (indexFrom == -1)
        return null
    val indexEnd = indexOf(to, indexFrom + from.length)
    if (indexEnd == -1)
        return null
   return substring(indexFrom + from.length, indexEnd)
}

fun String.toSentenceCase() : String {
    if (this.isEmpty()) return this
    return this[0].uppercaseChar() + this.substring(1).lowercase()
}

fun String.toTitleCase() : String {
    return this.split(" ").joinToString(" ") { it.toSentenceCase() }
}