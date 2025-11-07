package com.michaelflisar.toolbox.app

import android.app.Application
import com.jakewharton.processphoenix.ProcessPhoenix
import com.michaelflisar.toolbox.AppContext
import com.michaelflisar.toolbox.acra.AcraManager
import com.michaelflisar.toolbox.acra.AcraSetup
import com.michaelflisar.toolbox.app.classes.AndroidAppSetup
import com.michaelflisar.toolbox.utils.AcraUtil

abstract class AndroidApplication : Application() {

    abstract val acraSetup: AcraSetup

    final override fun onCreate() {

        super.onCreate()

        // 1) skip Phoenix process
        if (ProcessPhoenix.isPhoenixProcess(this))
            return

        // 2) init context + create setups
        AppContext.init(this)
        val setup = createSetup()
        val androidSetup = createAndroidSetup()

        // 3) init App + ACRA
        App.init(setup) // needed to init LogManager...
        AcraUtil.initAcra(
            this,
            acraSetup = acraSetup,
            appName = setup.name,
            fileLoggerSetup = setup.fileLogger?.setup,
            buildConfigClass = androidSetup.buildConfigClass.java,
            isDebugBuild = setup.isDebugBuild
        )

        // 4) skip ACRA process
        if (AcraManager.isACRAProcess()) {
            return
        }

        // 5) finish
        onAfterCreate()
    }

    open fun onAfterCreate() {
    }

    abstract fun createSetup(): AppSetup
    abstract fun createAndroidSetup(): AndroidAppSetup

}