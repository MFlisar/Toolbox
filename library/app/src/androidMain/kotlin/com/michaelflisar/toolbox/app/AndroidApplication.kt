package com.michaelflisar.toolbox.app

import android.app.Application
import com.jakewharton.processphoenix.ProcessPhoenix
import com.michaelflisar.toolbox.AppContext
import com.michaelflisar.toolbox.app.classes.AndroidAppSetup
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.crash_dialog_text
import com.michaelflisar.toolbox.core.resources.crash_dialog_title
import com.michaelflisar.toolbox.managers.AcraManager
import com.michaelflisar.toolbox.utils.AcraUtil
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource

abstract class AndroidApplication : Application() {

    abstract val appIcon: Int

    final override fun onCreate() {

        super.onCreate()

        if (AcraManager.isACRAProcess())
            return
        if (ProcessPhoenix.isPhoenixProcess(this))
            return

        // 1) init
        AppContext.init(this)
        val setup = createSetup()
        val androidSetup = createAndroidSetup()
        App.init(setup)
        AcraUtil.initAcra(
            this,
            appIcon = appIcon,
            appName = setup.name,
            fileLoggerSetup = setup.fileLogger?.setup,
            crash_dialog_text = Res.string.crash_dialog_text,
            crash_dialog_title = Res.string.crash_dialog_title,
            buildConfigClass = androidSetup.buildConfigClass.java,
            isDebugBuild = setup.isDebugBuild
        )

        onAfterCreate()
    }

    open fun onAfterCreate() {
    }

    abstract fun createSetup(): AppSetup
    abstract fun createAndroidSetup(): AndroidAppSetup

}