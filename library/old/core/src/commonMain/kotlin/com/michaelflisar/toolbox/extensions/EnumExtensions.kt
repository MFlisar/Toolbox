package com.michaelflisar.toolbox.extensions

fun <E : Enum<E>> Enum<E>.next(entries: List<E>) = entries[(ordinal + 1) % entries.size]