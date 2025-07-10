package com.michaelflisar.toolbox

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object AppContext {

    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context.applicationContext
    }

    fun context(): Context {
        return context
    }
}