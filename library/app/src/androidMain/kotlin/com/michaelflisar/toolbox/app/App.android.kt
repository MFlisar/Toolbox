package com.michaelflisar.toolbox.app

import android.app.Application
import androidx.activity.ComponentActivity
import com.michaelflisar.toolbox.acra.AcraSetup
import com.michaelflisar.toolbox.app.classes.AndroidAppSetup
import com.michaelflisar.toolbox.app.features.update.AndroidInAppUpdate
import com.michaelflisar.toolbox.utils.AcraUtil
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init

/**
 * initialises the setups
 *
 * also call [App.initActivity] to initialise FileKit with the activity
 */
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

/**
 * Initialises FileKit and the configured in-app update flow.
 *
 * Call from the activity's `onCreate`; Android's activity result launcher must be registered before
 * the activity is started.
 */
fun App.initActivity(activity: ComponentActivity) {
    FileKit.init(activity)
    AndroidInAppUpdate.initialize(activity, AppSetup.get().inAppUpdate)
}
