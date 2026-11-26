package com.michaelflisar.toolbox.app

import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import io.github.vinceglb.filekit.FileKit

/**
 * initialises the setups
 *
 * + initialises FileKit with the activity
 */
fun App.init(
    setup: AppSetup,
    desktop: DesktopAppSetup,
) {
    initAppSetup(setup)
    DesktopAppSetup.init(desktop)
    FileKit.init(appId = AppSetup.get().appData.namespace)
}