package com.michaelflisar.toolbox.app

import android.app.Application
import androidx.activity.ComponentActivity
import com.michaelflisar.toolbox.acra.AcraSetup
import com.michaelflisar.toolbox.app.classes.AndroidAppSetup
import com.michaelflisar.toolbox.utils.AcraUtil
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init

object AndroidApp {

    fun init(
        app: Application,
        setup: AppSetup,
        androidSetup: AndroidAppSetup,
        appIcon: Int,
    ) {
        App.init(setup)
        AcraUtil.initAcra(
            app = app,
            acraSetup = AcraSetup(
                appIcon = appIcon,
                appName = setup.appData.name
            ),
            fileLoggerSetup = setup.fileLogger?.setup,
            buildConfigClass = androidSetup.buildConfigClass.java,
            isDebugBuild = setup.isDebugBuild
        )
    }

    fun initActivity(activity: ComponentActivity) {
        FileKit.init(activity)
    }
}