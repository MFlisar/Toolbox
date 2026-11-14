package com.michaelflisar.toolbox.app

import android.app.Application
import androidx.activity.ComponentActivity
import com.michaelflisar.toolbox.acra.AcraSetup
import com.michaelflisar.toolbox.app.classes.AndroidAppSetup
import com.michaelflisar.toolbox.utils.AcraUtil
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init

fun App.init(
    app: Application,
    setup: AppSetup,
    android: AndroidAppSetup,
    appIcon: Int,
) {
    initAppSetup(setup)
    AcraUtil.initAcra(
        app = app,
        acraSetup = AcraSetup(
            appIcon = appIcon,
            appName = AppSetup.get().appData.name
        ),
        fileLoggingSetup = AppSetup.get().logging.file,
        buildConfigClass = android.buildConfigClass.java,
        isDebugBuild = AppSetup.get().isDebugBuild
    )
}

fun App.initActivity(activity: ComponentActivity) {
    FileKit.init(activity)
}
