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