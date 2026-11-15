package com.michaelflisar.toolbox.utils

import platform.Foundation.NSProcessInfo

object MacOSUtil {

    fun getExecutablePath(): String? {
        return NSProcessInfo.processInfo.arguments.firstOrNull() as? String
    }
}