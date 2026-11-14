package com.michaelflisar.toolbox.utils

import java.io.PrintWriter
import java.io.StringWriter
import java.lang.Exception

object ExceptionUtil {

    fun getStackTrace(e: Exception?): String? {
        if (e == null) return null
        val sw = StringWriter()
        e.printStackTrace(PrintWriter(sw))
        return sw.toString()
    }

    fun getStackTrace(e: Throwable?): String? {
        if (e == null) return null
        val sw = StringWriter()
        e.printStackTrace(PrintWriter(sw))
        return sw.toString()
    }
}