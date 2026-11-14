package com.michaelflisar.toolbox.app

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.app.features.logging.LogManager

object App {

    /**
     * Initializes the App module with the provided [setup].
     *
     * Initializes logging and the changelog renderer. Subsequent calls are ignored;
     * the setup from the first call remains active.
     */
    internal fun initAppSetup(
        setup: AppSetup,
    ) {
        // Can be called early by the app and later again by the framework.
        if (!AppSetup.initialize(setup)) {
            // first setup has already initialized the logger, so we can log with L
            L.d { "App.init() called multiple times. Ignoring subsequent calls." }
            return
        }

        // logging
        LogManager.init()
    }
}