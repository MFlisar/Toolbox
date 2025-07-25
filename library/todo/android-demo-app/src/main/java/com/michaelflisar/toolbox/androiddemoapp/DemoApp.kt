package com.michaelflisar.toolbox.androiddemoapp

import android.app.Application
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.composethemer.MetroThemes
import com.michaelflisar.composethemer.themes.DefaultThemes

open class DemoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val allThemes: List<ComposeTheme.Theme> =
            DefaultThemes.getAllThemes() + MetroThemes.getAllThemes()
                    //+ FlatUIThemes.getAllThemes()
                    //+ Material500Themes.getAllThemes()

        ComposeTheme.register(*allThemes.toTypedArray())
    }

}