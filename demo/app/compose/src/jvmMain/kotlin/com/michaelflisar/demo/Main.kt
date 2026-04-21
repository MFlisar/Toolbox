package com.michaelflisar.demo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.application
import com.michaelflisar.demo.pages.tests.TestPrefs
import com.michaelflisar.kotpreferences.storage.datastore.DataStoreStorage
import com.michaelflisar.kotpreferences.storage.datastore.create
import com.michaelflisar.toolbox.app.App
import com.michaelflisar.toolbox.app.DesktopApp
import com.michaelflisar.toolbox.app.DesktopAppDefaults
import com.michaelflisar.toolbox.app.DesktopApplication
import com.michaelflisar.toolbox.app.DesktopContainer
import com.michaelflisar.toolbox.app.DesktopStatusBar
import com.michaelflisar.toolbox.app.DesktopTitleBar
import com.michaelflisar.toolbox.app.DesktopTitleMenu
import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import com.michaelflisar.toolbox.app.debug.DebugPrefs
import com.michaelflisar.toolbox.app.features.dialogs.LocalErrorDialogState
import com.michaelflisar.toolbox.app.features.dialogs.show
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.AppNavigatorTransitionPlatformStyle
import com.michaelflisar.toolbox.app.features.preferences.DesktopPrefs
import com.michaelflisar.toolbox.app.utils.createFileLogger
import com.michaelflisar.toolbox.demo.BuildKonfig
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.utils.JvmFolderUtil
import com.michaelflisar.toolbox.utils.JvmUtil

@OptIn(ExperimentalMaterial3Api::class)
fun main() {

    // 1) Pfade
    val dataFolder = JvmFolderUtil.getPathForAppData(BuildKonfig.namespace)

    // 2) Storages erstellen
    val storageSettings = DataStoreStorage.create(folder = dataFolder, name = "settings")
    val storageDebug = DataStoreStorage.create(folder = dataFolder, name = "debug")
    val storageWindows = DataStoreStorage.create(folder = dataFolder, name = "windows")

    // 3) Setups erstellen
    val setup = Shared.createBaseAppSetup(
        prefs = Prefs(storageSettings),
        debugPrefs = DebugPrefs(storageDebug),
        isDebugBuild = JvmUtil.isDebug(),
        fileLogger = JvmUtil.createFileLogger(folder = dataFolder)
    )
    val desktopSetup = DesktopAppSetup(
        prefs = DesktopPrefs(storageWindows),
        titleBarIcon = { light -> Shared.appIcon(light = light) }, // icon in title bar
        appIcon = { Shared.appIcon(light = true) }  // icon in windows toolbar
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

            DesktopContainer(
                titleBar = {
                    DesktopTitleBar {
                        DesktopTitleMenu(
                            items = provideMenuItems()
                        )
                    }
                },
                statusBar = {
                    DesktopStatusBar()
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
        }
    }
}

@Composable
private fun provideMenuItems(): List<MenuItem> {
    val errorDialogState = LocalErrorDialogState.current
    return DesktopAppDefaults.getDesktopMenuItems(
        customActions = listOf(
            MenuItem.Group(
                text = "Test",
                icon = Icons.Default.Folder.toIconComposable(),
                items = listOf(
                    MenuItem.Item(
                        "Error Dialog Test",
                        Icons.Default.Error.toIconComposable()
                    ) {
                        errorDialogState.show("Test Error", "This is a test error message")
                    },
                    MenuItem.Separator(text = "Group 1"),
                    MenuItem.Item(
                        "Action 1",
                        Icons.Default.Folder.toIconComposable()
                    ) {
                        // ...
                    },
                    MenuItem.Item(
                        "Action 2",
                        Icons.Default.Folder.toIconComposable()
                    ) {
                        // ...
                    },
                    MenuItem.Separator(text = "Group 2"),
                    MenuItem.Item(
                        "Action 3",
                        Icons.Default.Folder.toIconComposable()
                    ) {
                        // ...
                    },
                    MenuItem.Item(
                        "Action 4",
                        Icons.Default.Folder.toIconComposable()
                    ) {
                        // ...
                    },
                )
            )
        )
    )
}
