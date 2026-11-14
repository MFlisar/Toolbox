package com.michaelflisar.toolbox.app

import android.app.Application
import com.jakewharton.processphoenix.ProcessPhoenix
import com.michaelflisar.kmp.platformcontext.PlatformContextProvider
import com.michaelflisar.toolbox.acra.AcraManager

abstract class AndroidApplication : Application() {

    final override fun onCreate() {

        super.onCreate()

        if (ProcessPhoenix.isPhoenixProcess(this))
            return

        PlatformContextProvider.init(this)
        initSetup()

        // nach minimalem init im ACRA Prozess ggf. abbrechen
        if (AcraManager.isACRAProcess()) {
            return
        }

        initFull()
    }

    abstract fun initSetup()
    abstract fun initFull()

}