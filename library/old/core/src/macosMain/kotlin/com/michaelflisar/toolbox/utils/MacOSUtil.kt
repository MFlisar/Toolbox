package com.michaelflisar.toolbox.utils

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UIntVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import macos._NSGetExecutablePath

object MacOSUtil {

    @OptIn(ExperimentalForeignApi::class)
    fun getExecutablePath(): String? {
        return memScoped {
            val bufSize = alloc<UIntVar>()
            bufSize.value = 1024u
            val buffer = allocArray<ByteVar>(bufSize.value.toInt())
            val result = _NSGetExecutablePath(buffer, bufSize.ptr)
            if (result == 0) {
                buffer.toKString()
            } else {
                null // Buffer war zu klein oder Fehler
            }
        }
    }
}