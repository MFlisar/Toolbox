package com.michaelflisar.toolbox.androiddemoapp

import android.app.Application
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.composethemer.themes.ComposeThemes

open class DemoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ComposeTheme.register(*ComposeThemes.ALL.toTypedArray())
    }

}