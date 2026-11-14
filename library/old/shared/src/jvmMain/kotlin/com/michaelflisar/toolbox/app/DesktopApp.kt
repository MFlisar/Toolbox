package com.michaelflisar.toolbox.app

import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import com.michaelflisar.toolbox.app2.classes.DesktopAppSetup as DesktopAppSetup2
import io.github.vinceglb.filekit.FileKit

object DesktopApp {

    fun init(
        setup: AppSetup,
        desktopSetup: DesktopAppSetup,
    ) {
        App.init(setup)
        App.registerSingleton(desktopSetup)
        DesktopAppSetup2.initialize(
            DesktopAppSetup2(
                prefs = desktopSetup.prefs,
                appIcon = desktopSetup.appIcon,
                visible = desktopSetup.visible,
                resizable = desktopSetup.resizable,
                enabled = desktopSetup.enabled,
                focusable = desktopSetup.focusable,
                swingCompatMode = desktopSetup.swingCompatMode,
                ensureIsFullyOnScreen = desktopSetup.ensureIsFullyOnScreen,
                rememberWindowState = desktopSetup.rememberWindowState,
                minimumVisibleWidthPercentOnWindowRestore = desktopSetup.minimumVisibleWidthPercentOnWindowRestore,
                minimumVisibleHeightPercentOnWindowRestore = desktopSetup.minimumVisibleHeightPercentOnWindowRestore,
            )
        )
        FileKit.init(appId = setup.appData.namespace)
    }
}