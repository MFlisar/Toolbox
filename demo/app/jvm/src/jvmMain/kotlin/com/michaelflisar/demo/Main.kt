package com.michaelflisar.demo

import androidx.compose.ui.window.application
import com.michaelflisar.kotpreferences.storage.datastore.DataStoreStorage
import com.michaelflisar.kotpreferences.storage.datastore.create
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.toolbox.app.utils.runApp
import com.michaelflisar.toolbox.app.App
import com.michaelflisar.toolbox.app.DesktopWindow
import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import com.michaelflisar.toolbox.app.features.preferences.DesktopPrefs
import com.michaelflisar.toolbox.app.init
import com.michaelflisar.toolbox.demo.BuildKonfig
import com.michaelflisar.toolbox.utils.JvmAppMeta
import com.michaelflisar.toolbox.utils.JvmFolderUtil
import com.michaelflisar.toolbox.utils.JvmUtil

fun main() {
    JvmUtil.runApp {
        app()
    }
}

private class SomeClassFromApp

private fun app() {

    val appMeta = JvmAppMeta.detect(
        cls = SomeClassFromApp::class.java,
        debug = BuildKonfig.DEBUG,
        exe = BuildKonfig.EXE
    )

    // 1) Pfade
    val dataFolder = JvmFolderUtil.getPathForAppData(appMeta, BuildKonfig.namespace)

    // 2) Storages erstellen
    val storageSettings = DataStoreStorage.create(folder = dataFolder, name = "settings")
    val storageDebug = DataStoreStorage.create(folder = dataFolder, name = "debug")
    val storageWindows = DataStoreStorage.create(folder = dataFolder, name = "windows")

    // 3) Setups erstellen
    val setup = Shared.createSetup(
        storageSettings = storageSettings,
        storageDebug = storageDebug,
        fileLoggingSetup = FileLoggerSetup.SingleFile(
            folder = dataFolder
        ),
        isDebugBuild = appMeta.isDebug
    )
    val desktopSetup = DesktopAppSetup(
        prefs = DesktopPrefs(storageWindows)
    )

    // 4) App initialisieren
    App.init(setup, desktopSetup)
    Shared.init()

    // 5) Application und Window
    application {
        DesktopWindow(
            onCloseRequest = ::exitApplication
        ) {
            DemoApp(
                desktopMenuBar = true,
                forwardHistoryEnabled = true
            )
        }
    }
}