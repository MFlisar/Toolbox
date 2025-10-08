package com.michaelflisar.toolbox

import kotlinx.serialization.json.Json

object JsonDefaults {

    val JSON = Json {
        useArrayPolymorphism = true
    }

    inline fun <reified T> encodeToString(data: T) = JSON.encodeToString(data)
    inline fun <reified T> decodeFromString(data: String) = JSON.decodeFromString<T>(data)
}