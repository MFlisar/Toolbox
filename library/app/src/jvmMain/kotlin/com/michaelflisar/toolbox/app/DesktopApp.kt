package com.michaelflisar.toolbox.app

import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import io.github.vinceglb.filekit.FileKit

object DesktopApp {

    fun init(
        setup: AppSetup,
        desktopSetup: DesktopAppSetup,
    ) {
        App.init(setup)
        App.registerSingleton(desktopSetup)
        FileKit.init(appId = setup.packageName)
    }
}