package com.michaelflisar.demo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.application
import com.michaelflisar.composedialogs.core.rememberDialogState
import com.michaelflisar.demo.pages.tests.TestPrefs
import com.michaelflisar.kotpreferences.storage.datastore.DataStoreStorage
import com.michaelflisar.kotpreferences.storage.datastore.create
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.toolbox.app.App
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.DesktopApp
import com.michaelflisar.toolbox.app.DesktopAppDefaults
import com.michaelflisar.toolbox.app.DesktopApplication
import com.michaelflisar.toolbox.app.DesktopContainer
import com.michaelflisar.toolbox.app.DesktopMenuBar
import com.michaelflisar.toolbox.app.DesktopPrefs
import com.michaelflisar.toolbox.app.DesktopStatusBar
import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import com.michaelflisar.toolbox.app.debug.DebugPrefs
import com.michaelflisar.toolbox.app.features.dialogs.JvmAppInfoDialog
import com.michaelflisar.toolbox.app.features.dialogs.LocalErrorDialogState
import com.michaelflisar.toolbox.app.features.dialogs.show
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.AppNavigatorTransitionPlatformStyle
import com.michaelflisar.toolbox.app.utils.runApp
import com.michaelflisar.toolbox.demo.BuildKonfig
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.utils.JvmAppMeta
import com.michaelflisar.toolbox.utils.JvmFolderUtil
import com.michaelflisar.toolbox.utils.JvmUtil
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

fun main() {
    JvmUtil.runApp {
        app()
    }
}

class SomeClassFromApp

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
    val setup = Shared.createBaseAppSetup(
        prefs = Prefs(storageSettings),
        debugPrefs = DebugPrefs(storageDebug),
        isDebugBuild = appMeta.isDebug,
        fileLoggingSetup = FileLoggerSetup.SingleFile(
            folder = dataFolder
        )
    )
    val desktopSetup = DesktopAppSetup(
        prefs = DesktopPrefs(storageWindows),
        titleBarIcon = { light -> Shared.appIcon(light = light) }, // icon in title bar
        appIcon = { Shared.appIcon(light = true) },  // icon in windows toolbar
        // immer mit Standard Einstellungen starten!
        //rememberWindowState = false,
        minimumVisibleWidthPercentOnWindowRestore = .5f,
        minimumVisibleHeightPercentOnWindowRestore = .5f
    )
    DesktopApp.init(
        setup = setup,
        desktopSetup = desktopSetup
    )

    // 4) App Data ggf. updaten
    Shared.init(setup)

    // 5) Sonstige Initialisierungen
    val storageTest = DataStoreStorage.create(folder = dataFolder, name = "test")
    App.registerSingleton(TestPrefs(storageTest))

    // 6) Application
    application {

        Shared.Init()

        DesktopApplication(
            screen = Shared.page1
        ) { navigator ->

            // theme + root (drawer state, app state) are available now

            val dialogAppInfo = rememberDialogState()

            DesktopContainer(
                menuBar = {
                    DesktopMenuBar(
                        items = provideMenuItems()
                    )
                },
                statusBar = {
                    DesktopStatusBar(
                        onAppVersionClick = { dialogAppInfo.show() }
                    )
                },
                content = {
                    // Scaffold
                    Shared.Content(
                        navigator
                    ) {
                        AppNavigatorTransitionPlatformStyle(navigator)
                    }
                }
            )

            JvmAppInfoDialog(
                state = dialogAppInfo,
                appMeta = appMeta,
                appData = AppSetup.get().appData,
                developer = AppSetup.get().developer,
                title = { Text("About") }
            )
        }
    }
}

@Composable
private fun provideMenuItems(): List<MenuItem> {
    val errorDialogState = LocalErrorDialogState.current

    val testScope = rememberCoroutineScope()
    return DesktopAppDefaults.getDesktopMenuItems(
        customActions = listOf(
            MenuItem.group(
                text = "Test First Level Group",
                icon = Icons.Default.Folder,
                items = listOf(
                    MenuItem.item(
                        "Error Dialog Test",
                        Icons.Default.Error,
                        mnemonic = 'E'
                    ) {
                        errorDialogState.show("Test Error", "This is a test error message")
                    },
                    MenuItem.item(
                        "Crash Test",
                        Icons.Default.Error,
                        mnemonic = 'C'
                    ) {
                        // swing thread does not crash the app, so we need to throw the exception in a separate thread
                        testScope.launch {
                            throw RuntimeException("This is a test crash")
                        }
                    },
                    MenuItem.separator(text = "Group 1"),
                    MenuItem.item(
                        "Action 1",
                        Icons.Default.Folder
                    ) {
                        // ...
                    },
                    MenuItem.item(
                        "Action 2",
                        Icons.Default.Folder
                    ) {
                        // ...
                    },
                    MenuItem.separator(text = "Group 2"),
                    MenuItem.item(
                        "Action 3",
                        Icons.Default.Folder
                    ) {
                        // ...
                    },
                    MenuItem.item(
                        "Action 4",
                        Icons.Default.Folder
                    ) {
                        // ...
                    },
                )
            )
        )
    )
}
